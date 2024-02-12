package com.zair.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la autenticación con un token.
 */
@Data @AllArgsConstructor
public class AuthDTO {
    String token;
}
