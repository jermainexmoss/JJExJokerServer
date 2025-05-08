package model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JokeOfTheDay implements Serializable {
    private int jotdId;
    private int jokeId;
    private LocalDate date;
}