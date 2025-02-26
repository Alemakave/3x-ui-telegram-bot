package ru.alemakave.xuitelegrambot.service;

import io.netty.handler.codec.http.HttpScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.alemakave.xuitelegrambot.client.CookedWebClient;
import ru.alemakave.xuitelegrambot.configuration.WebClientConfiguration;
import ru.alemakave.xuitelegrambot.exception.InvalidCountryException;
import ru.alemakave.xuitelegrambot.utils.WebUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ThreeXAuthImpl implements ThreeXAuth {
    @Autowired
    private CookedWebClient webClient;
    @Autowired
    private WebClientConfiguration webClientConfiguration;
    @Autowired
    private BodyInserters.FormInserter<String> authBody;
    @Autowired
    private HttpClient httpClient;

    @Override
    public void login() {
        if (isAuthorized()) {
            return;
        }

        ResponseEntity<String> response = webClient
                .post("/login")
                .body(authBody)
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response == null) {
            log.error("Connection failed!");
            return;
        }

        if (response.getStatusCode().is3xxRedirection()) {
            if (!webClient.isHttps()) {
                webClient.setScheme(HttpScheme.HTTPS);
                login();
                return;
            }
        }

        HttpHeaders headers = response.getHeaders();
        if (!headers.containsKey(HttpHeaders.SET_COOKIE)) {
            log.error("Login failed!");
            return;
        }

        List<String> cookie = headers.get(HttpHeaders.SET_COOKIE);
        if (cookie == null) {
            log.error("Login failed!");
            return;
        }

        for (String value : cookie) {
            Map<String, String> cookieParams = Arrays.stream(value.split(";"))
                    .collect(Collectors.toMap(
                            s -> s.contains("=") ? s.split("=", 2)[0].strip() : s.strip(),
                            s -> s.contains("=") ? s.split("=", 2)[1].strip() : ""
                    ));

            webClient.putCookie("3x-ui", cookieParams.get("3x-ui"));
        }

        log.debug("Login response: {}", response.getBody());
    }

    @Override
    public boolean isAuthorized() {
        if (webClient.isHttp()) {
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient));

            if (WebUtils.isInvalidCountry(webClientBuilder)) {
                throw new InvalidCountryException();
            }
        }

        if (webClient.getCookies().get("3x-ui") == null) {
            return false;
        }

        ResponseEntity<String> response = webClient.get("/panel").retrieve().toEntity(String.class).block();
        if (response == null) {
            return false;
        }

        if (response.getStatusCode().is3xxRedirection()) {
            if (!response.getHeaders().containsKey(HttpHeaders.LOCATION)) {
                return false;
            }

            return Objects.requireNonNull(response.getHeaders().get(HttpHeaders.LOCATION)).get(0).equals("/" + webClientConfiguration.getPanelPath() + "/panel/");
        }

        return false;
    }
}
