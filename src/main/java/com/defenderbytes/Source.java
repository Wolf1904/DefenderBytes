package com.defenderbytes;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class Source {
    private static final String SCANLOGS_FILE = "text_files/scanlogs.txt";
    private static final String INFECTED_FILE = "text_files/infected.txt";
    private static final String HASH_CODES_FILE = "text_files/hashcodes.txt";
    private static final String STATUS_INFECTED = "Infected";
    private static final String STATUS_CLEAN = "Clean";
    private static final String SCANNING_DONE = "Scanning: Done";
    private static final String FILES_DETECT = " | Infected Files Detected: ";
    private static final String FILES_SCANNED = " | Total Files Scanned: ";
    private static final String INFECTED_PER = " | Infected Percentage: ";

    public JLabel pathLabel;
    public JLabel labelMD5;
    public JLabel statusLabel;
    public JLabel scanningLabel;
    public JTextArea logArea;
    private Set<String> hashSet;
    private int infectedCount = 0;
    private int totalFilesScanned = 0;

    // Constructor
    public Source() {
        // Initialize hashSet in the constructor
        this.hashSet = loadHashes(HASH_CODES_FILE);
        initializeUI();
        logArea = new JTextArea();
    }

    public void initializeUI() {
        this.pathLabel = new JLabel();
        this.labelMD5 = new JLabel();
        this.statusLabel = new JLabel("Status: N/A");
        this.scanningLabel = new JLabel("Scanning: ...");
    }

    public String getPathLabelValue() {
        return pathLabel.getText();
    }
    
    public String getMD5LabelValue() {
        return labelMD5.getText();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Source().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // Create frame as a local variable
        JFrame mainFrame = new JFrame("DefenderBytes Antivirus Scanner");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 800);
        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Set margins
    
        // Title Label
        JLabel titleLabel = new JLabel("DefenderBytes Antivirus Scanner");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainFrame.add(titleLabel, gbc);
    
        // Scan Button
        JButton scanButton = new JButton("Scan");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        scanButton.setPreferredSize(new Dimension(120, 40));
        scanButton.addActionListener(e -> onScan());
        mainFrame.add(scanButton, gbc);
    
        // Full Scan Button
        JButton fullScanButton = new JButton("Full Scan");
        gbc.gridx = 1;
        fullScanButton.setPreferredSize(new Dimension(120, 40));
        fullScanButton.addActionListener(e -> new Thread(this::scanAll).start());
        mainFrame.add(fullScanButton, gbc);
    
        // Scan Logs Button
        JButton scanlogsButton = new JButton("Scan Logs");
        gbc.gridx = 2;
        scanlogsButton.setPreferredSize(new Dimension(120, 40));
        scanlogsButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(SCANLOGS_FILE));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        mainFrame.add(scanlogsButton, gbc);
    
        // Infected Files Button
        JButton infectedButton = new JButton("Infected Files");
        gbc.gridx = 3;
        infectedButton.setPreferredSize(new Dimension(120, 40));
        infectedButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(INFECTED_FILE));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        mainFrame.add(infectedButton, gbc);
    
        // Path Label
        pathLabel = new JLabel("Path: ...");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        mainFrame.add(pathLabel, gbc);
    
        // MD5 Label
        labelMD5 = new JLabel("MD5: ...");
        gbc.gridy = 3;
        mainFrame.add(labelMD5, gbc);
    
        // Status Label
        statusLabel = new JLabel("Status: N/A");
        gbc.gridy = 4;
        mainFrame.add(statusLabel, gbc);
    
        // Scanning Status Label
        scanningLabel = new JLabel("Scanning: ...");
        gbc.gridy = 5;
        mainFrame.add(scanningLabel, gbc);
    
        // Log Area (Text Area for logs)
        logArea = new JTextArea(10, 40);  // 10 rows and 40 columns
        logArea.setEditable(false);  // Set to not editable by the user
        JScrollPane scrollPane = new JScrollPane(logArea);  // Add scroll bar
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;  // Make it fill both vertically and horizontally
        mainFrame.add(scrollPane, gbc);
    
        // Load hash codes into a Set
        hashSet = loadHashes(HASH_CODES_FILE);
    
        // Add shutdown hook to clean scanlogs.txt
        Runtime.getRuntime().addShutdownHook(new Thread(this::clearScanLogs));
    
        // Show frame
        mainFrame.setVisible(true);
    }


    public void performScan(String filePath, String valueMD5) {
        if (logArea != null) {
            logArea.append("Some log message");
        }
        // Call the original scanFile method here
        scanFile(filePath, valueMD5);
    }    

    private void onScan() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.isDirectory()) {
                scanDirectory(selectedFile.toPath());
            } else {
                try {
                    String valueMD5 = MD5.getMD5Checksum(selectedFile.getAbsolutePath());
                    scanFile(selectedFile.getAbsolutePath(), valueMD5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scanFile(String filePath, String valueMD5) {
        // Check if the file exists
        File file = new File(filePath);
        if (!file.exists()) {
            statusLabel.setText("File not found: " + filePath); // Update this line
            return; // Exit early if the file does not exist
        }

        totalFilesScanned++;
        boolean found = hashSet.contains(valueMD5);

        pathLabel.setText("Path: " + filePath);
        labelMD5.setText("MD5: " + valueMD5);
        statusLabel.setText("Status: " + (found ? STATUS_INFECTED : STATUS_CLEAN));
        // scanningLabel.setText(SCANNING_DONE);

        // Increment infectedCount if the file is infected
        if (found) {
            infectedCount++;
            logArea.append("File infected. Incrementing infectedCount to: " + infectedCount + "\n");
        }

        // Log scan details in text area
        logArea.append("File: " + filePath + " | MD5: " + valueMD5 + " | Status: " + (found ? STATUS_INFECTED : STATUS_CLEAN) + "\n");

        saveScanLogs(filePath, valueMD5, found ? STATUS_INFECTED : STATUS_CLEAN);

        // Update status after scanning file
        if (totalFilesScanned > 0) {
            statusLabel.setText("Scan complete."); // Add this line
        }
    }

    private void scanDirectory(Path directoryPath) {
        infectedCount = 0;
        try {
            Files.walk(directoryPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String valueMD5 = MD5.getMD5Checksum(path.toString());
                        scanFile(path.toString(), valueMD5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            double infectedPercentage = (totalFilesScanned == 0) ? 0 : ((double) infectedCount / totalFilesScanned) * 100;
            scanningLabel.setText(SCANNING_DONE + FILES_DETECT + infectedCount + FILES_SCANNED + totalFilesScanned + INFECTED_PER + String.format("%.2f", infectedPercentage) + "%");
            logArea.append("Scanning completed. Infected Files Detected: " + infectedCount + "\n");

            // Update status after scanning directory
            if (totalFilesScanned > 0) {
                statusLabel.setText("Scan complete."); // Add this line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scanAll() {
        infectedCount = 0;
        Path startPath = System.getProperty("os.name").startsWith("Windows") ? Paths.get("C:\\") : Paths.get("/");
        try {
            Files.walk(startPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String valueMD5 = MD5.getMD5Checksum(path.toString());
                        scanFile(path.toString(), valueMD5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            double infectedPercentage = (totalFilesScanned == 0) ? 0 : ((double) infectedCount / totalFilesScanned) * 100;
            scanningLabel.setText(SCANNING_DONE + FILES_DETECT + infectedCount + FILES_SCANNED + totalFilesScanned + INFECTED_PER + String.format("%.2f", infectedPercentage) + "%");
            logArea.append("Full scan completed. Infected Files Detected: " + infectedCount + "\n");

            // Update status after scanning all
            if (totalFilesScanned > 0) {
                statusLabel.setText("Scan complete."); // Add this line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<String> loadHashes(String filePath) {
        Set<String> loadedHashes = new HashSet<>();  // Renamed variable
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                loadedHashes.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedHashes;
    }
    

    private void saveScanLogs(String filePath, String valueMD5, String status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCANLOGS_FILE, true))) {
            writer.write(filePath + " " + valueMD5 + " " + status + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (status.equals(STATUS_INFECTED)) {
            try (BufferedWriter infectedWriter = new BufferedWriter(new FileWriter(INFECTED_FILE, true))) {
                infectedWriter.write(filePath + " " + valueMD5 + " " + status + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearScanLogs() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCANLOGS_FILE))) {
            // Simply open the file in write mode to clear its contents
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
