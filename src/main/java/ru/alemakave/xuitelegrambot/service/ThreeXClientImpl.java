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
import ru.alemakave.xuitelegrambot.exception.UnauthorizedException;
import ru.alemakave.xuitelegrambot.functions.UnauthorizedThrowingFunction;
import ru.alemakave.xuitelegrambot.mapper.ClientMapper;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.Flow;
import ru.alemakave.xuitelegrambot.model.messages.AddClientMessage;

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
    public void getClientTrafficsByEmail(String email) {

    }

    @Override
    public void getClientTrafficsById(int uuid) {

    }

    @Override
    public void clientIps(String email) {

    }

    @Override
    public void clearClientIps(String email) {

    }

    @SneakyThrows
    @Override
    public Client addClient(long inboundId) {
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

        List<Client> clients = threeXConnection.get(inboundId).getSettings().getClients();
        return clients.get(clients.size() - 1);
    }

    @Override
    public void deleteClientByClientId(long inboundId, String clientId) {

    }

    @Override
    public void updateClient(String uuid) {

    }

    @Override
    public void resetClientTraffic(long inboundId, String email) {

    }

    @Override
    public void resetAllClientTraffics(long inboundId) {

    }

    @Override
    public void delDepletedClients(long inboundId) {

    }
}
