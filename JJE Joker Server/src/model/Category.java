package model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category implements Serializable {
    private int categoryId;
    private String categoryName;
}