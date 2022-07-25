package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrdersFile {
    private UUID id = UUID.randomUUID();
    private File ordersFile;
    private User user;

    public UserOrdersFile(File ordersFile, User user) {
        this.ordersFile = ordersFile;
        this.user = user;
    }
}
