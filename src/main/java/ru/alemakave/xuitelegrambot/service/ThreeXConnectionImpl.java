package ru.alemakave.xuitelegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.alemakave.xuitelegrambot.client.CookedWebClient;
import ru.alemakave.xuitelegrambot.configuration.WebClientConfiguration;
import ru.alemakave.xuitelegrambot.exception.ConnectionFailedException;
import ru.alemakave.xuitelegrambot.exception.CreateNewConnectionException;
import ru.alemakave.xuitelegrambot.exception.UnauthorizedException;
import ru.alemakave.xuitelegrambot.functions.UnauthorizedThrowingFunction;
import ru.alemakave.xuitelegrambot.mapper.ConnectionMapper;
import ru.alemakave.xuitelegrambot.model.*;
import ru.alemakave.xuitelegrambot.model.messages.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ThreeXConnectionImpl implements ThreeXConnection {
    @Autowired
    private CookedWebClient webClient;
    @Autowired
    private ConnectionMapper connectionMapper;
    @Autowired
    private ThreeXAuth threeXAuth;
    @Autowired
    private ThreeXWeb threeXWeb;
    @Autowired
    private WebClientConfiguration webClientConfiguration;

    @Override
    public List<Connection> list() {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .get("/panel/api/inbounds/list")
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ConnectionsMessage connectionsMessage = responseSpec
                .bodyToMono(ConnectionsMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        if (connectionsMessage == null) {
            throw new ConnectionFailedException("Connection failed! Connections message is null!");
        }

        List<Connection> result = connectionsMessage.getObj().stream()
                .map(connectionGetUpdateDTO -> {
                    try {
                        return connectionMapper.connectionGetUpdateDtoToConnection(connectionGetUpdateDTO);
                    } catch (JsonProcessingException e) {
                        log.error("Throw exception for user: " + connectionGetUpdateDTO.getRemark());
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());;

        try {
            log.debug("Список подключений: {}",
                    new ObjectMapper().writeValueAsString(result)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @SneakyThrows
    @Override
    public Connection get(long inboundId) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .get("/panel/api/inbounds/get/" + inboundId)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ConnectionMessage connectionMessage = responseSpec
                .bodyToMono(ConnectionMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        if (connectionMessage == null) {
            throw new ConnectionFailedException("Connection failed! Connection message is null!");
        }

        Connection result = connectionMapper.connectionGetUpdateDtoToConnection(connectionMessage.getObj());

        log.debug("Подключение: {}", new ObjectMapper().writeValueAsString(result));

        return result;
    }

    @Override
    public Connection add(String remark) {
        threeXAuth.login();

        List<Connection> connections = list();
        List<Integer> connectionUsedPorts = connections.stream()
                .map(Connection::getPort)
                .toList();

        Random rand = new Random();
        int port = rand.nextInt(65535);
        while (connectionUsedPorts.contains(port)) {
            port = rand.nextInt(65535);
        }

        Certificate certificate = threeXWeb.getNewCertificate();
        log.debug(certificate.toString());

        ConnectionSettings connectionSettings = new ConnectionSettings();
        connectionSettings.setClients(new ArrayList<>());
        connectionSettings.setDecryption("none");
        connectionSettings.setFallbacks(new ArrayList<>());

        RealityConnectionSettings realityConnectionSettings = new RealityConnectionSettings();
        realityConnectionSettings.setPublicKey(certificate.getPublicKey());
        realityConnectionSettings.setFingerprint("random");
        realityConnectionSettings.setServerName("");
        realityConnectionSettings.setSpiderX("/");

        RealitySettings realitySettings = new RealitySettings();
        realitySettings.setShow(false);
        realitySettings.setXver(0);
        realitySettings.setDest("yahoo.com:443");
        realitySettings.setServerNames(Arrays.asList("yahoo.com", "www.yahoo.com", "www.speedtest.net", "speedtest.net"));
        realitySettings.setPrivateKey(certificate.getPrivateKey());
        realitySettings.setMinClient("");
        realitySettings.setMaxClient("");
        realitySettings.setMaxTimediff(0);
        realitySettings.setShortIds(List.of(String.format("%08X", new Random().nextInt())));
        realitySettings.setSettings(realityConnectionSettings);

        TcpSettings tcpSettings = new TcpSettings();
        tcpSettings.setAcceptProxyProtocol(false);
        TcpSettings.Header header = new TcpSettings.Header();
        header.setType("none");
        tcpSettings.setHeader(header);

        ConnectionStreamSettings connectionStreamSettings = new ConnectionStreamSettings();
        connectionStreamSettings.setNetwork("tcp");
        connectionStreamSettings.setSecurity("reality");
        connectionStreamSettings.setExternalProxy(new ArrayList<>());
        connectionStreamSettings.setRealitySettings(realitySettings);
        connectionStreamSettings.setTcpSettings(tcpSettings);

        ConnectionSniffing connectionSniffing = new ConnectionSniffing();
        connectionSniffing.setEnabled(true);
        connectionSniffing.setDestOverride(Arrays.asList("http", "tls", "quic", "fakedns"));
        connectionSniffing.setMetadataOnly(false);
        connectionSniffing.setRouteOnly(false);

        ConnectionAllocate connectionAllocate = new ConnectionAllocate();
        connectionAllocate.setStrategy("always");
        connectionAllocate.setRefresh(5);
        connectionAllocate.setConcurrency(3);

        Connection newConnection = new Connection();
        newConnection.setRemark(remark);
        newConnection.setEnable(true);
        newConnection.setListen(webClientConfiguration.getPanelIP());
        newConnection.setPort(port);
        newConnection.setProtocol("vless");
        newConnection.setSettings(connectionSettings);
        newConnection.setStreamSettings(connectionStreamSettings);
        newConnection.setSniffing(connectionSniffing);
        newConnection.setAllocate(connectionAllocate);
        newConnection.setTag("inbound-" + newConnection.getListen() + ":" + port);

        try {
            WebClient.ResponseSpec responseSpec = webClient
                    .post("/panel/api/inbounds/add")
                    .bodyValue(connectionMapper.connectionToConnectionGetUpdateDto(newConnection))
                    .retrieve()
                    .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

            ConnectionMessage message = responseSpec
                    .bodyToMono(ConnectionMessage.class)
                    .onErrorResume(new UnauthorizedThrowingFunction<>())
                    .block();

            log.debug("Add connection: {}", message);

            if (message.isSuccess()) {
                return connectionMapper.connectionGetUpdateDtoToConnection(message.getObj());
            } else {
                throw new CreateNewConnectionException("Error create new connection: " + message.getMsg());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DeleteConnectionMessage delete(long inboundId) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/del/" + inboundId)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        DeleteConnectionMessage message = responseSpec
                .bodyToMono(DeleteConnectionMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Delete connection: {}", message);

        return message;
    }

    @SneakyThrows
    @Override
    public void update(long inboundId, Connection connection) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/update/" + inboundId)
                .bodyValue(connectionMapper.connectionToConnectionGetUpdateDto(connection))
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ConnectionMessage receivedConnectionMessage = responseSpec
                .bodyToMono(ConnectionMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Update: {}", receivedConnectionMessage.toString().replace("\r", "").replace("\n", "\\n"));
    }

    @Override
    public List<String> onlines() {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/onlines")
                .retrieve();

        OnlinesMessage onlinesMessage = responseSpec
                .bodyToMono(OnlinesMessage.class)
                .block();

        log.debug("Onlines message: {}", onlinesMessage);

        return onlinesMessage.getObj();
    }
}
