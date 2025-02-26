## Запуск
<ol>
  <li>Скачать последнюю <a href="https://github.com/Alemakave/3x-ui-telegram-bot/releases">релизную версию</a> <code>3x-ut-telegram-bot.jar</code></li>
  <li>Создать конфигурационный файл <code>application.yml</code> и заполнить</li>

  ```yaml 
threex:
    panel:
      scheme: http # Тип подключения к серверу (опционально) [http (по умолчанию) | https]
      ip: 10.10.10.10
      port: 5678
      path: bibBoBbibO
      username: admin
      password: admin
      # Для использования самоподписанных сертификатов необходимо указать путь к 
      #   файлу публичного сертификата или использовать хранилище ключей.
      #   Если будут заполнены оба варианта, будет использоваться только хранилище ключей
      ssl:
        # Для использования публичного файла сертификата
        key:
          public:
            path: путь_к_публичному_файлу_сертификата
        # Для использования хранилища ключей
        truststore:
          path: путь_к_хранилищу_файлов_сертификатов
          password: пароль_от_хранилища_сертификатов
    # Прокси необходим, если бот запускается из RU региона. 
    #   Если запускается из другого региона, необходимо удалить
    connection:
      proxy:
        address: 127.0.0.1
        port: 12334
telegram:
    bot:
      token: 0000000000:AABBRRc_xyzabcdEfgHigklmNOPqrstu13
      owner:
        uuid: 24d6b4c5-22da-4d11-8e6a-86f8351029a0
  ```

  <ul>
    <li>
      Для создания хранилища ключей и импорта используйте команды ниже
      <details>
        <summary>Windows</summary>
        <code>curl -O https://raw.githubusercontent.com/Alemakave/3x-ui-telegram-bot/refs/heads/master/scripts/ImportCert.cmd</code><br>
        <code>.\ImportCert.cmd путь_к_файлу_сертификата пароль_к_хранилищу_сертификатов</code>
      </details>
      <details>
        <summary>Linux (bash)</summary>
        <code>curl -O https://raw.githubusercontent.com/Alemakave/3x-ui-telegram-bot/refs/heads/master/scripts/ImportCert.sh</code><br>
        <code>chmod +x ./ImportCert.sh</code><br>
        <code>./ImportCert.sh путь_к_файлу_сертификата пароль_к_хранилищу_сертификатов</code>
      </details>
    </li>
    <li>
      Если при запуске выпадает исключение <code>javax.net.ssl.SSLHandshakeException: No subject alternative names present</code> необходимо пересоздать сертификат к панели и переподключить к телеграм боту
      <details>
        <summary>Создание сертификата на сервере:</summary>
        1. Запустите:<br>
        <code>bash <(curl -Ls https://raw.githubusercontent.com/Alemakave/3x-ui-telegram-bot/refs/heads/master/scripts/CreateCert.sh)</code><br>
        2. Перезагрузите панель 3x-ui
      </details>
    </li>
  </ul>
  <li>
    Запустить бота через командную строку:<br>
    <code>java -jar 3x-ut-telegram-bot.jar</code>
  </li>
</ol>

## TODO

### Реализация API
<details>
  <summary>Нажмите для получения информации о маршрутах API и статусе реализации</summary>

#### Использование и статус реализации
- `/login` с `POST`-данными: `{username: '', password: ''}` для входа
- `/panel/api/inbounds` это базовый путь для следующих действий:

| Метод  | Путь                               | Описание                                                                                        |   Статус реализации   |
|:------:|------------------------------------|-------------------------------------------------------------------------------------------------|:---------------------:|
| `GET`  | `"/list"`                          | Получить список всех подключений                                                                |  :white_check_mark:   |
| `GET`  | `"/get/:id"`                       | Получить информацию о подключении по его id                                                     |  :white_check_mark:   |
| `GET`  | `"/getClientTraffics/:email"`      | Получить трафик клиента по его email                                                            |  :white_check_mark:   |
| `GET`  | `"/getClientTrafficsById/:id"`     | Получить трафик клиентов по id                                                                  |  :white_check_mark:   |
| `GET`  | `"/createbackup"`                  | Отправить бекап админам в телеграм бот                                                          | :white_square_button: |
| `POST` | `"/add"`                           | Добавить подключение                                                                            |  :white_check_mark:   |
| `POST` | `"/del/:id"`                       | Удалить подключение по его id                                                                   |  :white_check_mark:   |
| `POST` | `"/update/:id"`                    | Обновить подключение по его id                                                                  |  :white_check_mark:   |
| `POST` | `"/clientIps/:email"`              | Получить IP адреса клиентов по его email                                                        | :white_square_button: |
| `POST` | `"/clearClientIps/:email"`         | Отчистить IP адреса клиентов по его email                                                       | :white_square_button: |
| `POST` | `"/addClient"`                     | Добавить клиента в подключение                                                                  |  :white_check_mark:   |
| `POST` | `"/:id/delClient/:clientId"`       | Удалить клиента по его clientId\* в подключении по id                                           | :white_square_button: |
| `POST` | `"/updateClient/:clientId"`        | Обновить клиента по его clientId\*                                                              | :white_square_button: |
| `POST` | `"/:id/resetClientTraffic/:email"` | Сбросить трафик у клиента                                                                       | :white_square_button: |
| `POST` | `"/resetAllTraffics"`              | Сбросить трафик у всех подключений                                                              | :white_square_button: |
| `POST` | `"/resetAllClientTraffics/:id"`    | Сбросить трафик у всех клиентов в подключении по его id                                         | :white_square_button: |
| `POST` | `"/delDepletedClients/:id"`        | Удалить всех клиентов в подключении id (-1 для всех подключений) с истекшем сроком или трафиком | :white_square_button: |

\* - Поле `clientId` должно быть заполнено следующим образом:

- `client.id` для VMESS и VLESS
- `client.password` для TROJAN
- `client.email` для Shadowsocks


:white_square_button: - Не реализовано<br>
:ballot_box_with_check: - В процессе<br>
:white_check_mark: - Реализовано
</details>