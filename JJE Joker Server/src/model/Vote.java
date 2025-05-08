package model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Vote implements Serializable {
    private int voteId;
    private int jokeId;
    private int userId;
    private int voteValue; // 1 for upvote, -1 for downvote
    private LocalDateTime voteDate;
}

