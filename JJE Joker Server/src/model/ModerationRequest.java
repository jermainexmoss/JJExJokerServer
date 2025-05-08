package model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModerationRequest implements Serializable {
    private int requestId;
    private int userId;
    private int jokeId;
    private LocalDateTime requestDate;
    private String requestStatus;
    private Integer processedBy;
    private LocalDateTime processingDate;
}

