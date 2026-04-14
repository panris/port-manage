package com.portmanager.web.scanner;

import com.portmanager.web.model.PortInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mac系统端口扫描器
 * 使用lsof命令扫描端口
 */
@Component
public class MacPortScanner implements PortScanner {

    private static final Logger log = LoggerFactory.getLogger(MacPortScanner.class);

    @Value("${port-manage.scan.dev-process-keywords:idea,java,tace,claude,springboot}")
    private String devProcessKeywords;

    private static final Pattern PORT_PATTERN = Pattern.compile(":(\\d+)");

    @Override
    public List<PortInfo> scanPorts() {
        List<PortInfo> portList = new ArrayList<>();

        try {
            // 执行lsof命令: lsof -i -P -n | grep LISTEN
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", "lsof -i -P -n | grep LISTEN");
            pb.redirectErrorStream(true); // 合并错误流和输出流
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    log.debug("lsof output line {}: {}", lineCount, line);
                    PortInfo portInfo = parseLsofLine(line);
                    if (portInfo != null) {
                        portList.add(portInfo);
                        log.debug("Parsed port: {}", portInfo.getPort());
                    }
                }
                log.info("Scanned {} lines, found {} ports", lineCount, portList.size());
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.warn("lsof command exited with code: {}", exitCode);
            }
        } catch (Exception e) {
            log.error("Failed to scan ports on Mac: {}", e.getMessage(), e);
        }

        return portList;
    }

    @Override
    public PortInfo scanPort(int port) {
        try {
            // 扫描指定端口: lsof -i :{port} -P -n
            ProcessBuilder pb = new ProcessBuilder("sh", "-c",
                    String.format("lsof -i :%d -P -n | grep LISTEN", port));
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null) {
                    return parseLsofLine(line);
                }
            }

            process.waitFor();
        } catch (Exception e) {
            log.error("Failed to scan port {} on Mac: {}", port, e.getMessage());
        }

        return null;
    }

    /**
     * 解析lsof命令输出的一行
     * 示例格式: java    12345 user  123u  IPv4 0x1234      0t0  TCP *:8080 (LISTEN)
     */
    private PortInfo parseLsofLine(String line) {
        try {
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 9) {
                return null;
            }

            String processName = parts[0];
            Long pid = Long.parseLong(parts[1]);
            String user = parts[2];
            String protocol = parts[7]; // TCP or UDP
            String address = parts[8]; // *:8080

            // 提取端口号
            Matcher matcher = PORT_PATTERN.matcher(address);
            if (!matcher.find()) {
                return null;
            }
            int port = Integer.parseInt(matcher.group(1));

            // 获取完整命令行
            String commandLine = getProcessCommandLine(pid);

            // 识别端口类型
            String portType = PortTypeIdentifier.identifyPortType(port, processName, commandLine);

            // 识别进程类型
            String processType = PortTypeIdentifier.identifyProcessType(processName, commandLine);

            return PortInfo.builder()
                    .port(port)
                    .protocol(protocol)
                    .status("LISTENING")
                    .pid(pid)
                    .processName(processName)
                    .commandLine(commandLine)
                    .isDevelopmentProcess(isDevProcess(processName, commandLine))
                    .user(user)
                    .localAddress(address)
                    .portType(portType)
                    .processType(processType)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to parse lsof line: {}", line, e);
            return null;
        }
    }

    /**
     * 获取进程完整命令行
     */
    private String getProcessCommandLine(Long pid) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ps", "-p", pid.toString(), "-o", "command=");
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                return reader.readLine();
            }
        } catch (Exception e) {
            log.debug("Failed to get command line for PID {}: {}", pid, e.getMessage());
            return "";
        }
    }

    /**
     * 判断是否为开发进程
     */
    private boolean isDevProcess(String processName, String commandLine) {
        if (devProcessKeywords == null || devProcessKeywords.isEmpty()) {
            return false;
        }

        String searchText = (processName + " " + commandLine).toLowerCase();
        return Arrays.stream(devProcessKeywords.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .anyMatch(searchText::contains);
    }
}