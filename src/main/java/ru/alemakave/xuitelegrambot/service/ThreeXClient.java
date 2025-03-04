package ru.alemakave.xuitelegrambot.service;

import org.springframework.http.HttpMethod;
import ru.alemakave.xuitelegrambot.dto.ClientWithConnectionDto;
import ru.alemakave.xuitelegrambot.model.Client;
import ru.alemakave.xuitelegrambot.model.ClientTraffics;

public interface ThreeXClient {
    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#GET}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/getClientTraffics/{email}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот путь используется для получения информации о конкретном клиенте на основе его
     *     электронной почты. Этот эндпоинт предоставляет такие сведения, как статистика трафика и другую
     *     соответствующую информацию, связанную с клиентом.
     * </p>
     *
     * @param email Адрес электронной почты клиента, для которого запрашивается информация.
     */
    ClientTraffics getClientTrafficsByEmail(String email);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#GET}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/getClientTrafficsById/{uuid}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот путь используется для получения информации о клиентах на основе его uuid. Этот
     *     эндпоинт предоставляет такие сведения, как статистика и другую соответствующую информацию, связанную с
     *     клиентами.
     * </p>
     *
     * @param uuid Идентификатор пользователя, для которого запрашивается информация
     */
    ClientTraffics[] getClientTrafficsById(String uuid);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/clientIps/{email}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для получения записей IP, связанных с конкретным клиентом,
     *     по его электронной почте.<br>
     *     Работа эндпоинта не известна. При тестировании на 24 записях был получен ответ <i>Message(success=true, msg=, obj=No IP Record)</i>
     * </p>
     *
     * @param email Адрес электронной почты клиента, для которого запрашиваются записи IP.
     */
    void clientIps(String email);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/clearClientIps/{email}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для сброса или очистки записей IP, связанных с конкретным
     *     клиентом, по его адресу электронной почты ({@code {email}}).<br>
     *     Работа эндпоинта не известна. При тестировании на 24 записях был получен ответ <i>Message(success=true, msg=, obj=No IP Record)</i>
     * </p>
     *
     * @param email Адрес электронной почты клиента, для которого необходимо сбросить записи IP.
     */
    void clearClientIps(String email);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/addClient}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для добавления нового клиента к определенному входящему
     *     подключению, по его идентификатору в теле.
     * </p>
     */
    void addClient(long inboundId);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/{inboundId}/delClient/{uuid}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для удаления клиента,
     *     идентифицированного его UUID ({@code {uuid}}) в пределах определенного входящего подключения,
     *     идентифицированного его идентификатором ({@code {inboundId}}).
     * </p>
     *
     * @param inboundId Идентификатор входящего подключения, из которого будет удален клиент.
     * @param clientId Уникальный идентификатор (UUID) клиента, подлежащего удалению.
     */
    boolean deleteClientByClientId(long inboundId, String clientId);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/updateClient/{uuid}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для обновления существующего клиента,
     *     идентифицированного его UUID ({@code {uuid}}), в пределах определенного входящего трафика.
     * </p>
     */
    boolean updateClient(String uuid, Client client);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/{inboundId}/resetClientTraffic/{email}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для сброса статистики трафика для клиента,
     *     идентифицированного по его адресу электронной почты ({@code {email}}) в рамках определенного входящего
     *     подключения, по его идентификатору ({@code {inboundId}}).
     * </p>
     *
     * @param inboundId Идентификатор входящего подключения, к которому принадлежит клиент.
     * @param email Адрес электронной почты клиента, у которого сбрасывается статистика трафика.
     */
    boolean resetClientTraffic(long inboundId, String email);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/resetAllClientTraffics/{inboundId}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для сброса статистики трафика для всех клиентов, связанных
     *     с определенным входящим подключением, по его идентификатору ({@code {inboundId}}).
     * </p>
     *
     * @param inboundId Идентификатор входящего подключения, для которого сбрасывается клиентский трафик.
     */
    boolean resetAllClientTraffics(long inboundId);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/delDepletedClients/{inboundId}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для удаления всех клиентов с истекшим сроком, связанных с
     *     конкретным подключением, по его идентификатору ({@code {inboundId}}). Если {@code inboundId=-1},
     *     клиенты с истекшим сроком будут удалены из всех входящих подключений.
     * </p>
     *
     * @param inboundId Идентификатор входящего подключения, из которого будут удалены исчерпанные клиенты. Если
     * {@code inboundId=-1}, исчерпанные клиенты будут удалены из всех входящих подключений.
     */
    void delDepletedClients(long inboundId);

    /**
     * <p>
     *     <b><i>Описание</i></b>: Метод используется для получения клиента по его UUID.
     * </p>
     *
     * @param uuid Идентификатор клиента.
     * @return Возвращает пользователя по его UUID.
     */
    ClientWithConnectionDto getClientByUUID(String uuid);
}
