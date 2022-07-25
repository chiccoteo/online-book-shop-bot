package uz.pdp.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.bot.BotState;
import uz.pdp.model.Category;
import uz.pdp.model.User;
import uz.pdp.model.enums.Lan;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import static uz.pdp.db.DB.*;
import static uz.pdp.services.BotService.saveUserChanges;
import static uz.pdp.services.GenerateKeyboardMarkup.generateInlineKeyboardMarkup;
import static uz.pdp.services.OrderService.getOrderStatus;
import static uz.pdp.services.OrderService.saveOrderChangesOrAddOrderToOrderList;

public class AdminService {

    public static SendMessage adminShowMenu(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText("Menu tanlang");
        tgUser.setState(BotState.SHOW_ADMIN_MENU);
        saveUserChanges(tgUser);
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static SendMessage categoryCRUD(User tgUser) {
        return addCategory(tgUser);
    }

    public static SendMessage addCategory(User tgUser) {
//        tgUser.setState(BotState.ADD_CATEGORY);
//        saveUserChanges(tgUser);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        StringBuilder text = new StringBuilder();
        if (categoryList.isEmpty() || tgUser.getState().equals(BotState.ADD_CATEGORY)) {
            text.append("OK. Send me a category name for your bot. Please use this format:\n\n" +
                    "Name_UZ, Name_ENG, Name_RU\n");
            sendMessage.setText(text.toString());
        } else {
            for (Category category : categoryList) {
                if (category.getLanguage().equals(Lan.UZ)) {
                    text.append(category.getName()).append("\n");
                }
            }
            text.append("\nChoose command");
            sendMessage.setText(text.toString());
            sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        }
        return sendMessage;
    }

    public static EditMessageText addInnerCategory(User tgUser, Update update) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(tgUser.getChatId());
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        StringBuilder text = new StringBuilder();
        for (Category category : categoryList) {
            if (category.getLanguage().equals(Lan.UZ)) {
                text.append(category.getName()).append("\n");
            }
        }
        text.append("\nChoose category");
        editMessageText.setText(String.valueOf(text));
        editMessageText.setReplyMarkup((InlineKeyboardMarkup) generateInlineKeyboardMarkup(tgUser));
        return editMessageText;
    }

    public static SendMessage bookCRUD(User tgUser) {
        return null;
    }

    public static SendMessage payTypeCRUD(User tgUser) {
        return null;
    }

    public static SendMessage orderStatusCRUD(User tgUser) {
        return null;
    }

    public static SendMessage showOrders(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        StringBuilder text = new StringBuilder("Natijalar " + ((tgUser.getTempOrderPage() - 1) * PAGE_SIZE + 1) +
                "-" + (Math.min(PAGE_SIZE * tgUser.getTempOrderPage(), orderList.size())) + "  " + orderList.size() + " dan\n\n");

        int j = 1;
        for (int i = (tgUser.getTempOrderPage() - 1) * PAGE_SIZE;
             i < ((tgUser.getTempOrderPage() * PAGE_SIZE < orderList.size()) ?
                     PAGE_SIZE : (orderList.size()) - PAGE_SIZE * (tgUser.getTempOrderPage() - 1)); i++) {
            text.append(j).append(". ").append(orderList.get(i).getBook().getName()).append(" - ").
                    append(orderList.get(i).getUser().getUsername()).append("\n");
            j++;
        }
        sendMessage.setText(String.valueOf(text));
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static EditMessageText showNextBackOrders(User tgUser, Update update) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(tgUser.getChatId());
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        StringBuilder text = new StringBuilder(((tgUser.getTempOrderPage() - 1) * PAGE_SIZE + 1) +
                "-" + (Math.min(PAGE_SIZE * tgUser.getTempOrderPage(), orderList.size())) + "  " + orderList.size() + " dan\n\n");
        int j = 1;
        for (int i = PAGE_SIZE * (tgUser.getTempOrderPage() - 1);
             ((PAGE_SIZE * tgUser.getTempOrderPage() < orderList.size()) ?
                     i < PAGE_SIZE * tgUser.getTempOrderPage() : i < orderList.size()); i++) {
            text.append(j).append(". ").append(orderList.get(i).getBook().getName()).append(" - ").
                    append(orderList.get(i).getUser().getUsername()).append("\n");
            j++;
        }
        editMessageText.setText(String.valueOf(text));
        editMessageText.setReplyMarkup((InlineKeyboardMarkup) generateInlineKeyboardMarkup(tgUser));
        return editMessageText;
    }

    public static SendMessage changeOrderStatus(User tgUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText("Current status -> " + orderList.get(tgUser.getChosenOrderIndex()).getOrderStatus().getName() +
                "\n\nChoose next order status");
        sendMessage.setReplyMarkup(generateInlineKeyboardMarkup(tgUser));
        return sendMessage;
    }

    public static SendMessage setNewOrderStatus(User tgUser, String newOrderStatus) {
        orderList.get(tgUser.getChosenOrderIndex()).setOrderStatus(getOrderStatus(newOrderStatus));
        saveOrderChangesOrAddOrderToOrderList(orderList.get(tgUser.getChosenOrderIndex()));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tgUser.getChatId());
        sendMessage.setText("Successfully changed order status");
        return sendMessage;
    }

    @SneakyThrows
    public static SendDocument downloadOrders(User tgUser) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        XSSFWorkbook workbook = new XSSFWorkbook();
        File file = new File("src/main/resources/allOrdersForAdmin/orders.xlsx");
        FileOutputStream outputStream = new FileOutputStream(file);
        XSSFSheet sheet = workbook.createSheet("ORDERS");
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("T/r");
        row.createCell(1).setCellValue("Username");
        row.createCell(2).setCellValue("Book name");
        row.createCell(3).setCellValue("Amount");
        row.createCell(4).setCellValue("Price");
        row.createCell(5).setCellValue("Order Time");
        row.createCell(6).setCellValue("Longitude");
        row.createCell(7).setCellValue("Latitude");
        row.createCell(8).setCellValue("Address");
        row.createCell(9).setCellValue("Order status");
        for (int i = 0; i < orderList.size(); i++) {
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            sheet.autoSizeColumn(0);
            row.createCell(1).setCellValue(orderList.get(i).getUser().getUsername());
            sheet.autoSizeColumn(1);
            row.createCell(2).setCellValue(orderList.get(i).getBook().getName());
            sheet.autoSizeColumn(2);
            row.createCell(3).setCellValue(orderList.get(i).getAmount());
            row.createCell(4).setCellValue(orderList.get(i).getPayment().getPaySum());
            if (orderList.get(i).getOrderDate() == null)
                row.createCell(5).setCellValue("");
            else
                row.createCell(5).setCellValue(orderList.get(i).getOrderDate().toLocalDateTime().toLocalTime().format(formatter));
            sheet.autoSizeColumn(5);
            if (orderList.get(i).getLongitude() == null)
                row.createCell(6).setCellValue("");
            else
                row.createCell(6).setCellValue(orderList.get(i).getLongitude());
            sheet.autoSizeColumn(6);
            if (orderList.get(i).getLatitude() == null)
                row.createCell(7).setCellValue("");
            else
                row.createCell(7).setCellValue(orderList.get(i).getLatitude());
            sheet.autoSizeColumn(7);
            if (orderList.get(i).getAddress() == null)
                row.createCell(8).setCellValue("");
            else
                row.createCell(8).setCellValue(orderList.get(i).getAddress());
            sheet.autoSizeColumn(8);
            row.createCell(9).setCellValue(orderList.get(i).getOrderStatus().getName());
        }
        sheet.autoSizeColumn(9);
        workbook.write(outputStream);
        outputStream.close();
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(tgUser.getChatId());
        sendDocument.setDocument(new InputFile(file));
        sendDocument.setCaption("Orders file");
        return sendDocument;
    }


    @SneakyThrows
    public static SendDocument downloadUsers(User tgUser) {
        Document document = new Document();
        File file = new File("src/main/resources/allUsers/users.pdf");
        PdfWriter.getInstance(document, new FileOutputStream(file, true));
        document.open();
        float[] widths = {0.03f, 0.1f, 0.12f, 0.07f, 0.1f};
        PdfPTable table = new PdfPTable(widths);
        table.addCell("T/r");
        table.addCell("Name");
        table.addCell("Username");
        table.addCell("Phone number");
        table.addCell("Balance");
        for (int i = 0; i < userList.size(); i++) {
            table.addCell(String.valueOf(i + 1));
            table.addCell(userList.get(i).getName());
            table.addCell(userList.get(i).getUsername());
            table.addCell(userList.get(i).getPhoneNumber());
            table.addCell(String.valueOf(userList.get(i).getBalance()));
        }
        document.add(table);
        document.close();

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(tgUser.getChatId());
        sendDocument.setDocument(new InputFile(file));
        return sendDocument;
    }
}
