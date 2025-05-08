package model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAction implements Serializable {
    private int actionId;
    private int userId;
    private String actionType;
    private String actionDetails;
    private LocalDateTime actionTimestamp;
    private String actionDescription;
    private LocalDateTime actionDate;
}