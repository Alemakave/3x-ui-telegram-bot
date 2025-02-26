package ru.alemakave.xuitelegrambot.client;

import io.netty.handler.codec.http.HttpScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CookedWebClientTest {
    @Test
    @DisplayName("Init test 1")
    public void initTest1() {
        CookedWebClient actual = new CookedWebClient(null, HttpScheme.HTTP, "test.host", 8080, "testpath");
        CookedWebClient expected = new CookedWebClient(null, "http://test.host:8080/testpath");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Init test 2")
    public void initTest2() {
        CookedWebClient actual = new CookedWebClient(null, HttpScheme.HTTPS, "test.host", 8080, "testpath");
        CookedWebClient expected = new CookedWebClient(null, "https://test.host:8080/testpath");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Init test 3")
    public void initTest3() {
        CookedWebClient actual = new CookedWebClient(null, HttpScheme.HTTP, "test.host", HttpScheme.HTTP.port(), "testpath");
        CookedWebClient expected = new CookedWebClient(null, "http://test.host/testpath");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Init test 4")
    public void initTest4() {
        CookedWebClient actual = new CookedWebClient(null, HttpScheme.HTTPS, "test.host", HttpScheme.HTTPS.port(), "testpath");
        CookedWebClient expected = new CookedWebClient(null, "https://test.host/testpath");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Init test 5")
    public void initTest5() {
        CookedWebClient actual = new CookedWebClient(null, HttpScheme.HTTP, "test.host", HttpScheme.HTTP.port(), "");
        CookedWebClient expected = new CookedWebClient(null, "http://test.host/");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Init test 6")
    public void initTest6() {
        CookedWebClient actual = new CookedWebClient(null, HttpScheme.HTTPS, "test.host", HttpScheme.HTTPS.port(), "");
        CookedWebClient expected = new CookedWebClient(null, "https://test.host/");
        assertEquals(expected, actual);
    }
}
