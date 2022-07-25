package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book extends AbsName {
    private List<UUID> categoryUUIDList;
    private String bookUrl;
    private String author;
    private Double price;

    public Book(String name, List<UUID> categoryUUIDList, String bookUrl, String author, Double price) {
        super(name);
        this.categoryUUIDList = categoryUUIDList;
        this.bookUrl = bookUrl;
        this.author = author;
        this.price = price;
    }

}
