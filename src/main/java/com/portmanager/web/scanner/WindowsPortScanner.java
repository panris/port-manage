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
 * Windows系统端口扫描器
 * 使用netstat命令扫描端口
 */
@Component
public class WindowsPortScanner implements PortScanner {

    private static final Logger log = LoggerFactory.getLogger(WindowsPortScanner.class);

    @Value("${port-manage.scan.dev-process-keywords:idea,java,tace,claude,springboot}")
    private String devProcessKeywords;

    private static final Pattern NETSTAT_PATTERN = Pattern.compile(
            "(TCP|UDP)\\s+([\\d.:]+):(\\d+)\\s+([\\d.:]+|\\*):(\\d+|\\*)\\s+(\\w+)\\s+(\\d+)");

    @Override
    public List<PortInfo> scanPorts() {
        List<PortInfo> portList = new ArrayList<>();

        try {
            // 执行netstat命令: netstat -ano | findstr LISTENING
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "netstat -ano | findstr LISTENING");
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    PortInfo portInfo = parseNetstatLine(line);
                    if (portInfo != null) {
                        portList.add(portInfo);
                    }
                }
            }

            process.waitFor();
        } catch (Exception e) {
            log.error("Failed to scan ports on Windows: {}", e.getMessage(), e);
        }

        return portList;
    }

    @Override
    public PortInfo scanPort(int port) {
        try {
            // 扫描指定端口
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c",
                    String.format("netstat -ano | findstr LISTENING | findstr :%d", port));
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null) {
                    return parseNetstatLine(line);
                }
            }

            process.waitFor();
        } catch (Exception e) {
            log.error("Failed to scan port {} on Windows: {}", port, e.getMessage());
        }

        return null;
    }

    /**
     * 解析netstat命令输出的一行
     * 示例格式: TCP    0.0.0.0:8080           0.0.0.0:0              LISTENING       12345
     */
    private PortInfo parseNetstatLine(String line) {
        try {
            Matcher matcher = NETSTAT_PATTERN.matcher(line.trim());
            if (!matcher.find()) {
                return null;
            }

            String protocol = matcher.group(1);
            String localAddress = matcher.group(2);
            int port = Integer.parseInt(matcher.group(3));
            String status = matcher.group(6);
            Long pid = Long.parseLong(matcher.group(7));

            // 获取进程名称和命令行
            String processName = getProcessName(pid);
            String commandLine = getProcessCommandLine(pid);

            return PortInfo.builder()
                    .port(port)
                    .protocol(protocol)
                    .status(status)
                    .pid(pid)
                    .processName(processName)
                    .commandLine(commandLine)
                    .isDevelopmentProcess(isDevProcess(processName, commandLine))
                    .localAddress(localAddress + ":" + port)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to parse netstat line: {}", line, e);
            return null;
        }
    }

    /**
     * 获取进程名称 (使用tasklist命令)
     */
    private String getProcessName(Long pid) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c",
                    String.format("tasklist /FI \"PID eq %d\" /FO CSV /NH", pid));
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null) {
                    // CSV格式: "processname.exe","12345",...
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        return parts[0].replace("\"", "");
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get process name for PID {}: {}", pid, e.getMessage());
        }

        return "Unknown";
    }

    /**
     * 获取进程完整命令行 (使用wmic命令)
     */
    private String getProcessCommandLine(Long pid) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c",
                    String.format("wmic process where processid=%d get commandline /format:list", pid));
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("CommandLine=")) {
                        return line.substring("CommandLine=".length()).trim();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get command line for PID {}: {}", pid, e.getMessage());
        }

        return "";
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