package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.model.enums.Lan;
import uz.pdp.model.enums.Role;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbsName {
    private String username;
    private String phoneNumber;
    private Role role;
    private String chatId;
    private Lan language;
    private Double balance;
    private String state;
    private Category tempCategory;
    private Category adminChosenCategory;
    private Integer tempBookPage;
    private Integer tempOrderPage;
    private Integer chosenBookIndex;
    private Integer chosenOrderIndex;
    private PayType payType;
    private List<Order> basket = new ArrayList<>();

    public User(String name, String username, String chatId, Double balance, Role role, String state) {
        super(name);
        this.username = username;
        this.chatId = chatId;
        this.balance = balance;
        this.role = role;
        this.state = state;
    }

    public User(String name, String username, String chatId, Role role, Lan language, String state) {
        super(name);
        this.username = username;
        this.chatId = chatId;
        this.role = role;
        this.language = language;
        this.state = state;
    }
}
