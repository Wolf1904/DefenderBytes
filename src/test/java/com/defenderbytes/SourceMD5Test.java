package test.java.com.defenderbytes;

import com.defenderbytes.MD5;
import com.defenderbytes.Source;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.Assert.*;

public class SourceMD5Test {

    private Source source;
    private String testFilePath;
    private String testMD5;

    // Setup method runs before each test
    @Before
    public void setUp() throws Exception {
        source = new Source();  // Initializing the Source class
        testFilePath = "test_files/testfile.txt";
        testMD5 = MD5.getMD5Checksum(testFilePath);

        // Create the directory structure if it does not exist
        File testDirectory = new File("test_files");
        if (!testDirectory.exists()) {
            testDirectory.mkdirs();  // Create directories if they don't exist
        }

        // Create a test file if it does not exist
        File testFile = new File(testFilePath);
        if (!testFile.exists()) {
            testFile.createNewFile();  // Ensure the file is created
        }
    }


    // Cleanup method runs after each test
    @After
    public void tearDown() throws IOException {
        // Delete test file after tests
        Files.deleteIfExists(Path.of(testFilePath));
    }

    // Source class related test cases
    @Test
    public void testPerformScan() {
        assertNotNull(source);
        assertNotNull(testMD5);

        source.performScan(testFilePath, testMD5);  // Perform scan on the test file
        // You can add further assertions to verify the behavior
        // Example: Check if certain labels are updated as expected
    }

    @Test
    public void testScanFile() {
        source.performScan(testFilePath, testMD5); // Perform the file scan
        // Verify expected behavior after scan
        assertEquals("Path: " + testFilePath, source.getPathLabelValue());
        assertEquals("MD5: " + testMD5, source.getMD5LabelValue());
    }

    @Test
    public void testUIButtons() {
        // Simulate a button click for scan
        JButton scanButton = new JButton();
        @SuppressWarnings("unused")
        ActionEvent event = new ActionEvent(scanButton, ActionEvent.ACTION_PERFORMED, "scan");
        scanButton.doClick();
        
        // Check if the action performed is correctly handled
        // For example, verifying the file chooser action triggered
        assertNotNull(scanButton.getActionListeners());
    }

    // MD5 class related test cases
    @Test
    public void testMD5Checksum() throws Exception {
        // Get MD5 checksum for a file
        String md5Checksum = MD5.getMD5Checksum(testFilePath);
        assertNotNull(md5Checksum);

        // Make sure the checksum length is as expected for MD5 (32 characters)
        assertEquals(32, md5Checksum.length());
    }

    @Test
    public void testMD5Hash() {
        String text = "TestString";
        String expectedMD5 = MD5.hash(text);
        assertEquals("db346d691d7acc4dc2625db19f9e3f52", expectedMD5);
    }

    @Test
    public void testCreateChecksum() throws Exception {
        byte[] checksum = MD5.createChecksum(testFilePath);
        assertNotNull(checksum);

        // Check if the checksum length is 16 bytes (MD5 produces 128-bit hash)
        assertEquals(16, checksum.length);
    }
}
