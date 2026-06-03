package com.njit.takeoutsystem.utils;

import com.njit.takeoutsystem.common.BusinessException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private PasswordUtil() {
    }

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return "pbkdf2_sha256$" + ITERATIONS + "$" + ENCODER.encodeToString(salt) + "$" + ENCODER.encodeToString(hash);
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        String[] parts = storedPassword.split("\\$");
        if (parts.length != 4 || !"pbkdf2_sha256".equals(parts[0])) {
            return false;
        }

        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = DECODER.decode(parts[2]);
        byte[] expected = DECODER.decode(parts[3]);
        byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iterations, expected.length * 8);
        return slowEquals(expected, actual);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (Exception exception) {
            throw new BusinessException(500, "密码加密失败");
        }
    }

    // 固定时间比较，避免密码摘要比较时泄露时间差。
    private static boolean slowEquals(byte[] expected, byte[] actual) {
        int diff = expected.length ^ actual.length;
        for (int i = 0; i < expected.length && i < actual.length; i++) {
            diff |= expected[i] ^ actual[i];
        }
        return diff == 0;
    }
}
