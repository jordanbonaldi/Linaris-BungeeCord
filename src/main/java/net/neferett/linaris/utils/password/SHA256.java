package net.neferett.linaris.utils.password;

import java.security.*;
import java.math.*;

public class SHA256 implements EncryptionMethod
{
    @Override
    public String getHash(final String password, final String salt, final String name) throws NoSuchAlgorithmException {
        return "$SHA$" + salt + "$" + getSHA256(String.valueOf(getSHA256(password)) + salt);
    }
    
    @Override
    public boolean comparePassword(final String hash, final String password) throws NoSuchAlgorithmException {
        final String[] line = hash.split("\\$");
        return hash.equals(this.getHash(password, line[2], ""));
    }
    
    private static String getSHA256(final String message) throws NoSuchAlgorithmException {
        final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.reset();
        sha256.update(message.getBytes());
        final byte[] digest = sha256.digest();
        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
    }
}
