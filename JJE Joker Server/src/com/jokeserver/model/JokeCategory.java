package com.jokeserver.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JokeCategory implements Serializable {
    private int jokeId;
    private int categoryId;
}

