package uz.pdp.services;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.bot.BotState;
import uz.pdp.model.*;
import uz.pdp.model.enums.Lan;

import java.util.ArrayList;
import java.util.List;

import static uz.pdp.bot.OnlineBookShopBot.*;
import static uz.pdp.db.DB.*;
import static uz.pdp.services.OrderService.isImageFile;

public class GenerateKeyboardMarkup {

    public static ReplyKeyboard generateReplyKeyboardMarkup(User tgUser) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        List<KeyboardRow> rowList = new ArrayList<>();
        switch (tgUser.getState()) {
            case BotState.CHOOSE_LANG:
            case BotState.CHANGE_LANG: {
                KeyboardButton button1 = new KeyboardButton();
                button1.setText("O'zbek \uD83C\uDDFA\uD83C\uDDFF");
                keyboardRow.add(button1);
                KeyboardButton button2 = new KeyboardButton();
                button2.setText("English \uD83C\uDDEC\uD83C\uDDE7");
                keyboardRow.add(button2);
                KeyboardButton button3 = new KeyboardButton();
                button3.setText("Ruscha \uD83C\uDDF7\uD83C\uDDFA");
                keyboardRow.add(button3);
                rowList.add(keyboardRow);
                break;
            }
            case BotState.SHARE_CONTACT: {
                KeyboardButton button = new KeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "JO'NATISH \uD83D\uDCF1" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "SHARE CONTACT \uD83D\uDCF1" :
                                "ОТПРАВИТЬ \uD83D\uDCF1");
                button.setRequestContact(true);
                keyboardRow.add(button);
                rowList.add(keyboardRow);
                break;
            }
            case BotState.SHARE_LOCATION: {
                KeyboardButton button = new KeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "JO'NATISH \uD83D\uDCCD" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "SHARE LOCATION \uD83D\uDCCD" :
                                "ОТПРАВИТЬ \uD83D\uDCCD"
                );
                button.setRequestLocation(true);
                keyboardRow.add(button);
                rowList.add(keyboardRow);
                break;
            }
        }
        replyKeyboardMarkup.setKeyboard(rowList);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboard generateInlineKeyboardMarkup(User tgUser) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;
        switch (tgUser.getState()) {
            case BotState.SHOW_ADMIN_MENU: {
                row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("Category CRUD");
                button.setCallbackData("CATEGORY_CRUD");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText("Book CRUD");
                button.setCallbackData("BOOK_CRUD");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText("PayType CRUD");
                button.setCallbackData("PAY_TYPE_CRUD");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText("OrderStatus CRUD");
                button.setCallbackData("ORDER_STATUS_CRUD");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText("Change order status");
                button.setCallbackData("CHANGE_ORDER_STATUS");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText("Excel file of orders");
                button.setCallbackData("EXCEL_ORDERS");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText("Pdf file of users");
                button.setCallbackData("PDF_USERS");
                row.add(button);
                rows.add(row);
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.CATEGORY_CRUD: {
                row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("Add category");
                button.setCallbackData("ADD_CATEGORY");
                row.add(button);
                button = new InlineKeyboardButton();
                button.setText("Add inner category");
                button.setCallbackData("ADD_INNER_CATEGORY");
                row.add(button);
                rows.add(row);
                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText("Main menu");
                button.setCallbackData("ADMIN_MENU");
                row.add(button);
                rows.add(row);
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.ADD_INNER_CATEGORY: {
                row = new ArrayList<>();
                int i = 0;
                for (int j = 0; j < categoryList.size()/3; j++) {
                    if (i == 5) {
                        rows.add(row);
                        row = new ArrayList<>();
                    }
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(numbersEmojis.get(i));
                    button.setCallbackData("CATEGORY_ID:" + categoryList.get(j).getId());
                    row.add(button);
                    i++;
                }
                rows.add(row);
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.SHOW_ORDERS: {
                row = new ArrayList<>();
                int j = 0;
                for (int i = (tgUser.getTempOrderPage() - 1) * PAGE_SIZE;
                     i < (Math.min(PAGE_SIZE * tgUser.getTempOrderPage(), orderList.size())); i++) {
                    if (j == 5) {
                        rows.add(row);
                        row = new ArrayList<>();
                    }
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(numbersEmojis.get(j));
                    button.setCallbackData("ORDER_INDEX:" + i);
                    row.add(button);
                    j++;
                }
                rows.add(row);

                row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("Bosh menu");
                button.setCallbackData("ADMIN_MENU");

                InlineKeyboardButton button1 = new InlineKeyboardButton();
                button1.setText("⬅️");
                button1.setCallbackData("BACK");

                InlineKeyboardButton button2 = new InlineKeyboardButton();
                button2.setText("➡️");
                button2.setCallbackData("NEXT");

                if (back && next) {
                    row.add(button1);
                    row.add(button);
                    row.add(button2);
                } else if (back) {
                    row.add(button1);
                    row.add(button);
                } else if (next) {
                    row.add(button);
                    row.add(button2);
                } else {
                    row.add(button);
                }
                rows.add(row);
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.CHANGE_ORDER_STATUS: {
                row = new ArrayList<>();
                int i = 0;
                for (OrderStatus orderStatus : orderStatusList) {
                    if (i == 2) {
                        rows.add(row);
                        row = new ArrayList<>();
                    }
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(orderStatus.getName());
                    button.setCallbackData("ORDER_STATUS:" + orderStatus.getName());
                    row.add(button);
                    i++;
                }
                rows.add(row);
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }

            case BotState.SHOW_MENU: {
                row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Kitoblar \uD83D\uDCDA" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "Books \uD83D\uDCDA" :
                                "Книги \uD83D\uDCDA");
                button.setCallbackData("BOOKS");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Savatchani ko'rish \uD83D\uDED2" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "View the basket \uD83D\uDED2" :
                                "Посмотреть корзину \uD83D\uDED2");
                button.setCallbackData("BASKET");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Fayllarni qayta yuklash ↪️" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "Upload files ↪️" :
                                "Загрузить файлы ↪️");
                button.setCallbackData("UPLOAD");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Xaridlar PDF file da \uD83D\uDCC1\n" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "Purchases in PDF file \uD83D\uDCC1\n" :
                                "Покупки в PDF файле \uD83D\uDCC1\n");
                button.setCallbackData("DOWNLOAD_PURCHASES");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Balansni to'ldirish \uD83D\uDCB5" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "Filling the balance \uD83D\uDCB5" :
                                "Пополнить баланс \uD83D\uDCB5");
                button.setCallbackData("FILL_BALANCE");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Tilni o'zgartirish \uD83D\uDC45" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "Change language \uD83D\uDC45" :
                                "Изменить язык \uD83D\uDC45");
                button.setCallbackData("CHANGE_LAN");
                row.add(button);
                rows.add(row);

                row = new ArrayList<>();
                button = new InlineKeyboardButton();
                button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "Bot haqida \uD83E\uDD16" :
                        tgUser.getLanguage().equals(Lan.ENG) ? "About Bot \uD83E\uDD16" :
                                "О боте \uD83E\uDD16");
                button.setCallbackData("ABOUT");
                row.add(button);
                rows.add(row);
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.SHOW_BOOK_CATEGORY: {
                for (Category category : categoryList) {
                    row = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    if (category.getLanguage().equals(tgUser.getLanguage())) {
                        button.setText(category.getName());
                        button.setCallbackData("MAIN:" + category.getId());
                        row.add(button);
                        rows.add(row);
                    }
                }
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.SHOW_BOOK_TYPE: {
                int j = 0;
                for (Category category : tgUser.getTempCategory().getCategoryList()) {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    row = new ArrayList<>();
                    button.setText(category.getName() + " " + bookTypeEmojis.get(j));
                    button.setCallbackData("BOOK_TYPE:" + category.getId());
                    row.add(button);
                    rows.add(row);
                    j++;
                }
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.SHOW_BOOK: {
                inlineKeyboardMarkup.setKeyboard(forShowBookButtons(tgUser, bookListByCategory(tgUser, tgUser.getTempCategory().getId())));
                break;
            }
            case BotState.SHOW_BASKET: {
                row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                List<Order> userBasket = tgUser.getBasket();
                if (userBasket.isEmpty()) {
                    button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDD1D Bosh menu" :
                            tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDD1D Main menu" :
                                    "\uD83D\uDD1D Выберите меню");
                    button.setCallbackData("MAIN_MENU");
                    row.add(button);

                    button = new InlineKeyboardButton();
                    button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDCD4 Kitob tanlash" :
                            tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDCD4 Choose book" :
                                    "\uD83D\uDCD4 Выбор книги");
                    button.setCallbackData("CHOOSE_BOOK");
                    row.add(button);
                } else {
                    for (Order order : userBasket) {
                        button = new InlineKeyboardButton();
                        row = new ArrayList<>();
                        if (isImageFile(order.getBook().getBookUrl()))
                            button.setText("❌ " + order.getBook().getName() + "  " + order.getAmount() + " ❌");
                        else
                            button.setText("❌ " + order.getBook().getName() + " ❌");
                        button.setCallbackData("DELETE:" + order.getId());
                        row.add(button);
                        rows.add(row);
                    }
                    row = new ArrayList<>();
                    button = new InlineKeyboardButton();
                    button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDD1D Bosh menu" :
                            tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDD1D Main menu" :
                                    "\uD83D\uDD1D Выберите меню");
                    button.setCallbackData("MAIN_MENU");
                    row.add(button);

                    button = new InlineKeyboardButton();
                    button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDCD4 Yana kitob tanlash" :
                            tgUser.getLanguage().equals(Lan.ENG) ? "\uD83D\uDCD4 Choose another book" :
                                    "\uD83D\uDCD4 Выбрать другую книгу");
                    button.setCallbackData("CHOOSE_BOOK");
                    row.add(button);

                    button = new InlineKeyboardButton();
                    button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83C\uDFC1 Xaridni yakunlash" :
                            tgUser.getLanguage().equals(Lan.ENG) ? "\uD83C\uDFC1 Finish the purchase" :
                                    "\uD83C\uDFC1 Завершите покупку");
                    button.setCallbackData("FINISH_PURCHASES");
                    row.add(button);
                }
                rows.add(row);
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.ENTER_AMOUNT: {
                row = new ArrayList<>();
                InlineKeyboardButton button;
                for (int i = 1; i <= 10; i++) {
                    if (i == 6) {
                        rows.add(row);
                        row = new ArrayList<>();
                    }
                    button = new InlineKeyboardButton();
                    button.setText(numbersEmojis.get(i - 1));
                    button.setCallbackData("AMOUNT:" + i);
                    row.add(button);
                }
                rows.add(row);
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
            case BotState.CHOOSE_PAY_TYPE: {
                for (PayType payType : payTypeList) {
                    row = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(payType.getName());
                    button.setCallbackData("PAY_TYPE:" + payType.getName());
                    row.add(button);
                    rows.add(row);
                }
                inlineKeyboardMarkup.setKeyboard(rows);
                break;
            }
        }
        return inlineKeyboardMarkup;
    }

    public static List<List<InlineKeyboardButton>> forShowBookButtons(User tgUser, List<Book> bookList) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0;
             ((PAGE_SIZE * tgUser.getTempBookPage() < bookList.size()) ?
                     i < PAGE_SIZE : i < (bookList.size()) - PAGE_SIZE * (tgUser.getTempBookPage() - 1)); i++) {
            if (i == 5) {
                rows.add(row);
                row = new ArrayList<>();
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(numbersEmojis.get(i));
            button.setCallbackData("NUMBER:" + (i + 1));
            row.add(button);
        }
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(tgUser.getLanguage().equals(Lan.UZ) ? "\uD83D\uDD1D Bosh menu" :
                (tgUser.getLanguage().equals(Lan.ENG)) ? "\uD83D\uDD1D Main menu" :
                        "\uD83D\uDD1D Выберите меню");
        button.setCallbackData("MAIN_MENU");

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("⬅️");
        button1.setCallbackData("BACK:" + tgUser.getTempCategory().getId());

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("➡️");
        button2.setCallbackData("NEXT:" + tgUser.getTempCategory().getId());

        if (back && next) {
            row.add(button1);
            row.add(button);
            row.add(button2);
        } else if (back) {
            row.add(button1);
            row.add(button);
        } else if (next) {
            row.add(button);
            row.add(button2);
        } else {
            row.add(button);
        }
        rows.add(row);

        return rows;
    }

    public static void nextOrBackButtonForShowBooks(Integer userTempPage, List<Book> bookList) {
        int booksCount = bookList.size();
        int maxPage = (int) Math.ceil((double) booksCount / PAGE_SIZE);
        if (maxPage == 1) {
            back = false;
            next = false;
        } else if (userTempPage == 1) {
            back = false;
            next = true;
        } else if (userTempPage == maxPage) {
            back = true;
            next = false;
        } else {
            back = true;
            next = true;
        }
    }

    public static void nextOrBackButtonForSHowOrders(Integer adminOrderPage, List<Order> orderList) {
        int ordersCount = orderList.size();
        int maxPage = (int) Math.ceil((double) ordersCount / PAGE_SIZE);
        if (maxPage == 1) {
            back = false;
            next = false;
        } else if (adminOrderPage == 1) {
            back = false;
            next = true;
        } else if (adminOrderPage == maxPage) {
            back = true;
            next = false;
        } else {
            back = true;
            next = true;
        }
    }
}
