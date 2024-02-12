package com.zair.services;

import com.zair.models.dtos.AuthDTO;
import com.zair.models.dtos.LoginDTO;
import com.zair.models.dtos.RegisterDTO;

/**
 * Interfaz que define los servicios de autenticación en el sistema.
 */
public interface AuthService {

    AuthDTO login(LoginDTO login) throws Exception;

    AuthDTO register(RegisterDTO register) throws Exception;
}
