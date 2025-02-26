package ru.alemakave.xuitelegrambot.utils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class FileUtils {
    public static String byteToDisplaySize(long size) {
        if (size < 1024) {
            return size + "B";
        }

        CharacterIterator characters = new StringCharacterIterator("KMGT");
        while (size > 1024*1024) {
            size /= 1024;
            characters.next();
        }

        return String.format("%.2f %sB", size / 1024.0, characters.current());
    }
}
