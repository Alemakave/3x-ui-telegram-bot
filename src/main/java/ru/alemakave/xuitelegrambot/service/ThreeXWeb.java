package ru.alemakave.xuitelegrambot.service;

import org.springframework.http.HttpMethod;
import ru.alemakave.xuitelegrambot.model.Certificate;

/**
 * Подробности API доступны по <a href="https://github.com/MHSanaei/3x-ui?tab=readme-ov-file#api-routes">ссылке</a>
  */
public interface ThreeXWeb {
    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#GET}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/createbackup}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт запускает создание резервной копии системы и инициирует доставку файла
     *     резервной копии назначенным администраторам через настроенного бота Telegram. Сервер проверяет статус
     *     активации бота Telegram в настройках системы и проверяет наличие идентификаторов администратора, указанных
     *     в настройках, перед отправкой резервной копии.
     * </p>
     */
    void createBackup();

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /panel/api/inbounds/resetAllTraffics}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт используется для сброса статистики трафика для всех входящих
     *     подключений в системе.
     * </p>
     */
    void resetAllTraffics();

    /**
     * <p>
     *     <b><i>Метов</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /server/getNewX25519Cert}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт генерирует новый сертификат с публичным и приватным ключом
     * </p>
     *
     * @return сертификат с публичным и приватным ключом
     */
    Certificate getNewCertificate();

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#POST}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /server/importDB}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт импортирует и устанавливает резервную копию на сервер
     * </p>
     */
    void importBackup(byte[] bytes);

    /**
     * <p>
     *     <b><i>Метод</i></b>: {@link HttpMethod#GET}
     * </p>
     * <p>
     *     <b><i>Путь</i></b>: {@code /server/getDb}
     * </p>
     * <p>
     *     <b><i>Описание</i></b>: Этот эндпоинт создает резервную копию
     * </p>
     */
    byte[] exportBackup();
}
