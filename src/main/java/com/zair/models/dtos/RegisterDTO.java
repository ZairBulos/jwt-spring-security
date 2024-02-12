package com.zair.models.dtos;

import com.zair.models.enums.UserRole;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para almacenar la información necesaria para registrar un nuevo usuario.
 */
@Data
public class RegisterDTO {
    String name;
    String lastName;
    String email;
    String password;
    UserRole role;
}
