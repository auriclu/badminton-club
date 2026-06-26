package com.club.badminton.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }
    public static boolean checkPassword(String plainText, String hashed) {
        return BCrypt.checkpw(plainText, hashed);
    }
}