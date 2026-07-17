package com.turing.saasmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    private String username;

    @Email(message = "El formato del correo electrónico no es válido")
    private String email;

    /**
     * Contraseña actual, requerida para confirmar cambios sensibles (username, email o password).
     * Solo obligatoria cuando se envía un nuevo password.
     */
    private String passwordActual;

    @Size(min = 6, max = 255, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String passwordNuevo;

    /**
     * Solo puede ser modificado por un ROLE_ADMIN.
     */
    private String rol;

    public UpdateUserRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNuevo() {
        return passwordNuevo;
    }

    public void setPasswordNuevo(String passwordNuevo) {
        this.passwordNuevo = passwordNuevo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
