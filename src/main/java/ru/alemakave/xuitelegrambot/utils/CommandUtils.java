package ru.alemakave.xuitelegrambot.utils;

import java.util.ArrayList;

public class CommandUtils {
    public static String[] getArguments(String command) {
        String[] callbackParts = command.split(" ");
        if (callbackParts.length == 1) {
            return new String[0];
        }

        ArrayList<String> result = new ArrayList<>(callbackParts.length - 1);
        StringBuilder arg = new StringBuilder();
        boolean isCompoundArg = false;

        for (int i = 1; i < callbackParts.length; i++) {
            if (isCompoundArg) {
                arg.append(" ").append(callbackParts[i]);

                if (callbackParts[i].contains("\"")) {
                    result.add(arg.toString().strip());
                    isCompoundArg = false;
                    arg = new StringBuilder();
                }
            } else {
                if (callbackParts[i].contains("\"")) {
                    arg.append(" ").append(callbackParts[i]);
                    isCompoundArg = true;
                } else {
                    result.add(callbackParts[i]);
                }
            }
        }

        return result.toArray(String[]::new);
    }
}
