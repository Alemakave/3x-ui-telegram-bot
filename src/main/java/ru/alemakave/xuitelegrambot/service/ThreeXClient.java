package ru.alemakave.xuitelegrambot.service;

import org.springframework.http.HttpMethod;
import ru.alemakave.xuitelegrambot.model.Client;

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
    void getClientTrafficsByEmail(String email);

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
    void getClientTrafficsById(int uuid);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/clientIps/{email}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для получения записей IP, связанных с конкретным клиентом,
     *     по его электронной почте.
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
     *     клиентом, по его адресу электронной почты ({@code {email}}).
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
    Client addClient(long inboundId);

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
    void deleteClientByClientId(long inboundId, String clientId);

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
    void updateClient(String uuid);

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
    void resetClientTraffic(long inboundId, String email);

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
    void resetAllClientTraffics(long inboundId);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>:
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для удаления всех исчерпанных клиентов, связанных с
     *     конкретным входящим подключением, по его идентификатору ({@code {inboundId}}). Если {@code inboundId=-1},
     *     исчерпанные клиенты будут удалены из всех входящих подключений.
     * </p>
     *
     * @param inboundId Идентификатор входящего подключения, из которого будут удалены исчерпанные клиенты. Если
     * {@code inboundId=-1}, исчерпанные клиенты будут удалены из всех входящих подключений.
     */
    void delDepletedClients(long inboundId);

}
