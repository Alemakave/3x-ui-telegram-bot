package ru.alemakave.xuitelegrambot.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class TelegramClient {
    private final long tgChatId;
    private final TelegramClientRole role;
    private final long connectionId;
    private final String clientUuid;
    @Setter
    private TelegramClientMode mode = TelegramClientMode.NONE;

    public TelegramClient(long tgChatId, TelegramClientRole role, long connectionId, String clientUuid) {
        this.tgChatId = tgChatId;
        this.role = role;
        this.connectionId = connectionId;
        this.clientUuid = clientUuid;
    }

    @Getter
    public enum TelegramClientRole {
        ADMIN(2),
        USER(1);

        private final int accessLevel;

        TelegramClientRole(int accessLevel) {
            this.accessLevel = accessLevel;
        }

        @Override
        public String toString() {
            return switch (this) {
                case USER -> "User";
                case ADMIN -> "Admin";
            };
        }
    }

    public enum TelegramClientMode {
        NONE,
        ENTER_CONNECTION_NAME
    }
}
