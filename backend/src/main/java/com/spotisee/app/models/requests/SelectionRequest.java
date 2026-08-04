package com.spotisee.app.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SelectionRequest {
    @NotBlank
    private String selectionTitle;
}
