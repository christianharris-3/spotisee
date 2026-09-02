package com.spotisee.app.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TokenLifelineCheck {
    @NotNull
    private String token;
}
