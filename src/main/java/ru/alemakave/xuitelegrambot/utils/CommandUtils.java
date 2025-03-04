package ru.alemakave.xuitelegrambot.utils;

import java.util.ArrayList;

public class CommandUtils {
    public static String[] getArguments(String command) {
        String[] commandParts = getCommandParts(command);

        if (commandParts.length == 1) {
            return new String[0];
        }

        String[] result = new String[commandParts.length - 1];
        System.arraycopy(commandParts, 1, result, 0, result.length);

        return result;
    }

    public static String getCommand(String command) {
        return getCommandParts(command)[0];
    }

    private static String[] getCommandParts(String command) {
        String[] callbackParts = command.split(" ");

        ArrayList<String> result = new ArrayList<>(callbackParts.length - 1);
        StringBuilder arg = new StringBuilder();
        boolean isCompoundArg = false;

        for (String callbackPart : callbackParts) {
            if (isCompoundArg) {
                arg.append(" ").append(callbackPart);

                if (callbackPart.contains("\"")) {
                    result.add(arg.toString().strip());
                    isCompoundArg = false;
                    arg = new StringBuilder();
                }
            } else {
                if (callbackPart.contains("\"")) {
                    arg.append(" ").append(callbackPart);
                    isCompoundArg = true;
                } else {
                    result.add(callbackPart);
                }
            }
        }

        return result.toArray(String[]::new);
    }
}
