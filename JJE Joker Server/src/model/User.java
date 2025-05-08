package model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String accountType;
    private LocalDateTime creationDate;
    private LocalDateTime lastLogin;
    private boolean isActive;
    private String preferences;
}