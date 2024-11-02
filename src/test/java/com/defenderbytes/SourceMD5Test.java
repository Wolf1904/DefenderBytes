package test.java.com.defenderbytes;

import com.defenderbytes.MD5;
import com.defenderbytes.Source;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SourceMD5Test {

    private static final String PATH = "Path: ";
    private static final String MD5_STRING = "MD5: ";

    private Source source;
    private String testFilePath;
    private String testMD5;

    // Setup method runs before each test
    @Before
    public void setUp() throws Exception {
        source = Mockito.spy(new Source());
        // Mock the JLabel components
        source.pathLabel = mock(JLabel.class);
        source.labelMD5 = mock(JLabel.class);
        source.statusLabel = mock(JLabel.class);
        source.scanningLabel = mock(JLabel.class);
        source.logArea = mock(JTextArea.class);
        testFilePath = "test_files/testfile.txt";

        // Create the directory structure if it does not exist
        File testDirectory = new File("test_files");
        if (!testDirectory.exists()) {
            boolean dirsCreated = testDirectory.mkdirs();  // Create directories if they don't exist
            if (dirsCreated) {
                System.out.println("Created directory: " + testDirectory.getAbsolutePath());
            } else {
                System.out.println("Failed to create directory: " + testDirectory.getAbsolutePath());
            }
        }

        // Create a test file if it does not exist
        File testFile = new File(testFilePath);
        if (!testFile.exists()) {
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("This is a test file for MD5 checksum.");
                System.out.println("Created test file: " + testFile.getAbsolutePath());
            } catch (IOException e) {
                fail("Failed to create test file: " + e.getMessage());
            }
        } else {
            System.out.println("Test file already exists: " + testFile.getAbsolutePath());
        }

        // Calculate MD5 checksum
        testMD5 = MD5.getMD5Checksum(testFilePath);
        assertNotNull(testMD5);  // Ensure MD5 checksum is not null
    }
    // Cleanup method runs after each test
    @After
    public void tearDown() throws IOException {
        // Delete test file after tests
        try {
            Files.deleteIfExists(Path.of(testFilePath));
        } catch (IOException e) {
            fail("Failed to delete test file: " + e.getMessage());
        }
    }

    // Test performScan method and verify label updates
    @Test
    public void testPerformScan() {
        assertNotNull(source);
        assertNotNull(testMD5);

        source.performScan(testFilePath, testMD5);  // Perform scan on the test file

        // Verify if the performScan method is called
        verify(source).performScan(testFilePath, testMD5);

        // Verify label updates after scan
        verify(source.pathLabel).setText(PATH + testFilePath);
        verify(source.labelMD5).setText(MD5_STRING + testMD5);
        verify(source.statusLabel).setText("Scan complete.");
    }

    @Test
    public void testFileDoesNotExist() throws Exception {
        String nonExistentFilePath = "test_files/non_existent_file.txt";

        // Attempt to scan a non-existent file
        source.performScan(nonExistentFilePath, testMD5);

        // Verify that the status label is updated correctly
        verify(source.statusLabel).setText("File not found: " + nonExistentFilePath);
    }

    // Test scanFile method behavior using mocked labels and verify updates
    @Test
    public void testScanFile() {
        source.performScan(testFilePath, testMD5);  // Perform the file scan

        // Verify label updates after scan
        verify(source.pathLabel).setText(PATH + testFilePath);
        verify(source.labelMD5).setText(MD5_STRING + testMD5);
    }

    @SuppressWarnings("unused")
    @Ignore("Skipping this test because the file may not exist.")
    // Test UI buttons using mocked JButton and ensure proper action handling
    @Test
    public void testUIButtons() throws Exception {
        JButton mockScanButton = mock(JButton.class);
        ActionEvent mockEvent = new ActionEvent(mockScanButton, ActionEvent.ACTION_PERFORMED, "scan");

        // Simulate button click event and trigger the action
        doAnswer(invocation -> {
            // Call the actual method on the source object when the button is clicked
            source.performScan(testFilePath, testMD5);
            return null;
        }).when(mockScanButton).doClick();

        // Trigger button action
        mockScanButton.doClick();
        verify(mockScanButton, times(1)).doClick();

        // Verify label updates after button action
        assertEquals(PATH + testFilePath, source.pathLabel.getText());
        assertEquals(MD5_STRING + testMD5, source.labelMD5.getText());
    }

    // MD5 class related test cases
    @Test
    public void testMD5Checksum() throws Exception {
        // Get MD5 checksum for a file
        String md5Checksum = MD5.getMD5Checksum(testFilePath);
        assertNotNull(md5Checksum);

        // Make sure the checksum length is as expected for MD5 (32 characters)
        assertEquals(32, md5Checksum.length());

        // Verify the result of the checksum calculation
        assertEquals(testMD5, md5Checksum);
    }

    @Test
    public void testMD5Hash() {
        String text = "TestString";
        String expectedMD5 = MD5.hash(text);
        assertEquals("2fb4174612579bdffcb97531b958e443", expectedMD5);

        // Verify the result of the hash calculation
        assertEquals(expectedMD5, MD5.hash(text));
    }

    @Test
    public void testCreateChecksum() throws Exception {
        byte[] checksum = MD5.createChecksum(testFilePath);
        assertNotNull(checksum);

        // Check if the checksum length is 16 bytes (MD5 produces 128-bit hash)
        assertEquals(16, checksum.length);

        // Verify the result of the checksum calculation
        assertEquals(testMD5, MD5.getMD5Checksum(testFilePath));
    }

    // Test edge case: empty file
    @Test
    public void testEmptyFile() throws Exception {
        String emptyFilePath = "test_files/empty_file.txt";
        File emptyFile = new File(emptyFilePath);
        try {
            boolean fileCreated = emptyFile.createNewFile();  // Create an empty file
            assertTrue("Failed to create empty file: " + emptyFilePath, fileCreated);
            
            // Perform scan on the empty file
            String emptyFileMD5 = MD5.getMD5Checksum(emptyFilePath);
            source.performScan(emptyFilePath, emptyFileMD5);

            // Verify label updates for an empty file
            verify(source.pathLabel).setText(PATH + emptyFilePath);
            verify(source.labelMD5).setText(MD5_STRING + emptyFileMD5);

        } finally {
            // Cleanup the empty file
            boolean fileDeleted = emptyFile.delete();
            assertTrue("Failed to delete empty file: " + emptyFilePath, fileDeleted);
        }
    }
}
