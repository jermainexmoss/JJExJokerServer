package model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Joke implements Serializable {
    private int jokeId;
    private int userId;
    private String jokeText;
    private LocalDateTime submissionDate;
    private String approvalStatus; // pending, approved, rejected
    private Integer approvedBy; // Nullable
    private LocalDateTime approvalDate; // Nullable
    private int rating; // Calculated, not stored
}