package ru.alemakave.xuitelegrambot.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.alemakave.xuitelegrambot.client.CookedWebClient;
import ru.alemakave.xuitelegrambot.dto.ClientAddDto;
import ru.alemakave.xuitelegrambot.dto.ClientWithConnectionDto;
import ru.alemakave.xuitelegrambot.exception.ClientNotFoundException;
import ru.alemakave.xuitelegrambot.exception.UnauthorizedException;
import ru.alemakave.xuitelegrambot.functions.UnauthorizedThrowingFunction;
import ru.alemakave.xuitelegrambot.mapper.ClientMapper;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.ClientTraffics;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.model.Flow;
import ru.alemakave.xuitelegrambot.model.messages.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ThreeXClientImpl implements ThreeXClient {
    @Autowired
    private ThreeXAuth threeXAuth;
    @Autowired
    private ThreeXConnection threeXConnection;
    @Autowired
    private CookedWebClient webClient;
    @Autowired
    private ClientMapper clientMapper;

    @Override
    public ClientTraffics getClientTrafficsByEmail(String email) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .get("/panel/api/inbounds/getClientTraffics/" + email)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ClientTrafficsMessage message = responseSpec.bodyToMono(ClientTrafficsMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        if (!message.isSuccess()) {
            throw new RuntimeException("Error add client! Received message: \"" + message.getMsg() + "\"");
        }
        log.debug("Client traffics by email (email={}): {}", email, message.getObj());

        return message.getObj();
    }

    @Override
    public ClientTraffics[] getClientTrafficsById(String uuid) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .get("/panel/api/inbounds/getClientTrafficsById/" + uuid)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        ClientTrafficsByIdMessage message = responseSpec.bodyToMono(ClientTrafficsByIdMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        if (!message.isSuccess()) {
            throw new RuntimeException("Error add client! Received message: \"" + message.getMsg() + "\"");
        }
        log.debug("Client traffics by uuid (uuid={}): {}", uuid, message.getObj());

        return message.getObj();
    }

    @Override
    public void clientIps(String email) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/clientIps/" + email)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        Message message = responseSpec.bodyToMono(Message.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Client ips (email={}): {}", email, message);
    }

    @Override
    public void clearClientIps(String email) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/clearClientIps/" + email)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        Message message = responseSpec.bodyToMono(Message.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Client ips (email={}): {}", email, message);
    }

    @SneakyThrows
    @Override
    public void addClient(long inboundId) {
        threeXAuth.login();

        Client newClient = new Client();
        newClient.setId(UUID.randomUUID().toString());
        newClient.setFlow(Flow.defaultValue());
        newClient.setEmail(newClient.getId().split("-")[0]);
        newClient.setEnable(true);
        newClient.setTgId("");
        newClient.setSubId(newClient.getId().split("-")[4]);

        ClientAddDto clientAddDto = clientMapper.clientToClientAddDto(inboundId, newClient);

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/addClient")
                .bodyValue(clientAddDto)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        AddClientMessage addClientMessage = responseSpec
                .bodyToMono(AddClientMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        if (!addClientMessage.isSuccess()) {
            throw new RuntimeException("Error add client! Received message: \"" + addClientMessage.getMsg() + "\"");
        }

        log.debug("Add client: {}", addClientMessage);
    }

    @Override
    public boolean deleteClientByClientId(long inboundId, String clientId) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post(String.format("/panel/api/inbounds/%s/delClient/%s", inboundId, clientId))
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        VoidObjMessage deleteClientMessage = responseSpec
                .bodyToMono(VoidObjMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Delete client: {}", deleteClientMessage);

        return deleteClientMessage.isSuccess();
    }

    @SneakyThrows
    @Override
    public boolean updateClient(String uuid, Client client) {
        threeXAuth.login();

        long connectionId = getClientByUUID(uuid).getConnection().getId();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/updateClient/" + uuid)
                .bodyValue(clientMapper.clientToClientUpdateDto(connectionId, client))
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        VoidObjMessage updateClientMessage = responseSpec
                .bodyToMono(VoidObjMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Update client: {}", updateClientMessage);

        return updateClientMessage.isSuccess() && getClientByUUID(uuid).getClient().equals(client);
    }

    @Override
    public boolean resetClientTraffic(long inboundId, String email) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post(String.format("/panel/api/inbounds/%s/resetClientTraffic/%s", inboundId, email))
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        VoidObjMessage resetClientTrafficMessage = responseSpec
                .bodyToMono(VoidObjMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Reset client traffic: {}", resetClientTrafficMessage);

        return resetClientTrafficMessage.isSuccess();
    }

    @Override
    public boolean resetAllClientTraffics(long inboundId) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/resetAllClientTraffics/" + inboundId)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        VoidObjMessage resetAllClientTrafficMessage = responseSpec
                .bodyToMono(VoidObjMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Reset all client traffics: {}", resetAllClientTrafficMessage);

        return true;
    }

    @Override
    public void delDepletedClients(long inboundId) {
        threeXAuth.login();

        WebClient.ResponseSpec responseSpec = webClient
                .post("/panel/api/inbounds/delDepletedClients/" + inboundId)
                .retrieve()
                .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(new UnauthorizedException(webClient.getCookies())));

        VoidObjMessage delDepletedClientsMessage = responseSpec
                .bodyToMono(VoidObjMessage.class)
                .onErrorResume(new UnauthorizedThrowingFunction<>())
                .block();

        log.debug("Del depleted clients: {}", delDepletedClientsMessage);
    }

    @Override
    public ClientWithConnectionDto getClientByUUID(String uuid) {
        List<Connection> connections = threeXConnection.list();

        for (Connection connection : connections) {
            Client[] clients = connection.getSettings().getClients().toArray(Client[]::new);

            for (Client client : clients) {
                if (client.getId().equals(uuid)) {
                    return new ClientWithConnectionDto(connection, client);
                }
            }
        }

        throw new ClientNotFoundException("Не удалось найти клиента c UUID=" + uuid);
    }
}
