package ru.alemakave.xuitelegrambot.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandUtilsTest {
    @Test
    @DisplayName("Get arguments test 1")
    public void testGetArguments1() {
        String[] commandParts = new String[] {"command", "arg1", "arg2", "arg3"};

        String[] actual = new String[commandParts.length - 1];
        System.arraycopy(commandParts, 1, actual, 0, actual.length);

        String[] expected = CommandUtils.getArguments(String.join(" ", commandParts));

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Get arguments test 2")
    public void testGetArguments2() {
        String[] commandParts = new String[] {"command", "arg1", "arg2", "arg3", "\"arg 4\""};

        String[] actual = new String[commandParts.length - 1];
        System.arraycopy(commandParts, 1, actual, 0, actual.length);

        String[] expected = CommandUtils.getArguments(String.join(" ", commandParts));

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Get arguments test 3")
    public void testGetArguments3() {
        String[] commandParts = new String[] {"command", "arg1", "arg2", "arg3", "arg4=\"arg 4\""};

        String[] actual = new String[commandParts.length - 1];
        System.arraycopy(commandParts, 1, actual, 0, actual.length);

        String[] expected = CommandUtils.getArguments(String.join(" ", commandParts));

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Get arguments test 4")
    public void testGetArguments4() {
        String[] commandParts = new String[] {"command", "arg1", "arg2", "arg3", "arg4=\"arg 4\"", "arg5=\"arg 4 5\"", "arg6=\"arg 4\"6"};

        String[] actual = new String[commandParts.length - 1];
        System.arraycopy(commandParts, 1, actual, 0, actual.length);

        String[] expected = CommandUtils.getArguments(String.join(" ", commandParts));

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Get command test 1")
    public void testGetCommand1() {
        String[] commandParts = new String[] {"command", "arg1", "arg2", "arg3"};
        String actual = commandParts[0];

        String expected = CommandUtils.getCommand(String.join(" ", commandParts));

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get command test 2")
    public void testGetCommand2() {
        String[] commandParts = new String[] {"command", "arg1", "arg2", "arg3"};
        String actual = commandParts[0];

        String expected = CommandUtils.getCommand(String.join(" ", commandParts));

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get command test 3")
    public void testGetCommand3() {
        String[] commandParts = new String[] {"command", "arg1", "arg2", "arg3", "arg4=\"arg 4\""};
        String actual = commandParts[0];

        String expected = CommandUtils.getCommand(String.join(" ", commandParts));

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get command test 4")
    public void testGetCommand4() {
        String[] commandParts = new String[] {"command", "arg1", "arg2", "arg3", "arg4=\"arg 4\"", "arg5=\"arg 4 5\"", "arg6=\"arg 4\"6"};

        String actual = commandParts[0];
        String expected = CommandUtils.getCommand(String.join(" ", commandParts));

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get command test 5")
    public void testGetCommand5() {
        String[] commandParts = new String[] {"\"command qwe\"", "arg1", "arg2", "arg3", "arg4=\"arg 4\"", "arg5=\"arg 4 5\"", "arg6=\"arg 4\"6"};

        String actual = commandParts[0];
        String expected = CommandUtils.getCommand(String.join(" ", commandParts));

        assertEquals(expected, actual);
    }
}
