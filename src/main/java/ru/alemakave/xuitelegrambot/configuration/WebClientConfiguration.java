package ru.alemakave.xuitelegrambot.configuration;

import io.netty.handler.codec.http.HttpScheme;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.alemakave.xuitelegrambot.client.CookedWebClient;
import ru.alemakave.xuitelegrambot.exception.UnsetException;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;
import static ru.alemakave.xuitelegrambot.utils.WebUtils.*;

@Configuration
@PropertySource(value = {"file:./application.yml"}, ignoreResourceNotFound = true)
public class WebClientConfiguration {
    @Value("${threex.panel.scheme:http}")
    private String panelScheme;
    @Getter
    @Value("${threex.panel.ip}")
    private String panelIP;
    @Getter
    @Value("${threex.panel.port}")
    private int panelPort;
    @Getter
    @Value("${threex.panel.path}")
    private String panelPath;
    @Value("${threex.panel.username}")
    private String panelUsername;
    @Value("${threex.panel.password}")
    private String panelPassword;
    @Getter
    @Value("${threex.connection.proxy.address:}")
    private String proxyAddress;
    @Getter
    @Value("${threex.connection.proxy.port:-1}")
    private int proxyPort;

    @Scope(SCOPE_SINGLETON)
    @Bean
    public CookedWebClient webClient() {
        if (panelIP == null || panelIP.isEmpty()) {
            throw new UnsetException("Unset or invalid panel IP address!");
        }
        if (panelPort < 0 || panelPort > 65535) {
            throw new UnsetException("Unset or invalid panel port!");
        }
        if (panelPath == null || panelPath.isEmpty()) {
            throw new UnsetException("Unset or invalid panel path!");
        }
        HttpScheme scheme;
        if (panelScheme.equalsIgnoreCase("http")) {
            scheme = HttpScheme.HTTP;
        } else if (panelScheme.equalsIgnoreCase("https")) {
            scheme = HttpScheme.HTTPS;
        } else {
            throw new UnsetException("Unset or invalid panel scheme!");
        }

        return new CookedWebClient(WebClient.builder(), scheme, panelIP, panelPort, panelPath);
    }

    @Scope(SCOPE_SINGLETON)
    @Bean
    public BodyInserters.FormInserter<String> authBody() {
        return BodyInserters.fromFormData("username", panelUsername).with("password", panelPassword);
    }

    @Scope(SCOPE_SINGLETON)
    @Bean
    public HttpClient httpClient() {
        return connectProxyToHttpClient(HttpClient.create(), proxyAddress, proxyPort);
    }
}
