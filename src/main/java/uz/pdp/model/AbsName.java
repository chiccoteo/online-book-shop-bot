package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsName {
    private UUID id = UUID.randomUUID();
    private String name;

    public AbsName(String name) {
        this.name = name;
    }
}
