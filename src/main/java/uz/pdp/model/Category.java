package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.model.enums.Lan;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category extends AbsName {
    private Lan language;
    private List<Category> categoryList;

    public Category(String name, Lan language, List<Category> categoryList) {
        super(name);
        this.language = language;
        this.categoryList = categoryList;
    }

    public Category(String name, Lan language) {
        super(name);
        this.language = language;
    }

    public Category(String name) {
        super(name);
    }
}
