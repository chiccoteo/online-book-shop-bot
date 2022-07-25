package uz.pdp.services;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAudio;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.bot.BotState;
import uz.pdp.model.*;
import uz.pdp.model.User;
import uz.pdp.model.enums.Lan;
import uz.pdp.model.enums.Role;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uz.pdp.bot.OnlineBookShopBot.bookListByCategory;
import static uz.pdp.db.DB.*;
import static uz.pdp.services.AdminService.adminShowMenu;
import static uz.pdp.services.GenerateKeyboardMarkup.generateInlineKeyboardMarkup;
import static uz.pdp.services.GenerateKeyboardMarkup.generateReplyKeyboardMarkup;
import static uz.pdp.services.OrderService.*;

public class BotService {

    // USER ENTER TO BOT METHODS
    public static SendMessage start(Update update) {
        String chatId = getChatId(update);
        User tgUser = getOrCreateTgUser(chatId, update);
        if (tgUser.getRole().equals(Role.ADMIN)) {
            if (tgUser.getState().equals(BotState.START))
                return askPhoneNumber(tgUser);
            else
                return adminShowMenu(tgUser);
        } else if (tgUser.getState().equals(BotState.START)) {
            return chooseLanguage(update);
        } else
            return showMenu(tgUser);
    }

    public static String getChatId(Update update) {
        if (update.hasMessage())
            return update.getMessage().getChatId().toString();
        else if (update.hasCallbackQuery())
            return update.getCallbackQuery().getMessage().getChatId().toString();
        else
            return "";
    }

    public static User getOrCreateTgUser(String chatId, Update update) {
        for (User user : userList) {
            if (user.getChatId().equals(chatId)) {
                return user;
            }
        }
        String name = update.getMessage().getFrom().getFirstName();
        String username = update.getMessage().getFrom().getUserName();
        User tgUser = new User(name, username, chatId, 0.0, Role.USER, BotState.START);
        userList.add(tgUser);
        return tgUser;
    }

    public static void saveUserChanges(User changedUser) {
        for (User user : userList) {
            if (user.getChatId().equals(changedUser.getChatId())) {
                user = changedUser;
            }
        }
    }


    // LANGUAGE METHODS
    public static SendMessage chooseLanguage(Update update) {
        String chatId = getChatId(update);
        User tgUser = getOrCreateTgUser(chatId, update);
        if (tgUser.getState().equals(BotState.START)) {
            tgUser.setState(BotState.CHOOSE_LANG);
        }
        saveUserChanges(tgUser);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        if (tgUser.getState().equals(BotState.CHANGE_LANG)) {
            sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "O'zingizga qulay til tanlang \uD83D\uDC45"
                    : tgUser.getLanguage().equals(Lan.ENG) ? "Choose the language you want \uD83D\uDC45"
                    : "Выберите желаемый язык \uD83D\uDC45");
        } else {
            sendMessage.setText("Salom " + update.getMessage().getFrom().getFirstName() + ".\n\n" +
                    "\uD83E\uDD16 " + " Botga xush kelibsiz.\n" +
                    "\nO'zingizga qulay til tanlang");
        }
        sendMessage.setReplyMarkup(generateReplyKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static SendMessage getUserLang(User tgUser, String text) {
        switch (text) {
            case "O'zbek \uD83C\uDDFA\uD83C\uDDFF":
                tgUser.setLanguage(Lan.UZ);
                break;
            case "English \uD83C\uDDEC\uD83C\uDDE7":
                tgUser.setLanguage(Lan.ENG);
                break;
            case "Ruscha \uD83C\uDDF7\uD83C\uDDFA":
                tgUser.setLanguage(Lan.RU);
                break;
            default:
                return new SendMessage(tgUser.getChatId(), "Error");
        }

        if (tgUser.getState().equals(BotState.CHOOSE_LANG))
            return askPhoneNumber(tgUser);
        else
            return showMenu(tgUser);

    }

    public static SendMessage askPhoneNumber(User tgUser) {
        tgUser.setState(BotState.SHARE_CONTACT);
        saveUserChanges(tgUser);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Iltimos, JO'NATISH tugmasi orqali " +
                "telefon raqamingizni jo'nating" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Please, share your phoneNumber by pressing SHARE CONTACT" :
                        "Отправьте свой номер телефона с помощью кнопки ОТПРАВИТЬ");
        sendMessage.setReplyMarkup(generateReplyKeyboardMarkup(tgUser));
        return sendMessage;
    }


    // GET_CONTACT METHODS
    public static SendMessage getContact(User tgUser, Contact contact) {
        String phoneNumber = checkPhoneNumber(contact.getPhoneNumber());
        tgUser.setPhoneNumber(phoneNumber);
        tgUser.setState(BotState.ENTER_CODE);
        saveUserChanges(tgUser);
        return sendCode(tgUser);
    }

    public static String checkPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;
    }

    public static int code;

    private static SendMessage sendCode(User tgUser) {
        code = (int) (Math.random() * 899999) + 100000;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Tasdiqlash kodi: " + code :
                tgUser.getLanguage().equals(Lan.ENG) ? "Confirmation code: " + code :
                        "Код подтверждения: " + code);
        return sendMessage;
    }


    // SHOW ALL MENUS METHODS
    public static SendMessage showMenu(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDD1D Menyuni tanlang" :
                tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDD1D Choose menu" :
                        "\uD83D\uDD1D Выберите меню");
        tgUser.setState(BotState.SHOW_MENU);
        saveUserChanges(tgUser);
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static SendMessage showBookCategory(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDD20 Kategoriyalar" :
                tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDD20 Categories" :
                        "\uD83D\uDD20 Категории");
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static SendMessage chooseBookType(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDCD9 Kitob turini tanlang" :
                tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDCD9 Choose the book type" :
                        "\uD83D\uDCD9 Выберите тип книги");
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }


    // CHOOSE BOOKS METHODS
    public static SendMessage showBooks(User tgUser, List<Book> bookList) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        StringBuilder text = new StringBuilder(((tgUser.getTempBookPage() - 1) * PAGE_SIZE + 1) +
                "-" + (Math.min(PAGE_SIZE * tgUser.getTempBookPage(), bookList.size())) + "  " + bookList.size() + " dan\n\n");
        int j = 1;
        for (int i = PAGE_SIZE * (tgUser.getTempBookPage() - 1);
             ((PAGE_SIZE * tgUser.getTempBookPage() < bookList.size()) ?
                     i < PAGE_SIZE * tgUser.getTempBookPage() : i < bookList.size()); i++) {
            text.append(j).append(". ").append(bookList.get(i).getName()).append(" - ").append(bookList.get(i).getAuthor()).append("\n");
            j++;
        }
        sendMessage.setText(text.toString());
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static SendMessage enterBooksPage(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        int booksCount = bookListByCategory(tgUser, tgUser.getTempCategory().getId()).size();
        int maxPage = (int) Math.ceil((double) booksCount / PAGE_SIZE);
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? maxPage + " ta sahifa bor. Joriy sahifa: " +
                tgUser.getTempBookPage() + "\nQaysi sahifaga o'tmoqchisiz?"
                : tgUser.getLanguage().equals(Lan.ENG) ? (maxPage == 1) ? "There is a page"
                : "There are " + maxPage + " pages" + ". Current page: " +
                tgUser.getTempBookPage() + "\nWhich page do you want to go to?"
                : "Есть " + maxPage + " страницы. Текущая страница: " + tgUser.getTempBookPage() +
                "На какую страницу вы хотите перейти?"
        );
        return sendMessage;
    }

    public static EditMessageText showBackNextBooks(Update update, User tgUser, List<Book> bookList) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(tgUser.getChatId());
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        StringBuilder text = new StringBuilder(((tgUser.getTempBookPage() - 1) * PAGE_SIZE + 1) +
                "-" + (Math.min(PAGE_SIZE * tgUser.getTempBookPage(), bookList.size())) + "  " + bookList.size() + " dan\n\n");
        int j = 1;
        for (int i = PAGE_SIZE * (tgUser.getTempBookPage() - 1);
             ((PAGE_SIZE * tgUser.getTempBookPage() < bookList.size()) ?
                     i < PAGE_SIZE * tgUser.getTempBookPage() : i < bookList.size()); i++) {
            text.append(j).append(". ").append(bookList.get(i).getName()).append(" - ").append(bookList.get(i).getAuthor()).append("\n");
            j++;
        }
        editMessageText.setText(String.valueOf(text));
        editMessageText.setReplyMarkup((InlineKeyboardMarkup) generateInlineKeyboardMarkup(tgUser));
        return editMessageText;
    }

    public static SendPhoto enterPrintedBookAmount(User tgUser) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(tgUser.getChatId());
        sendPhoto.setPhoto(new InputFile(new File(getBookByIndex(tgUser).getBookUrl())));
        sendPhoto.setCaption(tgUser.getLanguage().equals(Lan.UZ) ? "Kitoblar sonini tanlang yoki kiriting \uD83D\uDD22" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Select or enter the number of books \uD83D\uDD22" :
                        "Выберите или введите количество книг \uD83D\uDD22");
        sendPhoto.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendPhoto;
    }

    public static SendMessage setPrintedBookAmount(Integer amount, User tgUser) {
        Order newOrder = new Order(tgUser, getBookByIndex(tgUser), amount, getOrderStatus("BASKET"));
        List<Order> userBasket = tgUser.getBasket();
        boolean hasOrder = false;
        for (Order order : userBasket) {
            if (order.getBook().equals(newOrder.getBook())) {
                hasOrder = true;
                order.setAmount(order.getAmount() + amount);
                break;
            }
        }
        if (!hasOrder) {
            userBasket.add(newOrder);
            tgUser.setBasket(userBasket);
        }
        return showBasket(tgUser);
    }

    public static SendMessage deleteOrderInBasket(User tgUser, UUID orderId) {
        List<Order> userBasket = tgUser.getBasket();
        userBasket.removeIf(order -> order.getId().equals(orderId));
        tgUser.setBasket(userBasket);
        tgUser.setState(BotState.SHOW_BASKET);
        saveUserChanges(tgUser);
        return showBasket(tgUser);
    }


    // BUY BOOKS METHODS
    public static SendMessage choosePayType(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "To'lov turini tanlang \uD83D\uDCB0" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Choose PayType \uD83D\uDCB0" :
                        "Выберите меню \uD83D\uDCB0");
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static SendMessage askBuyOrNot(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        double totalSumma = getTotalPriceInBasket(tgUser.getBasket()) * (1 + tgUser.getPayType().getCommission() / 100);
        if (totalSumma > tgUser.getBalance()) {
            sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Kechirasiz, sizning hisobingizda \uD83D\uDCB3 mablag' yetarli emas!" :
                    tgUser.getLanguage().equals(Lan.ENG) ? "Sorry, there is not enough money in your account! \uD83D\uDCB3" :
                            "Извините, на вашем счету \uD83D\uDCB3 недостаточно денег!");
        } else {
            sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ?
                    "\tHisobot \uD83E\uDDFE" +
                            "\nVositachilik haqi: " + getTotalPriceInBasket(tgUser.getBasket()) * tgUser.getPayType().getCommission() / 100 +
                            "\nJami summa: " + totalSumma +
                            "\n\nXarid qilasizmi?"
                    : tgUser.getLanguage().equals(Lan.ENG) ?
                    "\tReport \uD83E\uDDFE" +
                            "\nCommission: " + getTotalPriceInBasket(tgUser.getBasket()) * tgUser.getPayType().getCommission() / 100 +
                            "\nTotal price: " + totalSumma +
                            "\n\nDo you buy?"
                    :
                    "\tОтчет \uD83E\uDDFE" +
                            "\nКомиссия: " + getTotalPriceInBasket(tgUser.getBasket()) * tgUser.getPayType().getCommission() / 100 +
                            "\nОбщая цена: " + totalSumma +
                            "\n\nТы покупаешь?");
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("✅");
            button.setCallbackData("YES");
            row.add(button);
            button = new InlineKeyboardButton();
            button.setText("❎");
            button.setCallbackData("NO");
            row.add(button);
            rows.add(row);
            markup.setKeyboard(rows);
            sendMessage.setReplyMarkup(markup);
        }
        return sendMessage;
    }

    public static SendMessage askLocation(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        double totalSumma = getTotalPriceInBasket(tgUser.getBasket()) * (1 + tgUser.getPayType().getCommission() / 100);
        if (totalSumma > tgUser.getBalance()) {
            sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Kechirasiz, sizning hisobingizda \uD83D\uDCB3 mablag' yetarli emas!" :
                    tgUser.getLanguage().equals(Lan.ENG) ? "Sorry, there is not enough money in your account! \uD83D\uDCB3" :
                            "Извините, на вашем счету \uD83D\uDCB3 недостаточно денег!");
        } else {
            sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ?
                    "\uD83D\uDCE6 Buyurtmani yetkazib berish uchun joylashuvingizni ulashing yoki manzilingizni kiriting" :
                    tgUser.getLanguage().equals(Lan.ENG) ? "Share location or enter address for delivery \uD83D\uDCE6" :
                            "Поделитесь местоположением или введите адрес для доставки \uD83D\uDCE6");
            sendMessage.setReplyMarkup(generateReplyKeyboardMarkup(tgUser));
        }
        return sendMessage;
    }


    // SEND BOOKS TO USERS METHODS
    public static SendAudio sendAudioBook(User tgUser) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(tgUser.getChatId());
        Order tempOrder = new Order();
        if (tgUser.getState().equals(BotState.FINISH_ORDER) || tgUser.getState().equals(BotState.SHARE_LOCATION)) {
            for (Order order : tgUser.getBasket()) {
                if (isAudioFile(order.getBook().getBookUrl()))
                    tempOrder = order;
            }
        } else if (tgUser.getState().equals(BotState.UPLOAD)) {
            for (Order order : orderList) {
                if (isAudioFile(order.getBook().getBookUrl()))
                    tempOrder = order;
            }
        }
        sendAudio.setAudio(new InputFile(new File(tempOrder.getBook().getBookUrl())));
        return sendAudio;
    }

    public static SendDocument sendEBook(User tgUser) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(tgUser.getChatId());
        Order tempOrder = new Order();
        if (tgUser.getState().equals(BotState.FINISH_ORDER) || tgUser.getState().equals(BotState.SHARE_LOCATION)) {
            for (Order order : tgUser.getBasket()) {
                if (isDocumentFile(order.getBook().getBookUrl()))
                    tempOrder = order;
            }
        } else if (tgUser.getState().equals(BotState.UPLOAD)) {
            for (Order order : orderList) {
                if (isDocumentFile(order.getBook().getBookUrl()))
                    tempOrder = order;
            }
        }
        sendDocument.setDocument(new InputFile(new File(tempOrder.getBook().getBookUrl())));
        return sendDocument;
    }

    public static SendPhoto sendPrintedBook(User tgUser) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(tgUser.getChatId());
        List<Order> basket = tgUser.getBasket();
        Order tempOrder = new Order();
        for (Order order : basket) {
            if (isImageFile(order.getBook().getBookUrl()))
                tempOrder = order;
        }
        sendPhoto.setPhoto(new InputFile(new File(tempOrder.getBook().getBookUrl())));
        sendPhoto.setCaption(tgUser.getLanguage().equals(Lan.UZ) ? tempOrder.getBook().getName() +
                " kitobi tez orada yetkaziladi"
                : tgUser.getLanguage().equals(Lan.ENG) ? "The book (" + tempOrder.getBook().getName() + ") will be delivered soon"
                : "Книга (" + tempOrder.getBook().getName() + ")скоро будет доставлена");
        return sendPhoto;
    }

    public static SendMediaGroup sendAudioBooks(User tgUser) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(tgUser.getChatId());
        List<InputMedia> inputMediaList = new ArrayList<>();
        if (tgUser.getState().equals(BotState.FINISH_ORDER) || tgUser.getState().equals(BotState.SHARE_LOCATION)) {
            for (Order order : tgUser.getBasket()) {
                if (isAudioFile(order.getBook().getBookUrl())) {
                    InputMediaAudio mediaAudio = new InputMediaAudio();
                    mediaAudio.setMedia(new File(order.getBook().getBookUrl()), order.getBook().getName());
                    inputMediaList.add(mediaAudio);
                }
            }
        } else if (tgUser.getState().equals(BotState.UPLOAD)) {
            for (Order order : orderList) {
                if (isAudioFile(order.getBook().getBookUrl())) {
                    InputMediaAudio mediaAudio = new InputMediaAudio();
                    mediaAudio.setMedia(new File(order.getBook().getBookUrl()), order.getBook().getName());
                    inputMediaList.add(mediaAudio);
                }
            }
        }
        sendMediaGroup.setMedias(inputMediaList);
        return sendMediaGroup;
    }

    public static SendMediaGroup sendEBooks(User tgUser) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(tgUser.getChatId());
        List<InputMedia> inputMediaList = new ArrayList<>();
        if (tgUser.getState().equals(BotState.FINISH_ORDER) || tgUser.getState().equals(BotState.SHARE_LOCATION)) {
            for (Order order : tgUser.getBasket()) {
                if (isDocumentFile(order.getBook().getBookUrl())) {
                    InputMediaDocument mediaDocument = new InputMediaDocument();
                    mediaDocument.setMedia(new File(order.getBook().getBookUrl()), order.getBook().getName());
                    inputMediaList.add(mediaDocument);
                }
            }
        } else if (tgUser.getState().equals(BotState.UPLOAD)) {
            for (Order order : orderList) {
                if (isDocumentFile(order.getBook().getBookUrl())) {
                    InputMediaDocument mediaDocument = new InputMediaDocument();
                    mediaDocument.setMedia(new File(order.getBook().getBookUrl()), order.getBook().getName());
                    inputMediaList.add(mediaDocument);
                }
            }
        }
        sendMediaGroup.setMedias(inputMediaList);
        return sendMediaGroup;
    }

    public static SendMediaGroup sendPrintedBooks(User tgUser) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(tgUser.getChatId());
        List<InputMedia> inputMediaList = new ArrayList<>();
        for (Order order : tgUser.getBasket()) {
            if (isImageFile(order.getBook().getBookUrl())) {
                InputMediaPhoto mediaPhoto = new InputMediaPhoto();
                mediaPhoto.setMedia(new File(order.getBook().getBookUrl()), order.getBook().getName());
                inputMediaList.add(mediaPhoto);
            }
        }
        sendMediaGroup.setMedias(inputMediaList);
        return sendMediaGroup;
    }

    public static SendMessage infoPrintedBooks(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        StringBuilder text = new StringBuilder();
        for (Order order : tgUser.getBasket()) {
            if (isImageFile(order.getBook().getBookUrl())) {
                text.append(order.getBook().getName()).append("\n");
            }
        }
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? text + "\nKitoblari tez orada yetkaziladi"
                : tgUser.getLanguage().equals(Lan.ENG) ? text + "\nThe books will be delivered soon"
                : text + "\nКниги скоро будет доставлена");
        return sendMessage;
    }


    // FINISH METHOD
    public static SendMessage finishPurchases(User tgUser) {
        tgUser.setBalance(tgUser.getBalance() - getTotalPriceInBasket(tgUser.getBasket()) * (1 + tgUser.getPayType().getCommission() / 100));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Xaridingiz uchun tashakkur \uD83E\uDD1D. Omad yordir dovyuraklarga!\uD83D\uDCAA\n\n\n/start \uD83D\uDD04 buyrug'i orqali bosh menyuga qaytib oling" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Thank you for your purchase\uD83E\uDD1D. Good luck to the brave!\uD83D\uDCAA\n\n" +
                        "Return to the main menu by the /start \uD83D\uDD04 command" :
                        "Спасибо за покупку\uD83E\uDD1D. Удачи смельчакам!\uD83D\uDCAA\n\n" +
                                "Вернитесь в главное меню с помощью команды /start \uD83D\uDD04");
        orderSettersAndMakePurchasesPdf(tgUser);
        return sendMessage;
    }


    // SHOW_BASKET MENU METHOD
    public static SendMessage showBasket(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        List<Order> userBasket = tgUser.getBasket();
        if (tgUser.getState().equals(BotState.SHOW_BOOK)) {
            Order newOrder = new Order(tgUser, getBookByIndex(tgUser), 1, getOrderStatus("BASKET"));
            boolean hasOrder = false;
            for (Order order : userBasket) {
                if (order.getBook().getName().equals(newOrder.getBook().getName())) {
                    hasOrder = true;
                    break;
                }
            }
            if (!hasOrder) {
                userBasket.add(newOrder);
                tgUser.setBasket(userBasket);
            }
        }
        if (userBasket.isEmpty()) {
            sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDED2 Savatchada hozircha kitob mavjud emas" :
                    tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDED2 There are not books in the basket yet" :
                            "\uD83D\uDED2 В корзине пока нет книг");
        } else {
            sendMessage.setText(
                    tgUser.getLanguage().equals(Lan.UZ) ?
                            "Jami summa: " + getTotalPriceInBasket(tgUser.getBasket()) + "\n⬇️ Siz tanlagan kitoblar ⬇️" :
                            tgUser.getLanguage().equals(Lan.ENG) ?
                                    "Total summa: " + getTotalPriceInBasket(tgUser.getBasket()) + "\n⬇️ Books on your choice ⬇️" :
                                    "Общая сумма: " + getTotalPriceInBasket(tgUser.getBasket()) + "\n⬇️ Книги по вашему выбору ⬇️");
        }
        tgUser.setState(BotState.SHOW_BASKET);
        saveUserChanges(tgUser);
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }

    // FILL_BALANCE MENU METHOD
    public static SendMessage fillUserBalance(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDCB3 Balance: " + tgUser.getBalance() +
                "\n\nBalansingizga qancha summa kiritmoqchisiz?" :
                tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDCB3 Balance: " + tgUser.getBalance() +
                        "\n\nHow much do you want to add to your balance?" :
                        "\uD83D\uDCB3 Balance: " + tgUser.getBalance() + "\n\nСколько вы хотите добавить на свой баланс?");
        return sendMessage;
    }

    // DOWNLOAD_PURCHASES MENU METHOD
    @SneakyThrows
    public static SendDocument downloadPurchasesPdfFile(User tgUser, File tgUserOrdersFile) {
        orderSettersAndMakePurchasesPdf(tgUser);
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(tgUser.getChatId());
        sendDocument.setDocument(new InputFile(tgUserOrdersFile));
        sendDocument.setCaption(tgUser.getLanguage().equals(Lan.UZ) ? "Xaridlar fayli" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Purchases file" :
                        "Файл покупки");
        return sendDocument;
    }

    // ABOUT MENU METHOD
    public static SendMessage about(User tgUser, String username) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText("https://" + username);
        return sendMessage;
    }

}
