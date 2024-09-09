DefenderBytes Antivirus Scanner
===============================

Overview
---------

DefenderBytes is a simple antivirus scanner application developed in Java. It allows users to scan files and directories for known malware by checking their SHA256 hashes against a list of known malicious hash codes. The application features a graphical user interface (GUI) built with Swing, providing a user-friendly way to perform scans, view logs, and check infected files.

Features
--------

- **Scan**: Allows the user to scan a selected file or directory.
- **Full Scan**: Performs a comprehensive scan of the entire file system.
- **Scan Logs**: Opens a file containing the logs of previous scans.
- **Infected Files**: Opens a file listing detected infected files.

Prerequisites
-------------

- Java 11 or later

Project Structure
------------------

- `Source.java`: The main application file containing the GUI and scanning logic.
- `SHA256.java`: Utility class for calculating SHA256 checksums of files.
- `text_files/`:
  - `scanlogs.txt`: File where scan logs are saved.
  - `infected.txt`: File where detected infected files are listed.
  - `hashcodes.txt`: File containing known malicious hash codes.

Setup
-----

1. **Clone the Repository**
    ```bash
   git clone https://github.com/yourusername/defenderbytes.git
    
   cd defenderbytes
    ```

3. **Build the Project**

   If you are using Gradle, you can build the project with:

   ```bash
   gradle build
   ```

   If you are using an IDE like IntelliJ IDEA or Eclipse, you can import the project and build it from within the IDE.

4. **Run the Application**

   After building, run the application using:

   ```bash
   java -cp build/libs/defenderbytes.jar Source
   ```

   Ensure that the `text_files/` directory and necessary files (`scanlogs.txt`, `infected.txt`, `hashcodes.txt`) are present in the working directory.

Usage
-----

1. **Scan a File or Directory**
   - Click the "Scan" button to select a file or directory to scan.
   - The application will display the results including the file path, SHA256 checksum, and infection status.

2. **Full Scan**
   - Click the "Full Scan" button to start scanning the entire file system.

3. **View Scan Logs**
   - Click the "Scan Logs" button to open the file containing the logs of previous scans.

4. **View Infected Files**
   - Click the "Infected Files" button to open the file listing detected infected files.

Contributing
------------

If you would like to contribute to this project, please fork the repository and submit a pull request with your changes. For major changes, please open an issue first to discuss the change.

License
-------

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Notes:
---------
- **Update Links and Contact Information**: Make sure to replace placeholders such as `https://github.com/yourusername/defenderbytes.git` and `your.email@example.com` with the actual repository URL and your contact email.
- **Add LICENSE File**: Ensure that you include a `LICENSE` file if you mention a license in your `README.md`.
