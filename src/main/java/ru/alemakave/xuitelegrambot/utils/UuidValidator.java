package ru.alemakave.xuitelegrambot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UuidValidator {
    public static boolean isValidUUID(String uuid) {
        if (uuid == null) {
            return false;
        }

        if (uuid.length() != 36) {
            return false;
        }

        String pattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        Matcher matcher = Pattern.compile(pattern).matcher(uuid);

        return matcher.matches();
    }
}
