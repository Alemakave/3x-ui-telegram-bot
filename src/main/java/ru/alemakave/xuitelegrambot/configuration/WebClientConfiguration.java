package ru.alemakave.xuitelegrambot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ru.alemakave.xuitelegrambot.client.CookedWebClient;
import ru.alemakave.xuitelegrambot.exception.InvalidCountry;
import ru.alemakave.xuitelegrambot.exception.UnsetException;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;
import static ru.alemakave.xuitelegrambot.utils.WebUtils.isInvalidCountry;
import static ru.alemakave.xuitelegrambot.utils.WebUtils.connectProxyToWebClientBuilder;

@Configuration
public class WebClientConfiguration {
    @Value("${threex.panel.ip}")
    private String panelIP;
    @Value("${threex.panel.port}")
    private int panelPort;
    @Value("${threex.panel.path}")
    private String panelPath;
    @Value("${threex.panel.username}")
    private String panelUsername;
    @Value("${threex.panel.password}")
    private String panelPassword;
    @Value("${threex.connection.proxy.address:}")
    private String proxyAddress;
    @Value("${threex.connection.proxy.port:-1}")
    private int proxyPort;

    @Scope(SCOPE_SINGLETON)
    @Bean
    public CookedWebClient webClient() {
        if (panelIP == null || panelIP.isEmpty()) {
            throw new UnsetException("Invalid panel IP address!");
        }
        if (panelPort < 0 || panelPort > 65535) {
            throw new UnsetException("Invalid panel port!");
        }
        if (panelPath == null || panelPath.isEmpty()) {
            throw new UnsetException("Invalid panel path!");
        }

        WebClient.Builder webClientBuilder = WebClient.builder();
        connectProxyToWebClientBuilder(webClientBuilder, proxyAddress, proxyPort);
        if (isInvalidCountry(webClientBuilder)) {
            throw new InvalidCountry("Invalid country! Use proxy to change country from RU region!");
        }

        String baseUrl = String.format("http://%s:%s/%s", panelIP, panelPort, panelPath);
        webClientBuilder = WebClient.builder().baseUrl(baseUrl);

        connectProxyToWebClientBuilder(webClientBuilder, proxyAddress, proxyPort);

        return new CookedWebClient(webClientBuilder.build());
    }

    @Scope(SCOPE_SINGLETON)
    @Bean
    public BodyInserters.FormInserter<String> authBody() {
        return BodyInserters.fromFormData("username", panelUsername).with("password", panelPassword);
    }
}
