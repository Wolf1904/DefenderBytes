package com.defenderbytes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

// A class for calculating MD5 hashes of strings, byte arrays, or files.
// Usage:
// 1) Feed it blocks of bytes with update()
// 2) finalize()
// 3) getHexDigest() to retrieve the digest as a hex string
// Or use MD5.hash("some_string") or MD5.getMD5Checksum("some_file") for quick results.
public class MD5 {

    private static final int BLOCK_SIZE = 64; // 64 bytes
    private static final int[] state = new int[4]; // Digest so far
    private static final byte[] buffer = new byte[BLOCK_SIZE]; // Bytes that didn't fit in the last 64 byte chunk
    private static final int[] count = new int[2]; // 64-bit counter for number of bits (low, high)
    private byte[] digest = new byte[16]; // The final result, removed final

    private boolean finalized;

    // Constructors
    public MD5() {
        init();
    }

    public MD5(String text) {
        this();
        update(text.getBytes(StandardCharsets.UTF_8), text.length());
        finalizeHash();
    }

    // Initialize MD5 state
    private void init() {
        finalized = false;
        count[0] = 0;
        count[1] = 0;
        state[0] = 0x67452301;
        state[1] = 0xefcdab89;
        state[2] = 0x98badcfe;
        state[3] = 0x10325476;
    }

    // Update the MD5 with a chunk of bytes
    public void update(byte[] input, int length) {
        int index = (count[0] >>> 3) & 0x3F; // Calculate buffer index
        count[0] += (length << 3);

        if (count[0] < (length << 3)) {
            count[1]++;
        }
        count[1] += (length >>> 29);

        int partLen = 64 - index;
        int i = 0;

        if (length >= partLen) {
            System.arraycopy(input, 0, buffer, index, partLen);
            transform(buffer);

            for (i = partLen; i + BLOCK_SIZE - 1 < length; i += BLOCK_SIZE) {
                byte[] block = Arrays.copyOfRange(input, i, i + BLOCK_SIZE);
                transform(block);
            }

            index = 0;
        }

        if (i < length) {
            System.arraycopy(input, i, buffer, index, length - i);
        }
    }

    // Finalize the MD5 hash calculation
    public void finalizeHash() {
        byte[] padding = new byte[64];
        padding[0] = (byte) 0x80;
        byte[] bits = encode(count, 8);

        int index = (count[0] >>> 3) & 0x3f;
        int padLen = (index < 56) ? (56 - index) : (120 - index);
        update(padding, padLen);
        update(bits, 8);

        digest = encode(state, 16); // Removed final modifier here
        finalized = true;
    }

    // Get the MD5 hash as a hex string
    public String getHexDigest() {
        if (!finalized) {
            finalizeHash();
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Main MD5 transformation routine
    private void transform(byte[] block) {
        int[] x = new int[16];
        decode(x, block, 64);

        int a = state[0];
        int b = state[1];
        int c = state[2];
        int d = state[3];

        // Round 1
        a = ffTransform(a, b, c, d, x[0], 7, 0xd76aa478);
        d = ffTransform(d, a, b, c, x[1], 12, 0xe8c7b756);
        // (Continue similarly for all MD5 transformations using ffTransform, GG, HH, II)

        state[0] += a;
        state[1] += b;
        state[2] += c;
        state[3] += d;
    }

    // MD5 helper functions
    private static int f(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    private static int g(int x, int y, int z) {
        return (x & z) | (y & ~z);
    }

    private static int h(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private static int i(int x, int y, int z) {
        return y ^ (x | ~z);
    }

    private static int rotateLeft(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private static int ffTransform(int a, int b, int c, int d, int x, int s, int ac) {
        a += f(b, c, d) + x + ac;
        a = rotateLeft(a, s);
        a += b;
        return a;
    }

    // (Similarly implement GG, HH, II based on MD5 logic)

    private byte[] encode(int[] input, int length) {
        byte[] output = new byte[length];
        for (int i = 0, j = 0; j < length; i++, j += 4) {
            output[j] = (byte) (input[i] & 0xff);
            output[j + 1] = (byte) ((input[i] >>> 8) & 0xff);
            output[j + 2] = (byte) ((input[i] >>> 16) & 0xff);
            output[j + 3] = (byte) ((input[i] >>> 24) & 0xff);
        }
        return output;
    }

    private void decode(int[] output, byte[] input, int length) {
        for (int i = 0, j = 0; j < length; i++, j += 4) {
            output[i] = (input[j] & 0xff) |
                    ((input[j + 1] & 0xff) << 8) |
                    ((input[j + 2] & 0xff) << 16) |
                    ((input[j + 3] & 0xff) << 24);
        }
    }

    // Static method to calculate MD5 checksum of a file
    public static byte[] createChecksum(String filename) throws Exception {
        // Use try-with-resources to ensure the InputStream is closed automatically
        try (InputStream fis = new FileInputStream(filename)) {
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;
    
            // Read the file and update the MessageDigest with the read bytes
            while ((numRead = fis.read(buffer)) != -1) {
                complete.update(buffer, 0, numRead);
            }
    
            // Return the final computed digest (MD5 hash)
            return complete.digest();
        }
    }
    
    // Static method to get the MD5 checksum of a file as a hex string
    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        StringBuilder result = new StringBuilder();
        for (byte value : b) {
            result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    // Static helper for quick MD5 hash
    public static String hash(String text) {
        MD5 md5 = new MD5(text);
        return md5.getHexDigest();
    }
}
