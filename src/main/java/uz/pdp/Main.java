package uz.pdp;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.bot.OnlineBookShopBot;


public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new OnlineBookShopBot());
    }
}
