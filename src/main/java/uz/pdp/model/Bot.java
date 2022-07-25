package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
    private String photoUrl;
    private String owner;
    private String nameUz;
    private String nameEng;
    private String nameRu;
    private String descriptionUz;
    private String descriptionEng;
    private String descriptionRu;
}
