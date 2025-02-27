package ru.alemakave.xuitelegrambot.service;

import org.springframework.http.HttpMethod;
import ru.alemakave.xuitelegrambot.model.Connection;
import ru.alemakave.xuitelegrambot.model.messages.Message;

import java.util.List;

public interface ThreeXConnection {
    /**
     * <p>
     * <b><i>Метод</i></b>: {@link HttpMethod#GET}
     * </p>
     * <p>
     * <b><i>Путь</i></b>: {@code /panel/api/inbounds/list}
     * </p>
     * <p>
     * <b><i>Описание</i></b>: Этот путь используется для получения полного списка всех подключений, а также
     * связанных с ними параметров клиента и статистики.
     * </p>
     */
    List<Connection> list();

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#GET}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/get/{inboundId}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот путь используется для получения статистики и сведений для
     *     соединения, идентифицированного {@code {inboundId}}. Сюда входит информация о самом подключении,
     *     его статистике и подключенных к нему клиентах.
     * </p>
     *
     * @param inboundId Идентификатор входящего подключения, для которого запрашивается информация.
     */
    Connection get(long inboundId);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/add}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для добавления новой входящей конфигурации.
     * </p>
     */
    Connection add(String remark);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/del/{inboundId}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для удаления входящего подключения, по его идентификатору
     *     ({@code {inboundId}}).
     * </p>
     *
     * @param inboundId Идентификатор входящего подключения, подлежащего удалению.
     */
    Message<?> delete(long inboundId);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/update/{inboundId}}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для обновления существующего входящего подключения,
     *     по его идентификатору ({@code {inboundId}}).
     * </p>
     *
     * @param inboundId Идентификатор входящего подключения, которое необходимо обновить.
     */
    void update(long inboundId, Connection connection);


    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/onlines}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт возвращает клиентов онлайн
     * </p>
     */
    List<String> onlines();
}
