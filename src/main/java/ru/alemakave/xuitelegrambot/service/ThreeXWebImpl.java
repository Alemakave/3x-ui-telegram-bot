package ru.alemakave.xuitelegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.alemakave.xuitelegrambot.client.CookedWebClient;
import ru.alemakave.xuitelegrambot.exception.UnauthorizedException;
import ru.alemakave.xuitelegrambot.exception.UnsupportedMethodException;
import ru.alemakave.xuitelegrambot.functions.UnauthorizedThrowingFunction;
import ru.alemakave.xuitelegrambot.model.Certificate;
import ru.alemakave.xuitelegrambot.model.messages.CertificateGenMessage;
import ru.alemakave.xuitelegrambot.model.messages.Message;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ThreeXWebImpl implements ThreeXWeb {
    @Autowired
    private CookedWebClient webClient;
    @Autowired
    private ThreeXAuth threeXAuth;

    @Override
    public void createBackup() {
        throw new UnsupportedMethodException("Метод не поддерживается!");
    }

    @Override
    public byte[] exportBackup() {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .get("/server/getDb")
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ResponseEntity<byte[]> resp = responseSpec.toEntity(byte[].class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        return resp.getBody();
    }

    @Override
    public void resetAllTraffics() {

    }

    @Override
    public Certificate getNewCertificate() {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/server/getNewX25519Cert")
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        return responseSpec
                .bodyToMono(CertificateGenMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block()
                .getObj();
    }

    @Override
    public void importBackup(byte[] bytes) {
        threeXAuth.login();


    }
}
