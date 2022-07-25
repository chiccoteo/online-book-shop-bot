package uz.pdp.bot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.pdp.model.*;
import uz.pdp.model.enums.Lan;
import uz.pdp.model.enums.Role;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static uz.pdp.db.DB.*;
import static uz.pdp.services.AdminService.adminShowMenu;
import static uz.pdp.services.AdminService.*;
import static uz.pdp.services.BotService.*;
import static uz.pdp.services.GenerateKeyboardMarkup.nextOrBackButtonForSHowOrders;
import static uz.pdp.services.GenerateKeyboardMarkup.nextOrBackButtonForShowBooks;
import static uz.pdp.services.OrderService.*;

public class OnlineBookShopBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "t.me/online_bookshop_bot";
    }

    @Override
    public String getBaseUrl() {
        return super.getBaseUrl();
    }

    @Override
    public String getBotToken() {
        return "5059730003:AAGY7nH1fneU9TkMq3-Jr2axP3oXbqWMF84";
    }

    public static boolean back;
    public static boolean next;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        User user = getOrCreateTgUser(getChatId(update), update);
        if (user.getRole().equals(Role.ADMIN)) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasContact()) {
                    if (user.getState().equals(BotState.SHARE_CONTACT)) {
                        deleteReplyKeyboard(user);
                        execute(getContact(user, message.getContact()));
                    }
                } else if (message.getText().equals("/start")) {
                    execute(start(update));
                } else if (user.getState().equals(BotState.ENTER_CODE)) {
                    if (message.getText().equals(String.valueOf(code))) {
                        execute(adminShowMenu(user));
                    } else
                        execute(new SendMessage(user.getChatId(),
                                user.getLanguage().equals(Lan.UZ) ? "Tasdiqlash kodi xato kiritildi.\n" +
                                        "Iltimos, to'g'ri kiritishga harakat qiling!" :
                                        user.getLanguage().equals(Lan.ENG) ? "Confirmation code error entered.\n" +
                                                "Please try to enter correctly!" :
                                                "Введена ошибка кода подтверждения.\n" +
                                                        "Пожалуйста, попробуйте ввести правильно!"));
                } else if (user.getState().equals(BotState.ADD_CATEGORY)){
                    String text = message.getText();
                    String newCategoryUz = text.substring(0, text.indexOf("_"));
                    String newCategoryEng = text.substring(text.indexOf(",")+2, text.lastIndexOf(",")-4);
                    String newCategoryRu = text.substring(text.lastIndexOf(",")+2, text.lastIndexOf("_"));
                    if (user.getAdminChosenCategory()==null){
                        categoryList.add(new Category(
                                newCategoryUz, Lan.UZ
                        ));
                        categoryList.add(new Category(
                                newCategoryEng, Lan.ENG
                        ));
                        categoryList.add(new Category(
                                newCategoryRu, Lan.RU
                        ));
                    } else {
//                        for (Category category : categoryList) {
//                            if (category.equals(user.getAdminChosenCategory())){
//                                category.getCategoryList().setCategoryList(
//                                        Arrays.asList(
//                                                new Category(newCategoryUz, Lan.UZ),
//                                                new Category(newCategoryEng, Lan.ENG),
//                                                new Category(newCategoryRu, Lan.RU)
//                                        )
//                                );
//                                break;
//                            }
//                        }
//                        user.setAdminChosenCategory(null);
//                        saveUserChanges(user);
                    }
                    execute(new SendMessage(user.getChatId(), "OK"));
                }
            } else if (update.hasCallbackQuery()) {
                String data = update.getCallbackQuery().getData();
                if (data.equals("CATEGORY_CRUD")) {
                    user.setState(BotState.CATEGORY_CRUD);
                    saveUserChanges(user);
                    execute(addCategory(user));
                } else if (data.equals("ADD_CATEGORY")){
                    user.setState(BotState.ADD_CATEGORY);
                    saveUserChanges(user);
                    execute(addCategory(user));
                } else if (data.equals("ADD_INNER_CATEGORY")){
                    user.setState(BotState.ADD_INNER_CATEGORY);
                    saveUserChanges(user);
                    execute(addInnerCategory(user, update));
                } else if (data.startsWith("CATEGORY_ID:")){
                    String id = data.substring(data.indexOf(":")+1);
                    UUID uuid = UUID.fromString(id);
                    for (Category category : categoryList) {
                        if (category.getId().equals(uuid)){
                            user.setAdminChosenCategory(category);
                            saveUserChanges(user);
                            break;
                        }
                    }
                    user.setState(BotState.ADD_CATEGORY);
                    saveUserChanges(user);
                    execute(addCategory(user));
                }
                else if (data.equals("BOOK_CRUD")) {
                    user.setState(BotState.BOOK_CRUD);
                    saveUserChanges(user);
                    execute(bookCRUD(user));
                } else if (data.equals("PAY_TYPE_CRUD")) {
                    user.setState(BotState.PAY_TYPE_CRUD);
                    saveUserChanges(user);
                    execute(payTypeCRUD(user));
                } else if (data.equals("ORDER_STATUS_CRUD")) {
                    user.setState(BotState.ORDER_STATUS_CRUD);
                    saveUserChanges(user);
                    execute(orderStatusCRUD(user));
                } else if (data.equals("CHANGE_ORDER_STATUS") || data.equals("NEXT")) {
                    user.setState(BotState.SHOW_ORDERS);
                    if (data.equals("CHANGE_ORDER_STATUS"))
                        user.setTempOrderPage(1);
                    else user.setTempOrderPage(user.getTempOrderPage()+1);
                    saveUserChanges(user);
                    nextOrBackButtonForSHowOrders(user.getTempOrderPage(), orderList);
                    if (data.equals("CHANGE_ORDER_STATUS"))
                        execute(showOrders(user));
                    else
                        execute(showNextBackOrders(user, update));
                } else if (data.equals("BACK")){
                    user.setState(BotState.SHOW_ORDERS);
                    user.setTempOrderPage(user.getTempOrderPage()-1);
                    saveUserChanges(user);
                    nextOrBackButtonForSHowOrders(user.getTempOrderPage(), orderList);
                    execute(showNextBackOrders(user, update));
                } else if (data.equals("EXCEL_ORDERS")) {
                    if (orderList.isEmpty()){
                        execute(new SendMessage(user.getChatId(), "Buyurtmalar yo'q"));
                    } else {
                        user.setState(BotState.DOWNLOAD_ORDERS);
                        saveUserChanges(user);
                        execute(downloadOrders(user));
                    }
                    execute(adminShowMenu(user));
                } else if (data.equals("PDF_USERS")) {
                    user.setState(BotState.DOWNLOAD_USERS);
                    saveUserChanges(user);
                    execute(downloadUsers(user));
                    execute(adminShowMenu(user));
                } else if (data.equals("ADMIN_MENU")){
                    execute(adminShowMenu(user));
                } else if (data.startsWith("ORDER_INDEX:")){
                    Integer index = Integer.parseInt(data.substring(data.indexOf(":")+1));
                    user.setChosenOrderIndex(index);
                    user.setState(BotState.CHANGE_ORDER_STATUS);
                    saveUserChanges(user);
                    execute(changeOrderStatus(user));
                } else if (data.startsWith("ORDER_STATUS:")){
                    String status = data.substring(data.indexOf(":")+1);
                    execute(setNewOrderStatus(user, status));
                    execute(adminShowMenu(user));
                }
            }
        }
        // USERS
        else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasContact()) {
                if (user.getState().equals(BotState.SHARE_CONTACT)) {
                    deleteReplyKeyboard(user);
                    execute(getContact(user, message.getContact()));
                }
            } else if (message.hasLocation()) {
                if (user.getState().equals(BotState.SHARE_LOCATION)) {
                    deleteReplyKeyboard(user);

                    if (hasOneAudioBookInBasket(user) == 1)
                        execute(sendAudioBook(user));
                    else if (hasOneAudioBookInBasket(user) != 0)
                        execute(sendAudioBooks(user));

                    if (hasOneEBookInBasket(user) == 1)
                        execute(sendEBook(user));
                    else if (hasOneEBookInBasket(user) != 0)
                        execute(sendEBooks(user));

                    if (hasOnePrintedBookInBasket(user) == 1)
                        execute(sendPrintedBook(user));
                    else if (hasOnePrintedBookInBasket(user) != 0) {
                        execute(sendPrintedBooks(user));
                        execute(infoPrintedBooks(user));
                    }

                    execute(setOrderLocationOrAddress(update, user));
                }
            } else {
                if (message.getText().equals("/start")) {
                    user.setBasket(new ArrayList<>());
                    execute(start(update));
                } else if (user.getState().equals(BotState.CHOOSE_LANG) ||
                        user.getState().equals(BotState.CHANGE_LANG)) {
                    if (user.getState().equals(BotState.CHANGE_LANG)) {
                        deleteReplyKeyboard(user);
                    }
                    execute(getUserLang(user, message.getText()));
                } else if (user.getState().equals(BotState.ENTER_CODE)) {
                    if (message.getText().equals(String.valueOf(code))) {
                        execute(showMenu(user));
                    } else
                        execute(new SendMessage(user.getChatId(),
                                user.getLanguage().equals(Lan.UZ) ? "Tasdiqlash kodi xato kiritildi.\n" +
                                        "Iltimos, to'g'ri kiritishga harakat qiling!" :
                                        user.getLanguage().equals(Lan.ENG) ? "Confirmation code error entered.\n" +
                                                "Please try to enter correctly!" :
                                                "Введена ошибка кода подтверждения.\n" +
                                                        "Пожалуйста, попробуйте ввести правильно!"));
                } else if (user.getState().equals(BotState.SHOW_BOOK)) {
                    if (message.getText().equals("/enterpage")) {
                        user.setState(BotState.ENTER_PAGE);
                        saveUserChanges(user);
                        execute(enterBooksPage(user));
                    }
                } else if (user.getState().equals(BotState.ENTER_PAGE)) {
                    user.setState(BotState.SHOW_BOOK);
                    saveUserChanges(user);
                    int booksCount = bookListByCategory(user, user.getTempCategory().getId()).size();
                    int maxPage = (int) Math.ceil((double) booksCount / PAGE_SIZE);
                    if (Integer.parseInt(message.getText()) < 1 || Integer.parseInt(message.getText()) > maxPage) {
                        execute(new SendMessage(user.getChatId(),
                                user.getLanguage().equals(Lan.UZ) ? "Bunday sahifa mavjud emas!" :
                                        user.getLanguage().equals(Lan.ENG) ? "Such a page does not exist!" :
                                                "Такой страницы не существует!"));
                        execute(showBooks(user, bookListByCategory(user, user.getTempCategory().getId())));
                    } else {
                        user.setTempBookPage(Integer.valueOf(message.getText()));
                        saveUserChanges(user);
                        nextOrBackButtonForShowBooks(user.getTempBookPage(),
                                bookListByCategory(user, user.getTempCategory().getId()));
                        execute(showBooks(user, bookListByCategory(user, user.getTempCategory().getId())));
                    }
                } else if (user.getState().equals(BotState.ENTER_AMOUNT)) {
                    user.setState(BotState.SHOW_BASKET);
                    saveUserChanges(user);
                    execute(setPrintedBookAmount(Integer.parseInt(update.getMessage().getText()), user));
                } else if (user.getState().equals(BotState.FILL_BALANCE)) {
                    boolean wrong = false;
                    for (int i = 0; i < message.getText().length(); i++) {
                        if (!Character.isDigit(message.getText().charAt(i))) {
                            wrong = true;
                            break;
                        }
                    }
                    if (wrong){
                        execute(new SendMessage(user.getChatId(),"Iltimos, nomanfiy bo'lgan son kiriting"));
                        execute(showMenu(user));
                    }else {
                        user.setBalance(user.getBalance() + Double.parseDouble(message.getText()));
                        execute(new SendMessage(user.getChatId(), "\uD83D\uDCB3 Balance: " + user.getBalance()));
                        execute(showMenu(user));
                    }
                } else if (user.getState().equals(BotState.SHARE_LOCATION)) {
                    deleteReplyKeyboard(user);

                    if (hasOneAudioBookInBasket(user) == 1)
                        execute(sendAudioBook(user));
                    else if (hasOneAudioBookInBasket(user) != 0)
                        execute(sendAudioBooks(user));

                    if (hasOneEBookInBasket(user) == 1)
                        execute(sendEBook(user));
                    else if (hasOneEBookInBasket(user) != 0)
                        execute(sendEBooks(user));

                    if (hasOnePrintedBookInBasket(user) == 1)
                        execute(sendPrintedBook(user));
                    else if (hasOnePrintedBookInBasket(user) != 0)
                        execute(sendPrintedBooks(user));

                    execute(setOrderLocationOrAddress(update, user));
                }
            }

        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if (data.equals("BOOKS")) {
                user.setState(BotState.SHOW_BOOK_CATEGORY);
                saveUserChanges(user);
                execute(showBookCategory(user));
            } else if (data.startsWith("MAIN:")) {
                String uuid = data.substring(data.indexOf(":") + 1);
                Category tempCategory = new Category();
                for (Category category : categoryList) {
                    UUID id = UUID.fromString(uuid);
                    if (category.getId().equals(id)) {
                        tempCategory = category;
                        break;
                    }
                }
                user.setTempCategory(tempCategory);
                user.setTempBookPage(0);
                user.setState(BotState.SHOW_BOOK_TYPE);
                saveUserChanges(user);
                execute(chooseBookType(user));
            } else if (data.startsWith("BOOK_TYPE:") || data.startsWith("NEXT:")) {
                setUserTempCategory(user, data.substring(data.indexOf(":") + 1));
                String uuid = data.substring(data.indexOf(":") + 1);
                UUID id = UUID.fromString(uuid);
                user.setState(BotState.SHOW_BOOK);
                if (data.startsWith("BOOK_TYPE:") && user.getTempBookPage() != 0)
                    user.setTempBookPage(0);
                user.setTempBookPage(user.getTempBookPage() + 1);
                saveUserChanges(user);
                nextOrBackButtonForShowBooks(user.getTempBookPage(), bookListByCategory(user, id));
                if (data.startsWith("BOOK_TYPE:")) {
                    execute(showBooks(user, bookListByCategory(user, user.getTempCategory().getId())));
                } else if (data.startsWith("NEXT:")) {
                    execute(showBackNextBooks(update, user, bookListByCategory(user, user.getTempCategory().getId())));
                }
            } else if (data.startsWith("BACK:")) {
                user.setState(BotState.SHOW_BOOK);
                user.setTempBookPage(user.getTempBookPage() - 1);
                saveUserChanges(user);
                nextOrBackButtonForShowBooks(user.getTempBookPage(), bookListByCategory(user, user.getTempCategory().getId()));
                execute(showBackNextBooks(update, user, bookListByCategory(user, user.getTempCategory().getId())));
            } else if (data.startsWith("NUMBER:")) {
                int index = Integer.parseInt(data.substring(data.indexOf(":") + 1));
                index = index - 1 + PAGE_SIZE * (user.getTempBookPage() - 1);
                user.setChosenBookIndex(index);
                saveUserChanges(user);
                if (isImageFile(bookListByCategory(user, user.getTempCategory().getId()).get(0).getBookUrl())) {
                    user.setState(BotState.ENTER_AMOUNT);
                    saveUserChanges(user);
                    execute(enterPrintedBookAmount(user));
                } else
                    execute(showBasket(user));
            } else if (data.startsWith("AMOUNT:")) {
                user.setState(BotState.SHOW_BASKET);
                saveUserChanges(user);
                String amount = data.substring(data.indexOf(":") + 1);
                execute(setPrintedBookAmount(Integer.parseInt(amount), user));
            } else if (data.startsWith("DELETE:")) {
                execute(deleteOrderInBasket(user, UUID.fromString(data.substring(data.indexOf(":") + 1))));
            } else if (data.startsWith("PAY_TYPE:")) {
                for (PayType payType : payTypeList) {
                    if (payType.getName().equals(data.substring(data.indexOf(":") + 1))) {
                        user.setPayType(payType);
                        saveUserChanges(user);
                        break;
                    }
                }

                boolean hasPrintedBook = false;
                for (Order order : user.getBasket()) {
                    if (isImageFile(order.getBook().getBookUrl())) {
                        hasPrintedBook = true;
                        break;
                    }
                }
                if (hasPrintedBook) {
                    user.setState(BotState.SHARE_LOCATION);
                    saveUserChanges(user);
                    execute(askLocation(user));
                } else
                    execute(askBuyOrNot(user));

                double totalSumma = getTotalPriceInBasket(user.getBasket()) * (1 + user.getPayType().getCommission() / 100);
                if (totalSumma > user.getBalance()) {
                    user.setState(BotState.SHOW_MENU);
                    saveUserChanges(user);
                    execute(showMenu(user));
                }
            } else if (data.equals("YES")) {
                user.setState(BotState.FINISH_ORDER);
                saveUserChanges(user);

                if (hasOneAudioBookInBasket(user) == 1)
                    execute(sendAudioBook(user));
                else if (hasOneAudioBookInBasket(user) != 0)
                    execute(sendAudioBooks(user));

                if (hasOneEBookInBasket(user) == 1)
                    execute(sendEBook(user));
                else if (hasOneEBookInBasket(user) != 0)
                    execute(sendEBooks(user));

                if (hasOnePrintedBookInBasket(user) == 1)
                    execute(sendPrintedBook(user));
                else if (hasOnePrintedBookInBasket(user) != 0)
                    execute(sendPrintedBooks(user));

                execute(finishPurchases(user));
            } else if (data.equals("NO")) {
                user.setBasket(new ArrayList<>());
                execute(showMenu(user));
            } else if (data.equals("MAIN_MENU")) {
                execute(showMenu(user));
            } else if (data.equals("CHOOSE_BOOK")) {
                user.setState(BotState.SHOW_BOOK_CATEGORY);
                saveUserChanges(user);
                execute(showBookCategory(user));
            } else if (data.equals("FINISH_PURCHASES")) {
                user.setState(BotState.CHOOSE_PAY_TYPE);
                saveUserChanges(user);
                execute(choosePayType(user));
            } else if (data.equals("BASKET")) {
                user.setState(BotState.SHOW_BASKET);
                saveUserChanges(user);
                execute(showBasket(user));
            } else if (data.equals("UPLOAD")) {
                user.setState(BotState.UPLOAD);
                saveUserChanges(user);
                boolean noAudio = false, noEBook = false;
                if (hasOneAudioBookInBasket(user) == 1) {
                    execute(sendAudioBook(user));
                } else if (hasOneAudioBookInBasket(user) != 0) {
                    execute(sendAudioBooks(user));
                } else
                    noAudio = true;

                if (hasOneEBookInBasket(user) == 1) {
                    execute(sendEBook(user));
                } else if (hasOneEBookInBasket(user) != 0) {
                    execute(sendEBooks(user));
                } else
                    noEBook = true;

                if (noAudio && noEBook) {
                    execute(new SendMessage(user.getChatId(),
                            user.getLanguage().equals(Lan.UZ) ? "Kechirasiz, siz audio yoki elektron kitob sotib olmagansiz!" :
                                    user.getLanguage().equals(Lan.ENG) ? "Sorry, you didn't buy an audio or e-book!" :
                                            "Извините, вы не купили аудио или электронную книгу!"));
                }
                execute(showMenu(user));
            } else if (data.equals("DOWNLOAD_PURCHASES")) {
                user.setState(BotState.SHOW_PURCHASES);
                saveUserChanges(user);
                File tempPurchasesFile = null;
                for (UserOrdersFile userOrdersFile : ordersFileList) {
                    if (userOrdersFile.getUser().getId().equals(user.getId()))
                        tempPurchasesFile = userOrdersFile.getOrdersFile();
                }
                if (tempPurchasesFile != null)
                    execute(downloadPurchasesPdfFile(user, tempPurchasesFile));
                else {
                    execute(new SendMessage(user.getChatId(),
                            user.getLanguage().equals(Lan.UZ) ? "Kechirasiz, siz hali kitob xarid qilmagansiz!" :
                                    user.getLanguage().equals(Lan.ENG) ? "Sorry, you haven't bought a book yet!" :
                                            "Извините, вы еще не купили книгу!"));
                }
                execute(showMenu(user));
            } else if (data.equals("FILL_BALANCE")) {
                user.setState(BotState.FILL_BALANCE);
                saveUserChanges(user);
                execute(fillUserBalance(user));
            } else if (data.equals("CHANGE_LAN")) {
                user.setState(BotState.CHANGE_LANG);
                saveUserChanges(user);
                execute(chooseLanguage(update));
            } else if (data.equals("ABOUT")) {
                user.setState(BotState.ABOUT_US);
                saveUserChanges(user);
                execute(about(user, getBotUsername()));
                execute(showMenu(user));
            }
        }

    }

    @SneakyThrows
    public void deleteReplyKeyboard(User user) {
        SendMessage sendMessageRemove = new SendMessage();
        sendMessageRemove.setChatId(user.getChatId());
        sendMessageRemove.setText(".");
        sendMessageRemove.setReplyMarkup(new ReplyKeyboardRemove(true));
        Message message = execute(sendMessageRemove);
        DeleteMessage deleteMessage = new DeleteMessage(user.getChatId(), message.getMessageId());
        execute(deleteMessage);
    }

    public static List<Book> bookListByCategory(User user, UUID categoryId) {
        List<Book> categoryBookList = new ArrayList<>();
        Category tempCategory = new Category();
        for (Category category : categoryList) {
            if (category.getId().equals(user.getTempCategory().getId())) {
                for (Category bookType : category.getCategoryList()) {
                    if (bookType.getId().equals(categoryId)) {
                        tempCategory = bookType;
                        break;
                    }
                }
            } else
                tempCategory = user.getTempCategory();
        }
        for (Book book : bookList) {
            for (UUID uuid : book.getCategoryUUIDList()) {
                if (uuid.equals(tempCategory.getId())) {
                    categoryBookList.add(book);
                }
            }
        }
        return categoryBookList;
    }

    public static void setUserTempCategory(User user, String categoryId) {
        Category tempCategory = new Category();
        for (Category category : categoryList) {
            for (Category bookType : category.getCategoryList()) {
                if (bookType.getId().equals(UUID.fromString(categoryId))) {
                    tempCategory = bookType;
                }
            }
        }
        user.setTempCategory(tempCategory);
    }

}
