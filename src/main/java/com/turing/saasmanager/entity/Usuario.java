package com.turing.saasmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Column(name = "rol", nullable = false, length = 50)
    private String rol; // Ejemplo: ROLE_ADMIN, ROLE_USER

    public Usuario() {
    }

    public Usuario(String username, String email, String password, String rol) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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
