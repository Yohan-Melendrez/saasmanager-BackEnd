package com.turing.saasmanager.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private String rol = "ROLE_USER";

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        if (rol != null && !rol.isBlank()) {
            this.rol = rol;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
