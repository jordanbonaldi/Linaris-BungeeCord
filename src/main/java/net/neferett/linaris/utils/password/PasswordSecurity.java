package net.neferett.linaris.utils.password;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

public class PasswordSecurity
{
    private static SecureRandom rnd;
    public static HashMap<String, String> userSalt;
    
    static {
        PasswordSecurity.rnd = new SecureRandom();
        PasswordSecurity.userSalt = new HashMap<String, String>();
    }
    
    public static String createSalt(final int length) throws NoSuchAlgorithmException {
        final byte[] msg = new byte[40];
        PasswordSecurity.rnd.nextBytes(msg);
        final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        sha1.reset();
        final byte[] digest = sha1.digest(msg);
        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest)).substring(0, length);
    }
    
    public static String getHash(final HashAlgorithm alg, final String password, final String playerName) throws NoSuchAlgorithmException {
        EncryptionMethod method;
        try {
            if (alg != HashAlgorithm.CUSTOM) {
                method = (EncryptionMethod)alg.getclass().newInstance();
            }
            else {
                method = null;
            }
        }
        catch (InstantiationException e) {
            throw new NoSuchAlgorithmException("Problem with this hash algorithm");
        }
        catch (IllegalAccessException e2) {
            throw new NoSuchAlgorithmException("Problem with this hash algorithm");
        }
        String salt = "";
        switch (alg) {
            case SHA256: {
                salt = createSalt(16);
                break;
            }
            case CUSTOM: {
                break;
            }
            default: {
                throw new NoSuchAlgorithmException("Unknown hash algorithm");
            }
        }
        return method.getHash(password, salt, playerName);
    }
    
    public static boolean comparePasswordWithHash(final String password, final String hash) throws NoSuchAlgorithmException {
        final HashAlgorithm algo = HashAlgorithm.SHA256;
        EncryptionMethod method;
        try {
            if (algo != HashAlgorithm.CUSTOM) {
                method = (EncryptionMethod)algo.getclass().newInstance();
            }
            else {
                method = null;
            }
        }
        catch (InstantiationException e) {
            throw new NoSuchAlgorithmException("Problem with this hash algorithm");
        }
        catch (IllegalAccessException e2) {
            throw new NoSuchAlgorithmException("Problem with this hash algorithm");
        }
        try {
            if (method.comparePassword(hash, password)) {
                return true;
            }
        }
        catch (Exception ex) {}
        return false;
    }
}
