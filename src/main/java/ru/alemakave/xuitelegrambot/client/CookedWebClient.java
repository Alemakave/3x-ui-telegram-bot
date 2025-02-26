package ru.alemakave.xuitelegrambot.client;

import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.ssl.SslContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;
import reactor.netty.http.client.HttpClient;
import ru.alemakave.xuitelegrambot.exception.InvalidCountryException;

import java.util.*;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;
import static ru.alemakave.xuitelegrambot.utils.WebUtils.isInvalidCountry;

@ToString
@EqualsAndHashCode
@Scope(SCOPE_SINGLETON)
public class CookedWebClient {
    private final WebClient.Builder webClientBuilder;
    @Getter
    private final Map<String, String> cookies = new HashMap<>();
    @Getter
    @Setter
    private HttpScheme scheme;
    private final String host;
    private final int port;
    private final String basePath;
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private SslContext sslContext;

    public CookedWebClient(WebClient.Builder webClientBuilder, HttpScheme scheme, String host, int port, String basePath) {
        this.webClientBuilder = webClientBuilder;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.basePath = basePath;
    }

    public CookedWebClient(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClientBuilder = webClientBuilder;

        String url = baseUrl;

        if (baseUrl.startsWith("http://")) {
            scheme = HttpScheme.HTTP;
            url = url.substring("http://".length());
        } else if (baseUrl.startsWith("https://")) {
            scheme = HttpScheme.HTTPS;
            url = url.substring("https://".length());
        }

        String hostAndPort = url.substring(0, url.indexOf('/'));
        if (hostAndPort.contains(":")) {
            host = url.substring(0, url.indexOf(':'));
            url = url.substring(url.indexOf(':') + 1);
            port = Integer.parseInt(url.substring(url.indexOf(':') + 1, url.indexOf('/')));
            url = url.substring(Integer.toString(port).length() + 1);
        } else {
            host = hostAndPort;
            url = url.substring(hostAndPort.length() + 1);
            port = scheme.port();
        }

        basePath = url;
    }

    public RequestHeadersSpec<?> get(String uri) {
        return getWebClient().get().uri(uri).cookies(
                stringStringMultiValueMap -> cookies.forEach(
                        stringStringMultiValueMap::add
                )
        );
    }

    public RequestBodySpec post(String uri) {
        return getWebClient().post().uri(uri).cookies(
                stringStringMultiValueMap -> cookies.forEach(
                        stringStringMultiValueMap::add
                )
        );
    }

    public void putCookie(String name, String value) {
        cookies.put(name, value);
    }

    public WebClient getWebClient() {
        if (scheme == HttpScheme.HTTPS) {
            httpClient = httpClient.secure(s -> s.sslContext(sslContext));

            webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
        } else {
            webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));

            if (isInvalidCountry(webClientBuilder)) {
                throw new InvalidCountryException("Invalid country! Use proxy to change country from RU region!");
            }
        }

        String hostAndPort = host;

        if (port != scheme.port()) {
            hostAndPort += ":" + port;
        }

        return webClientBuilder
                .baseUrl(String.format("%s://%s/%s", scheme.name(), hostAndPort, basePath))
                .build();
    }

    public boolean isHttp() {
        return scheme == HttpScheme.HTTP;
    }

    public boolean isHttps() {
        return scheme == HttpScheme.HTTPS;
    }
}
