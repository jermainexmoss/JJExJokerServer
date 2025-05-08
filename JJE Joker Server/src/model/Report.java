package model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Report implements Serializable {
    private int reportId;
    private int userId;
    private int jokeId;
    private String reason;
    private LocalDateTime reportDate;
}
