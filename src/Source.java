// import javax.swing.*;
// import java.awt.*;
// import java.io.*;
// import java.nio.file.*;
// import java.util.HashSet;
// import java.util.Set;

// public class Source {
//     private static final String SCANLOGS_FILE = "text_files/scanlogs.txt";
//     private static final String INFECTED_FILE = "text_files/infected.txt";
//     private static final String HASH_CODES_FILE = "text_files/hashcodes.txt";
//     private JFrame frame;
//     private JLabel pathLabel, LabelSHA256, statusLabel, scanningLabel;
//     private JTextArea logArea;
//     private Set<String> hashSet;

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new Source().createAndShowGUI());
//     }

//     private void createAndShowGUI() {
//         // Create frame
//         frame = new JFrame("DefenderBytes Antivirus Scanner");
//         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         frame.setSize(1000, 800);
//         frame.setLayout(new GridBagLayout());
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(10, 10, 10, 10);  // Set margins

//         // Title Label
//         JLabel titleLabel = new JLabel("DefenderBytes Antivirus Scanner");
//         titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
//         titleLabel.setHorizontalAlignment(JLabel.CENTER);
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         gbc.gridwidth = 4;
//         frame.add(titleLabel, gbc);

//         // Scan Button
//         JButton scanButton = new JButton("Scan");
//         gbc.gridx = 0;
//         gbc.gridy = 1;
//         gbc.gridwidth = 1;
//         scanButton.setPreferredSize(new Dimension(120, 40));
//         scanButton.addActionListener(e -> onScan());
//         frame.add(scanButton, gbc);

//         // Full Scan Button
//         JButton fullScanButton = new JButton("Full Scan");
//         gbc.gridx = 1;
//         fullScanButton.setPreferredSize(new Dimension(120, 40));
//         fullScanButton.addActionListener(e -> new Thread(this::scanAll).start());
//         frame.add(fullScanButton, gbc);

//         // Scan Logs Button
//         JButton scanlogsButton = new JButton("Scan Logs");
//         gbc.gridx = 2;
//         scanlogsButton.setPreferredSize(new Dimension(120, 40));
//         scanlogsButton.addActionListener(e -> {
//             try {
//                 Desktop.getDesktop().open(new File(SCANLOGS_FILE));
//             } catch (IOException ex) {
//                 ex.printStackTrace();
//             }
//         });
//         frame.add(scanlogsButton, gbc);

//         // Infected Files Button
//         JButton infectedButton = new JButton("Infected Files");
//         gbc.gridx = 3;
//         infectedButton.setPreferredSize(new Dimension(120, 40));
//         infectedButton.addActionListener(e -> {
//             try {
//                 Desktop.getDesktop().open(new File(INFECTED_FILE));
//             } catch (IOException ex) {
//                 ex.printStackTrace();
//             }
//         });
//         frame.add(infectedButton, gbc);

//         // Path Label
//         pathLabel = new JLabel("Path: ...");
//         gbc.gridx = 0;
//         gbc.gridy = 2;
//         gbc.gridwidth = 4;
//         frame.add(pathLabel, gbc);

//         // SHA256 Label
//         LabelSHA256 = new JLabel("SHA256: ...");
//         gbc.gridy = 3;
//         frame.add(LabelSHA256, gbc);

//         // Status Label
//         statusLabel = new JLabel("Status: N/A");
//         gbc.gridy = 4;
//         frame.add(statusLabel, gbc);

//         // Scanning Status Label
//         scanningLabel = new JLabel("Scanning: ...");
//         gbc.gridy = 5;
//         frame.add(scanningLabel, gbc);

//         // Log Area (Text Area for logs)
//         logArea = new JTextArea(10, 40);  // 10 rows and 40 columns
//         logArea.setEditable(false);  // Set to not editable by the user
//         JScrollPane scrollPane = new JScrollPane(logArea);  // Add scroll bar
//         gbc.gridx = 0;
//         gbc.gridy = 6;
//         gbc.gridwidth = 4;
//         gbc.fill = GridBagConstraints.BOTH;  // Make it fill both vertically and horizontally
//         frame.add(scrollPane, gbc);

//         // Load hash codes into a Set
//         hashSet = loadHashes(HASH_CODES_FILE);

//         // Show frame
//         frame.setVisible(true);
//     }

//     private void onScan() {
//         JFileChooser fileChooser = new JFileChooser();
//         fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//         int returnValue = fileChooser.showOpenDialog(null);
//         if (returnValue == JFileChooser.APPROVE_OPTION) {
//             File selectedFile = fileChooser.getSelectedFile();
//             if (selectedFile.isDirectory()) {
//                 scanDirectory(selectedFile.toPath());
//             } else {
//                 try {
//                     String ValueSHA256 = SHA256.getSHA256Checksum(selectedFile.getAbsolutePath());
//                     scanFile(selectedFile.getAbsolutePath(), ValueSHA256);
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             }
//         }
//     }

//     private void scanFile(String filePath, String ValueSHA256) {
//         boolean found = hashSet.contains(ValueSHA256);

//         pathLabel.setText("Path: " + filePath);
//         LabelSHA256.setText("SHA256: " + ValueSHA256);
//         statusLabel.setText("Status: " + (found ? "Infected" : "Clean"));
//         scanningLabel.setText("Scanning: Done");

//         // Log scan details in text area
//         logArea.append("File: " + filePath + " | SHA256: " + ValueSHA256 + " | Status: " + (found ? "Infected" : "Clean") + "\n");

//         saveScanLogs(filePath, ValueSHA256, found ? "Infected" : "Clean");
//     }

//     private void scanDirectory(Path directoryPath) {
//         try {
//             Files.walk(directoryPath)
//                 .filter(Files::isRegularFile)
//                 .forEach(path -> {
//                     try {
//                         String ValueSHA256 = SHA256.getSHA256Checksum(path.toString());
//                         scanFile(path.toString(), ValueSHA256);
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     }
//                 });
//             scanningLabel.setText("Scanning: Done");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private void scanAll() {
//         Path startPath = System.getProperty("os.name").startsWith("Windows") ? Paths.get("C:\\") : Paths.get("/");
//         try {
//             Files.walk(startPath)
//                 .filter(Files::isRegularFile)
//                 .forEach(path -> {
//                     try {
//                         String ValueSHA256 = SHA256.getSHA256Checksum(path.toString());
//                         scanFile(path.toString(), ValueSHA256);
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     }
//                 });
//             scanningLabel.setText("Scanning: Done");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private Set<String> loadHashes(String filePath) {
//         Set<String> hashSet = new HashSet<>();
//         try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//             String line;
//             while ((line = reader.readLine()) != null) {
//                 hashSet.add(line.trim());
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         return hashSet;
//     }

//     private void saveScanLogs(String filePath, String ValueSHA256, String status) {
//         try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCANLOGS_FILE, true))) {
//             writer.write(filePath + " " + ValueSHA256 + " " + status + "\n");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         if (status.equals("Infected")) {
//             try (BufferedWriter infectedWriter = new BufferedWriter(new FileWriter(INFECTED_FILE, true))) {
//                 infectedWriter.write(filePath + " " + ValueSHA256 + " " + status + "\n");
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
// }

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
    private JFrame frame;
    private JLabel pathLabel;
    private JLabel LabelSHA256;
    private JLabel statusLabel;
    private JLabel scanningLabel;
    private JTextArea logArea;
    private Set<String> hashSet;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Source().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // Create frame
        frame = new JFrame("DefenderBytes Antivirus Scanner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Set margins

        // Title Label
        JLabel titleLabel = new JLabel("DefenderBytes Antivirus Scanner");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        frame.add(titleLabel, gbc);

        // Scan Button
        JButton scanButton = new JButton("Scan");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        scanButton.setPreferredSize(new Dimension(120, 40));
        scanButton.addActionListener(e -> onScan());
        frame.add(scanButton, gbc);

        // Full Scan Button
        JButton fullScanButton = new JButton("Full Scan");
        gbc.gridx = 1;
        fullScanButton.setPreferredSize(new Dimension(120, 40));
        fullScanButton.addActionListener(e -> new Thread(this::scanAll).start());
        frame.add(fullScanButton, gbc);

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
        frame.add(scanlogsButton, gbc);

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
        frame.add(infectedButton, gbc);

        // Path Label
        pathLabel = new JLabel("Path: ...");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        frame.add(pathLabel, gbc);

        // SHA256 Label
        LabelSHA256 = new JLabel("SHA256: ...");
        gbc.gridy = 3;
        frame.add(LabelSHA256, gbc);

        // Status Label
        statusLabel = new JLabel("Status: N/A");
        gbc.gridy = 4;
        frame.add(statusLabel, gbc);

        // Scanning Status Label
        scanningLabel = new JLabel("Scanning: ...");
        gbc.gridy = 5;
        frame.add(scanningLabel, gbc);

        // Log Area (Text Area for logs)
        logArea = new JTextArea(10, 40);  // 10 rows and 40 columns
        logArea.setEditable(false);  // Set to not editable by the user
        JScrollPane scrollPane = new JScrollPane(logArea);  // Add scroll bar
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;  // Make it fill both vertically and horizontally
        frame.add(scrollPane, gbc);

        // Load hash codes into a Set
        hashSet = loadHashes(HASH_CODES_FILE);

        // Add shutdown hook to clean scanlogs.txt
        Runtime.getRuntime().addShutdownHook(new Thread(this::clearScanLogs));

        // Show frame
        frame.setVisible(true);
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
                    String ValueSHA256 = SHA256.getSHA256Checksum(selectedFile.getAbsolutePath());
                    scanFile(selectedFile.getAbsolutePath(), ValueSHA256);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scanFile(String filePath, String ValueSHA256) {
        boolean found = hashSet.contains(ValueSHA256);

        pathLabel.setText("Path: " + filePath);
        LabelSHA256.setText("SHA256: " + ValueSHA256);
        statusLabel.setText("Status: " + (found ? "Infected" : "Clean"));
        scanningLabel.setText("Scanning: Done");

        // Log scan details in text area
        logArea.append("File: " + filePath + " | SHA256: " + ValueSHA256 + " | Status: " + (found ? "Infected" : "Clean") + "\n");

        saveScanLogs(filePath, ValueSHA256, found ? "Infected" : "Clean");
    }

    private void scanDirectory(Path directoryPath) {
        try {
            Files.walk(directoryPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String ValueSHA256 = SHA256.getSHA256Checksum(path.toString());
                        scanFile(path.toString(), ValueSHA256);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            scanningLabel.setText("Scanning: Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scanAll() {
        Path startPath = System.getProperty("os.name").startsWith("Windows") ? Paths.get("C:\\") : Paths.get("/");
        try {
            Files.walk(startPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String ValueSHA256 = SHA256.getSHA256Checksum(path.toString());
                        scanFile(path.toString(), ValueSHA256);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            scanningLabel.setText("Scanning: Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<String> loadHashes(String filePath) {
        Set<String> hashSet = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                hashSet.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashSet;
    }

    private void saveScanLogs(String filePath, String ValueSHA256, String status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCANLOGS_FILE, true))) {
            writer.write(filePath + " " + ValueSHA256 + " " + status + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (status.equals("Infected")) {
            try (BufferedWriter infectedWriter = new BufferedWriter(new FileWriter(INFECTED_FILE, true))) {
                infectedWriter.write(filePath + " " + ValueSHA256 + " " + status + "\n");
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
