package com.turing.saasmanager.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "asignacion_empleado")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AsignacionEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer idAsignacion;

    @NotNull(message = "La licencia es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_licencia", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LicenciaSoftware licencia;

    @NotBlank(message = "El correo del empleado es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Column(name = "correo_empleado", nullable = false, length = 150)
    private String correoEmpleado;

    @NotNull(message = "La fecha de asignación es obligatoria")
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(name = "estatus_activo")
    private Boolean estatusActivo = true;

    public AsignacionEmpleado() {
    }

    public AsignacionEmpleado(Integer idAsignacion, LicenciaSoftware licencia, String correoEmpleado,
            LocalDate fechaAsignacion, Boolean estatusActivo) {
        this.idAsignacion = idAsignacion;
        this.licencia = licencia;
        this.correoEmpleado = correoEmpleado;
        this.fechaAsignacion = fechaAsignacion;
        this.estatusActivo = estatusActivo;
    }

    public Integer getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(Integer idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public LicenciaSoftware getLicencia() {
        return licencia;
    }

    public void setLicencia(LicenciaSoftware licencia) {
        this.licencia = licencia;
    }

    public String getCorreoEmpleado() {
        return correoEmpleado;
    }

    public void setCorreoEmpleado(String correoEmpleado) {
        this.correoEmpleado = correoEmpleado;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Boolean getEstatusActivo() {
        return estatusActivo;
    }

    public void setEstatusActivo(Boolean estatusActivo) {
        this.estatusActivo = estatusActivo;
    }


}