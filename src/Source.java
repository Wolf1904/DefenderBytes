// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.io.*;
// import java.nio.file.*;
// import java.util.HashSet;
// import java.util.Set;

// public class Source {
//     private static final String scanlogs_FILE = "text_files/scanlogs.txt";
//     private static final String INFECTED_FILE = "text_files/infected.txt";
//     private static final String HASH_CODES_FILE = "text_files/hashcodes.txt";
//     private JFrame frame;
//     private JLabel pathLabel, SHA256Label, statusLabel, scanningLabel;
//     private Set<String> hashSet;

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new Source().createAndShowGUI());
//     }

//     private void createAndShowGUI() {
//         frame = new JFrame("DefenderBytes");
//         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         frame.setSize(800, 600);
//         frame.setLayout(null);
    
//         JButton scanButton = new JButton("Scan");
//         scanButton.setBounds(20, 20, 100, 80);
//         scanButton.addActionListener(e -> onScan());
    
//         JButton fullScanButton = new JButton("FullScan");
//         fullScanButton.setBounds(20, 120, 100, 80);
//         fullScanButton.addActionListener(e -> new Thread(this::scanAll).start());
    
//         JButton scanlogsButton = new JButton("scanlogs");
//         scanlogsButton.setBounds(20, 220, 100, 80);
//         scanlogsButton.addActionListener(e -> {
//             try {
//                 Desktop.getDesktop().open(new File(scanlogs_FILE));
//             } catch (IOException ex) {
//                 ex.printStackTrace();
//             }
//         });
    
//         JButton infectedButton = new JButton("Infected");
//         infectedButton.setBounds(20, 320, 100, 80);
//         infectedButton.addActionListener(e -> {
//             try {
//                 Desktop.getDesktop().open(new File(INFECTED_FILE));
//             } catch (IOException ex) {
//                 ex.printStackTrace();
//             }
//         });
    
//         frame.add(scanButton);
//         frame.add(fullScanButton);
//         frame.add(scanlogsButton);
//         frame.add(infectedButton);
    
//         pathLabel = new JLabel("Path: ...");
//         pathLabel.setBounds(170, 20, 700, 20);
//         frame.add(pathLabel);
    
//         SHA256Label = new JLabel("SHA256: ...");
//         SHA256Label.setBounds(170, 40, 700, 20);
//         frame.add(SHA256Label);
    
//         statusLabel = new JLabel("Status: N/A");
//         statusLabel.setBounds(170, 60, 700, 20);
//         frame.add(statusLabel);
    
//         scanningLabel = new JLabel("Scanning: ...");
//         scanningLabel.setBounds(170, 80, 700, 20);
//         frame.add(scanningLabel);
    
//         // Load hash codes into a Set
//         hashSet = loadHashes(HASH_CODES_FILE);
    
//         frame.setVisible(true);
//     }
    

//     private void onScan() {
//         JFileChooser fileChooser = new JFileChooser();
//         fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//         int returnValue = fileChooser.showOpenDialog(null);
//         if (returnValue == JFileChooser.APPROVE_OPTION) {
//             File selectedFile = fileChooser.getSelectedFile();
//             if (selectedFile.isDirectory()) {
//                 // Scan the entire directory
//                 scanDirectory(selectedFile.toPath());
//             } else {
//                 // Scan the single file
//                 String SHA256Value;
//                 try {
//                     SHA256Value = SHA256.getSHA256Checksum(selectedFile.getAbsolutePath());
//                     scanFile(selectedFile.getAbsolutePath(), SHA256Value);
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             }
//         }
//     }

//     private void scanFile(String filePath, String SHA256Value) {
//         boolean found = hashSet.contains(SHA256Value);

//         pathLabel.setText("Path: " + filePath);
//         SHA256Label.setText("SHA256: " + SHA256Value);
//         statusLabel.setText("Status: " + (found ? "Infected" : "Clean"));
//         scanningLabel.setText("Scanning: Done");

//         saveScanscanlogs(filePath, SHA256Value, found ? "Infected" : "Clean");
//     }

//     private void scanDirectory(Path directoryPath) {
//         try {
//             Files.walk(directoryPath) // Recursively walk through the directory
//                 .filter(Files::isRegularFile) // Only process files
//                 .forEach(path -> {
//                     try {
//                         String SHA256Value = SHA256.getSHA256Checksum(path.toString());
//                         scanFile(path.toString(), SHA256Value);
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
//         // Determine the starting path based on the OS
//         Path startPath;
//         if (System.getProperty("os.name").startsWith("Windows")) {
//             startPath = Paths.get("C:\\");
//         } else {
//             startPath = Paths.get("/");
//         }
    
//         try {
//             Files.walk(startPath) // Recursively include all files and directories
//                 .filter(Files::isRegularFile) // Only process files
//                 .forEach(path -> {
//                     try {
//                         String SHA256Value = SHA256.getSHA256Checksum(path.toString());
//                         scanFile(path.toString(), SHA256Value);
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

//     private void saveScanscanlogs(String filePath, String SHA256Value, String status) {
//         // Log the scanlogs in scanlogs.txt
//         try (BufferedWriter scanlogsWriter = new BufferedWriter(new FileWriter(scanlogs_FILE, true))) {
//             scanlogsWriter.write(filePath + " " + SHA256Value + " " + status + "\n");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         // If the file is infected, log it in infected.txt as well
//         if (status.equals("Infected")) {
//             try (BufferedWriter infectedWriter = new BufferedWriter(new FileWriter(INFECTED_FILE, true))) {
//                 infectedWriter.write(filePath + " " + SHA256Value + " " + status + "\n");
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
    private JLabel pathLabel, SHA256Label, statusLabel, scanningLabel;
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
        SHA256Label = new JLabel("SHA256: ...");
        gbc.gridy = 3;
        frame.add(SHA256Label, gbc);

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
                    String SHA256Value = SHA256.getSHA256Checksum(selectedFile.getAbsolutePath());
                    scanFile(selectedFile.getAbsolutePath(), SHA256Value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scanFile(String filePath, String SHA256Value) {
        boolean found = hashSet.contains(SHA256Value);

        pathLabel.setText("Path: " + filePath);
        SHA256Label.setText("SHA256: " + SHA256Value);
        statusLabel.setText("Status: " + (found ? "Infected" : "Clean"));
        scanningLabel.setText("Scanning: Done");

        // Log scan details in text area
        logArea.append("File: " + filePath + " | SHA256: " + SHA256Value + " | Status: " + (found ? "Infected" : "Clean") + "\n");

        saveScanLogs(filePath, SHA256Value, found ? "Infected" : "Clean");
    }

    private void scanDirectory(Path directoryPath) {
        try {
            Files.walk(directoryPath)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        String SHA256Value = SHA256.getSHA256Checksum(path.toString());
                        scanFile(path.toString(), SHA256Value);
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
                        String SHA256Value = SHA256.getSHA256Checksum(path.toString());
                        scanFile(path.toString(), SHA256Value);
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

    private void saveScanLogs(String filePath, String SHA256Value, String status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCANLOGS_FILE, true))) {
            writer.write(filePath + " " + SHA256Value + " " + status + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (status.equals("Infected")) {
            try (BufferedWriter infectedWriter = new BufferedWriter(new FileWriter(INFECTED_FILE, true))) {
                infectedWriter.write(filePath + " " + SHA256Value + " " + status + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
