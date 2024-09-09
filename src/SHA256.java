import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

// A class for calculating SHA-256 hashes of strings, byte arrays, or files.
// Usage:
// 1) Feed it blocks of bytes with update()
// 2) finalize()
// 3) getHexDigest() to retrieve the digest as a hex string
// Or use SHA256.hash("some_string") or SHA256.getSHA256Checksum("some_file") for quick scanlogss.
public class SHA256 {

    private static final int BLOCK_SIZE = 64; // 64 bytes
    private final long[] state = new long[8]; // Digest so far (64-bit)
    private final byte[] buffer = new byte[BLOCK_SIZE]; // Bytes that didn't fit in the last 64 byte chunk
    private final long[] count = new long[2]; // 64-bit counter for number of bits (low, high)
    private byte[] digest = new byte[32]; // The final scanlogs (32 bytes for SHA-256)

    private boolean finalized;

    // Constructors
    public SHA256() {
        init();
    }

    public SHA256(String text) {
        this();
        update(text.getBytes(StandardCharsets.UTF_8), text.length());
        finalizeHash();
    }

    // Initialize SHA-256 state
    private void init() {
        finalized = false;
        count[0] = 0;
        count[1] = 0;
        // Initial hash values for SHA-256
        state[0] = 0x6a09e667L;
        state[1] = 0xbb67ae85L;
        state[2] = 0x3c6ef372L;
        state[3] = 0xa54ff53aL;
        state[4] = 0x510e527fL;
        state[5] = 0x9b05688cL;
        state[6] = 0x1f83d9abL;
        state[7] = 0x5be0cd19L;
    }

    // Update the SHA-256 with a chunk of bytes
    public void update(byte[] input, int length) {
        int index = (int) (count[0] >>> 3) & 0x3F; // Calculate buffer index
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

    // Finalize the SHA-256 hash calculation
    public void finalizeHash() {
        byte[] padding = new byte[64];
        padding[0] = (byte) 0x80;
        byte[] bits = encode(count, 8);

        int index = (int) (count[0] >>> 3) & 0x3f;
        int padLen = (index < 56) ? (56 - index) : (120 - index);
        update(padding, padLen);
        update(bits, 8);

        digest = encode(state, 32); // SHA-256 produces a 32-byte hash
        finalized = true;
    }

    // Get the SHA-256 hash as a hex string
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

    // Main SHA-256 transformation routine
    private void transform(byte[] block) {
        long[] w = new long[64];
        decode(w, block, 64);

        long a = state[0];
        long b = state[1];
        long c = state[2];
        long d = state[3];
        long e = state[4];
        long f = state[5];
        long g = state[6];
        long h = state[7];

        for (int i = 0; i < 64; i++) {
            long s1 = rotateRight(e, 6) ^ rotateRight(e, 11) ^ rotateRight(e, 25);
            long ch = (e & f) ^ (~e & g);
            long temp1 = h + s1 + ch + K[i] + w[i];
            long s0 = rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22);
            long maj = (a & b) ^ (a & c) ^ (b & c);
            long temp2 = s0 + maj;

            h = g;
            g = f;
            f = e;
            e = d + temp1;
            d = c;
            c = b;
            b = a;
            a = temp1 + temp2;
        }

        state[0] += a;
        state[1] += b;
        state[2] += c;
        state[3] += d;
        state[4] += e;
        state[5] += f;
        state[6] += g;
        state[7] += h;
    }

    // SHA-256 helper functions
    private static long rotateRight(long x, int n) {
        return (x >>> n) | (x << (64 - n));
    }

    private static final long[] K = {
        0x428a2f98L, 0x71374491L, 0xb5c0fbcfL, 0xe9b5dba5L, 0x3956c25bL, 0x59f111f1L, 0x923f82a4L, 0xab1c5ed5L,
        0xd807aa98L, 0x12835b01L, 0x243185beL, 0x550c7dc3L, 0x72be5d74L, 0x80deb1feL, 0x9bdc06a7L, 0xc19bf174L,
        0xe49b69c1L, 0xefbe4786L, 0x0fc19dc6L, 0x240ca1ccL, 0x2de92c6fL, 0x4a7484aaL, 0x5cb0a9dcL, 0x76f988daL,
        0x983e5152L, 0xa831c66dL, 0xb00327c8L, 0xbf597fc7L, 0xc6e00bf3L, 0xd5a79147L, 0x06ca6351L, 0x14292967L,
        0x27b70a85L, 0x2e1b2138L, 0x4d2c6dfcL, 0x53380d13L, 0x650a7354L, 0x766a0abbL, 0x81c2c92eL, 0x92722c85L,
        0xa2bfe8a1L, 0xa81a664bL, 0xc24b8b70L, 0xc76c51a3L, 0xd192e819L, 0xd6990624L, 0xf40e3585L, 0x106aa070L,
        0x19a4c116L, 0x1e376c08L, 0x2748774cL, 0x34b0bcb5L, 0x391c0cb3L, 0x4ed8aa4aL, 0x5b9cca4fL, 0x682e6ff3L,
        0x748f82eeL, 0x78a5636fL, 0x84c87814L, 0x8cc70208L, 0x90befffaL, 0xa4506cebL, 0xbef9a3f7L, 0xc67178f2L
    };

    // Encode long array to bytes
    private byte[] encode(long[] input, int length) {
        byte[] output = new byte[length];
        for (int i = 0, j = 0; j < length; i++, j += 8) {
            output[j] = (byte) (input[i] & 0xff);
            output[j + 1] = (byte) ((input[i] >>> 8) & 0xff);
            output[j + 2] = (byte) ((input[i] >>> 16) & 0xff);
            output[j + 3] = (byte) ((input[i] >>> 24) & 0xff);
            output[j + 4] = (byte) ((input[i] >>> 32) & 0xff);
            output[j + 5] = (byte) ((input[i] >>> 40) & 0xff);
            output[j + 6] = (byte) ((input[i] >>> 48) & 0xff);
            output[j + 7] = (byte) ((input[i] >>> 56) & 0xff);
        }
        return output;
    }

    // Decode bytes to long array
    private void decode(long[] output, byte[] input, int length) {
        for (int i = 0, j = 0; j < length; i++, j += 8) {
            output[i] = ((input[j] & 0xff)) |
                        ((input[j + 1] & 0xff) << 8) |
                        ((input[j + 2] & 0xff) << 16) |
                        ((input[j + 3] & 0xff) << 24) |
                        ((input[j + 4] & 0xff) << 32) |
                        ((input[j + 5] & 0xff) << 40) |
                        ((input[j + 6] & 0xff) << 48) |
                        ((input[j + 7] & 0xff) << 56);
        }
    }

    // Static helper for quick SHA-256 hash
    public static String hash(String text) {
        SHA256 sha256 = new SHA256(text);
        return sha256.getHexDigest();
    }

    // Static method to calculate SHA-256 checksum of a file
    public static byte[] createChecksum(String filename) throws Exception {
        try (InputStream fis = new FileInputStream(filename)) {
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("SHA-256");
            int numRead;
            while ((numRead = fis.read(buffer)) != -1) {
                complete.update(buffer, 0, numRead);
            }
            return complete.digest();
        }
    }

    // Static method to get the SHA-256 checksum of a file as a hex string
    public static String getSHA256Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        StringBuilder scanlogs = new StringBuilder();
        for (byte value : b) {
            scanlogs.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        }
        return scanlogs.toString();
    }
}
