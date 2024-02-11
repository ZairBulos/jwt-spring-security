package com.zair.models.dtos;

import com.zair.models.enums.UserRole;
import lombok.Data;

@Data
public class RegisterDTO {
    String name;
    String lastName;
    String email;
    String password;
    UserRole role;
}
