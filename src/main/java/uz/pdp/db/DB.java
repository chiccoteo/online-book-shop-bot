package uz.pdp.db;

import uz.pdp.bot.BotState;
import uz.pdp.model.*;
import uz.pdp.model.enums.Lan;
import uz.pdp.model.enums.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DB {

    public static List<User> userList = new ArrayList<>(
            Arrays.asList(
                    new User("Ramziddin", "chicco_teo", "894511884", Role.ADMIN, Lan.UZ, BotState.START)
            )
    );

    public static List<Category> categoryList = new ArrayList<>(
            Arrays.asList(
                    new Category("Jahon adabiyoti",
                            Lan.UZ,
                            Arrays.asList(
                                    new Category("Audio kitob"),
                                    new Category("Elektron kitob"),
                                    new Category("Bosma kitob"))),
                    new Category("O'zbek adabiyoti",
                            Lan.UZ,
                            Arrays.asList(
                                    new Category("Audio kitob"),
                                    new Category("Elektron kitob"),
                                    new Category("Bosma kitob"))),
                    new Category("World Literature",
                            Lan.ENG,
                            Arrays.asList(
                                    new Category("Audio book"),
                                    new Category("E-Book"),
                                    new Category("Printed book"))),
                    new Category("Uzbek Literature",
                            Lan.ENG,
                            Arrays.asList(
                                    new Category("Audio book"),
                                    new Category("E-Book"),
                                    new Category("Printed book"))),
                    new Category("Мировая литература",
                            Lan.RU,
                            Arrays.asList(
                                    new Category("Аудио книга"),
                                    new Category("Электронная книга"),
                                    new Category("Печатная книга"))),
                    new Category("Узбекская литература",
                            Lan.RU,
                            Arrays.asList(
                                    new Category("Аудио книга"),
                                    new Category("Электронная книга"),
                                    new Category("Печатная книга")))
            )
    );

    public static List<Book> bookList = new ArrayList<>(
            Arrays.asList(
                    new Book("Novvoy qiz",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(0).getId(),
                                    categoryList.get(3).getCategoryList().get(0).getId(),
                                    categoryList.get(5).getCategoryList().get(0).getId()
                            ),
                            "src/main/resources/Abdulhamid Cho lpon - Novvoy qiz (hikoya).mp3",
                            "Abdulhamid Cho'lpon",
                            5000.0),
                    new Book("O'g'ri",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(0).getId(),
                                    categoryList.get(3).getCategoryList().get(0).getId(),
                                    categoryList.get(5).getCategoryList().get(0).getId()
                            ),
                            "src/main/resources/Abdulla Qahhor - O g ri (hikoya).mp3",
                            "Abdulla Qahhor",
                            5000.0),
                    new Book("Jinlar bazmi",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(0).getId(),
                                    categoryList.get(3).getCategoryList().get(0).getId(),
                                    categoryList.get(5).getCategoryList().get(0).getId()
                            ),
                            "src/main/resources/Abdulla Qodiriy - Jinlar bazmi (hikoya).mp3",
                            "Abdulla Qodiriy",
                            5000.0),
                    new Book("Buqalamun",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(0).getId(),
                                    categoryList.get(2).getCategoryList().get(0).getId(),
                                    categoryList.get(4).getCategoryList().get(0).getId()
                            ),
                            "src/main/resources/Anton Chexov - Buqalamun.mp3",
                            "Anton Chexov",
                            5000.0),
                    new Book("Chaqaloq",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(0).getId(),
                                    categoryList.get(2).getCategoryList().get(0).getId(),
                                    categoryList.get(4).getCategoryList().get(0).getId()
                            ),
                            "src/main/resources/Chaqaloq -Alberto Moraveia.mp3",
                            "Alberto Moravia",
                            5000.0),
                    new Book("Qopqon",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(0).getId(),
                                    categoryList.get(2).getCategoryList().get(0).getId(),
                                    categoryList.get(4).getCategoryList().get(0).getId()
                            ),
                            "src/main/resources/qopqon-j.london asari.mp3",
                            "Jek London",
                            5000.0),
                    new Book("Kuyov",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(1).getId(),
                                    categoryList.get(3).getCategoryList().get(1).getId(),
                                    categoryList.get(5).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Said Ahmad. Kuyov (komediya).pdf",
                            "Said Ahmad",
                            5000.0),
                    new Book("Mehrobdan chayon",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(1).getId(),
                                    categoryList.get(3).getCategoryList().get(1).getId(),
                                    categoryList.get(5).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Abdulla Qodiriy. Mehrobdan chayon (roman).pdf",
                            "Abdulla Qodiriy",
                            5000.0),
                    new Book("Bahor qaytmaydi",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(1).getId(),
                                    categoryList.get(3).getCategoryList().get(1).getId(),
                                    categoryList.get(5).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/O TKIR HOSHIMOV. Bahor qaytmaydi.pdf",
                            "O'tkir Hoshimov",
                            5000.0),
                    new Book("Dunyoning ishlari",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(1).getId(),
                                    categoryList.get(3).getCategoryList().get(1).getId(),
                                    categoryList.get(5).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/O tkir Hoshimov. Dunyoning ishlari.pdf",
                            "O'tkir Hoshimov",
                            5000.0),
                    new Book("Odamiylik mulki",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(1).getId(),
                                    categoryList.get(3).getCategoryList().get(1).getId(),
                                    categoryList.get(5).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Tohir Malik. Odamiylik mulki (axloq kitobi).pdf",
                            "Tohir Malik",
                            5000.0),
                    new Book("Nomus va Qasos",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(1).getId(),
                                    categoryList.get(3).getCategoryList().get(1).getId(),
                                    categoryList.get(5).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Tohir Malik Nomus va Qasos.pdf",
                            "Tohir Malik",
                            5000.0),
                    new Book("Otello",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Vilyam Shekspir. Otello (tragediya).pdf",
                            "William Shekspir",
                            5000.0),
                    new Book("Romeo va Julieta",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Vilyam Shekspir. Romeo va Julietta (tragediya).pdf",
                            "William Shekspir",
                            5000.0),
                    new Book("Hayotga muhabbat",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Hayotga muhabbat (Jek London).pdf",
                            "Jek London",
                            5000.0),
                    new Book("Garov",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/chexov. garov.pdf",
                            "Anton Chexov",
                            5000.0),
                    new Book("Kichkina shaxzoda",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Antuan de Sent-Ekzyuperi. Kichkina shaxzoda.pdf",
                            "Antuan de Sent-Ekzyuperi",
                            5000.0),
                    new Book("Yevgeniy Onegin",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Aleksandr_Pushkin_Yevgeniy_Onegin.pdf",
                            "Aleksandr Pushkin",
                            5000.0),
                    new Book("Suv parisi",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Aleksandr Pushkin. Suv parisi.pdf",
                            "Aleksandr Pushkin",
                            5000.0),
                    new Book("Uch mushetyor",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/Aleksandr Dyuma-Uch mushketyor(Kutubxona).pdf",
                            "Aleksandr Dyuma",
                            5000.0),
                    new Book("Sherlock Xolms",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/a.konan_doyl_sherlok_xolms_haqida_hikoyalar.pdf",
                            "Artur Konan Doyl",
                            5000.0),
                    new Book("Anna Karenina",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(1).getId(),
                                    categoryList.get(2).getCategoryList().get(1).getId(),
                                    categoryList.get(4).getCategoryList().get(1).getId()
                            ),
                            "src/main/resources/049-Anna Karenina - Leo Tolstoy.pdf",
                            "Lew Tolstoy",
                            5000.0),

                    new Book("Brown Baby",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(2).getId(),
                                    categoryList.get(2).getCategoryList().get(2).getId(),
                                    categoryList.get(4).getCategoryList().get(2).getId()
                            ),
                            "src/main/resources/1.jpg",
                            "Nikesh Shukla",
                            30000.0),
                    new Book("Mountains Sing",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(2).getId(),
                                    categoryList.get(2).getCategoryList().get(2).getId(),
                                    categoryList.get(4).getCategoryList().get(2).getId()
                            ),
                            "src/main/resources/2.jpg",
                            "Nguen fan Que Mai",
                            20000.0),
                    new Book("Band of sisters",
                            Arrays.asList(
                                    categoryList.get(0).getCategoryList().get(2).getId(),
                                    categoryList.get(2).getCategoryList().get(2).getId(),
                                    categoryList.get(4).getCategoryList().get(2).getId()
                            ),
                            "src/main/resources/3.jpg",
                            "Lauren Willig",
                            50000.0),
                    new Book("Bahor qaytmaydi",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(2).getId(),
                                    categoryList.get(3).getCategoryList().get(2).getId(),
                                    categoryList.get(5).getCategoryList().get(2).getId()
                            ),
                            "src/main/resources/Bahor Qaytmaydi.jpg",
                            "O'tkir Hoshimiov",
                            25000.0),
                    new Book("Dunyoning ishlari",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(2).getId(),
                                    categoryList.get(3).getCategoryList().get(2).getId(),
                                    categoryList.get(5).getCategoryList().get(2).getId()
                            ),
                            "src/main/resources/Dunyoning ishlari.jpg",
                            "O'tkir Hoshimov",
                            70000.0),
                    new Book("O'tgan kunlar",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(2).getId(),
                                    categoryList.get(3).getCategoryList().get(2).getId(),
                                    categoryList.get(5).getCategoryList().get(2).getId()
                            ),
                            "src/main/resources/O'tgan kunlar.jpg",
                            "O'tkir Hoshimov",
                            45000.0),
                    new Book("Sariq devni minib",
                            Arrays.asList(
                                    categoryList.get(1).getCategoryList().get(2).getId(),
                                    categoryList.get(3).getCategoryList().get(2).getId(),
                                    categoryList.get(5).getCategoryList().get(2).getId()
                            ),
                            "src/main/resources/Sariq devni minib.jpg",
                            "Xudoyberdi To'xtaboyev",
                            15000.0)
            )
    );

    public static List<OrderStatus> orderStatusList = new ArrayList<>(
            Arrays.asList(
                    new OrderStatus("BASKET"),
                    new OrderStatus("DELIVERED"),
                    new OrderStatus("COMPLETED")
            )
    );

    public static List<Order> orderList = new ArrayList<>();

    public static List<PayType> payTypeList = new ArrayList<>(
            Arrays.asList(
                    new PayType("Naqd", 0.0),
                    new PayType("Click", 1.0),
                    new PayType("Payme", 2.0)
            )
    );

    public static Integer PAGE_SIZE = 4;

    public static List<String> numbersEmojis = new ArrayList<>(
            Arrays.asList(
                    "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "\uD83D\uDD1F"
            )
    );

    public static List<String> bookTypeEmojis = new ArrayList<>(
            Arrays.asList(
                    "\uD83C\uDFA7", "\uD83D\uDCBB", "\uD83D\uDCDA"
            )
    );

    public static List<UserOrdersFile> ordersFileList=new ArrayList<>();
}
