package uz.pdp.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.bot.BotState;
import uz.pdp.model.*;
import uz.pdp.model.enums.Lan;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static uz.pdp.bot.OnlineBookShopBot.bookListByCategory;
import static uz.pdp.db.DB.*;
import static uz.pdp.services.BotService.finishPurchases;
import static uz.pdp.services.BotService.saveUserChanges;

public class OrderService {

    public static Double getTotalPriceInBasket(List<Order> basketList) {
        Double total = 0.0;
        for (Order order : basketList) {
            if (order.getAmount() != null)
                total += order.getBook().getPrice() * order.getAmount();
            else
                total += order.getBook().getPrice();
        }
        return total;
    }

    public static void saveOrderChangesOrAddOrderToOrderList(Order changedOrder) {
        boolean hasOrder = false;
        for (Order order : orderList) {
            if (order.getId().equals(changedOrder.getId())) {
                hasOrder = true;
                order = changedOrder;
            }
        }
        if (!hasOrder)
            orderList.add(changedOrder);
    }

    public static Book getBookByIndex(User tgUser) {
        return bookListByCategory(tgUser, tgUser.getTempCategory().getId()).get(tgUser.getChosenBookIndex());
    }

    public static OrderStatus getOrderStatus(String orderStatusName) {
        for (OrderStatus orderStatus : orderStatusList) {
            if (orderStatus.getName().equals(orderStatusName)) {
                return orderStatus;
            }
        }
        return new OrderStatus("NEW");
    }

    @SneakyThrows
    public static void orderSettersAndMakePurchasesPdf(User tgUser) {
        if (tgUser.getState().equals(BotState.FINISH_ORDER)) {
            List<Order> basket = tgUser.getBasket();
            for (Order order : basket) {
                order.setPayment(new Payment(order.getBook().getPrice() * order.getAmount(), tgUser.getPayType()));
                if (!isImageFile(order.getBook().getBookUrl())) {
                    order.setOrderDate(new Timestamp(new Date().getTime()));
                    order.setOrderStatus(getOrderStatus("COMPLETED"));
                    saveOrderChangesOrAddOrderToOrderList(order);
                }
            }
        }
        Document document = new Document();
        File file = new File("src/main/resources/userOrders/" + tgUser.getUsername() + "_purchases.pdf");
        PdfWriter.getInstance(document, new FileOutputStream(file, true));
        document.open();
        float[] widths = {0.03f, 0.15f, 0.05f, 0.07f, 0.1f};
        PdfPTable table = new PdfPTable(widths);
        table.addCell(tgUser.getLanguage().equals(Lan.UZ) ? "T/r" :
                tgUser.getLanguage().equals(Lan.ENG) ? "S/n" :
                        "П/ч");
        table.addCell(tgUser.getLanguage().equals(Lan.UZ) ? "Kitob nomi" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Book name" :
                        "Название книги");
        table.addCell(tgUser.getLanguage().equals(Lan.UZ) ? "Soni" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Amount" :
                        "Количество");
        table.addCell(tgUser.getLanguage().equals(Lan.UZ) ? "Narxi" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Price" :
                        "Цена");
        table.addCell(tgUser.getLanguage().equals(Lan.UZ) ? "Holati" :
                tgUser.getLanguage().equals(Lan.ENG) ? "Status" :
                        "Статус");
        int i = 1;
        for (Order order : orderList) {
            if (order.getUser().getId().equals(tgUser.getId())) {
                table.addCell(String.valueOf(i));
                table.addCell(order.getBook().getName());
                table.addCell(String.valueOf(order.getAmount()));
                table.addCell(String.valueOf(order.getPayment().getPaySum()));
                table.addCell(order.getOrderStatus().getName());
                i++;
            }
        }
        document.add(table);
        document.close();
        ordersFileList.add(new UserOrdersFile(file, tgUser));
    }

    public static SendMessage setOrderLocationOrAddress(Update update, User tgUser) {
        if (update.getMessage().hasLocation()) {
            Location location = update.getMessage().getLocation();
            for (Order order : tgUser.getBasket()) {
                if (isImageFile(order.getBook().getBookUrl())) {
                    order.setLongitude(location.getLongitude().floatValue());
                    order.setLatitude(location.getLatitude().floatValue());
                    order.setOrderStatus(getOrderStatus("DELIVERED"));
                    order.setOrderDate(new Timestamp(new Date().getTime()));
                    saveOrderChangesOrAddOrderToOrderList(order);
                }
            }
        } else {
            for (Order order : tgUser.getBasket()) {
                if (isImageFile(order.getBook().getBookUrl())) {
                    order.setAddress(update.getMessage().getText());
                    order.setOrderStatus(getOrderStatus("DELIVERED"));
                    order.setOrderDate(new Timestamp(new Date().getTime()));
                    saveOrderChangesOrAddOrderToOrderList(order);
                }
            }
        }
        tgUser.setState(BotState.FINISH_ORDER);
        saveUserChanges(tgUser);
        return finishPurchases(tgUser);
    }

    public static boolean isImageFile(String url) {
        String ext = url.substring(url.lastIndexOf(".") + 1);
        return ext.equalsIgnoreCase("jpg") ||
                ext.equalsIgnoreCase("png") ||
                ext.equalsIgnoreCase("jpeg");
    }

    public static boolean isAudioFile(String url) {
        String ext = url.substring(url.lastIndexOf(".") + 1);
        return ext.equalsIgnoreCase("mp3") ||
                ext.equalsIgnoreCase("m4a") ||
                ext.equalsIgnoreCase("wav");
    }

    public static boolean isDocumentFile(String url) {
        String ext = url.substring(url.lastIndexOf(".") + 1);
        return ext.equalsIgnoreCase("pdf") ||
                ext.equalsIgnoreCase("doc") ||
                ext.equalsIgnoreCase("docx") ||
                ext.equalsIgnoreCase("ppt") ||
                ext.equalsIgnoreCase("pptx") ||
                ext.equalsIgnoreCase("htm") ||
                ext.equalsIgnoreCase("html") ||
                ext.equalsIgnoreCase("txt");
    }

    public static int hasOneAudioBookInBasket(User tgUser) {
        int count = 0;
        if (tgUser.getState().equals(BotState.FINISH_ORDER) || tgUser.getState().equals(BotState.SHARE_LOCATION)) {
            for (Order order : tgUser.getBasket()) {
                if (isAudioFile(order.getBook().getBookUrl())) {
                    count++;
                }
            }
        } else if (tgUser.getState().equals(BotState.UPLOAD)) {
            for (Order order : orderList) {
                if (isAudioFile(order.getBook().getBookUrl())) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int hasOneEBookInBasket(User tgUser) {
        int count = 0;
        if (tgUser.getState().equals(BotState.FINISH_ORDER) || tgUser.getState().equals(BotState.SHARE_LOCATION)) {
            for (Order order : tgUser.getBasket()) {
                if (isDocumentFile(order.getBook().getBookUrl())) {
                    count++;
                }
            }
        } else if (tgUser.getState().equals(BotState.UPLOAD)) {
            for (Order order : orderList) {
                if (isDocumentFile(order.getBook().getBookUrl())) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int hasOnePrintedBookInBasket(User tgUser) {
        int count = 0;
        for (Order order : tgUser.getBasket()) {
            if (isImageFile(order.getBook().getBookUrl())) {
                count++;
            }
        }
        return count;
    }
}
