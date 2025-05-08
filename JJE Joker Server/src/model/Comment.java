package model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Comment implements Serializable {
    private int commentId;
    private int jokeId;
    private int userId;
    private String commentText;
    private LocalDateTime commentDate;


}
