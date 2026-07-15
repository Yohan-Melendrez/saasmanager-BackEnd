package com.turing.saasmanager.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.turing.saasmanager.entity.AsignacionEmpleado;
import com.turing.saasmanager.entity.LicenciaSoftware;
import com.turing.saasmanager.entity.ProveedorNube;
import com.turing.saasmanager.repository.AsignacionEmpleadoRepository;
import com.turing.saasmanager.repository.LicenciaSoftwareRepository;
import com.turing.saasmanager.repository.ProveedorNubeRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProveedorNubeRepository proveedorRepository;
    private final LicenciaSoftwareRepository licenciaRepository;
    private final AsignacionEmpleadoRepository asignacionRepository;

    public DataInitializer(ProveedorNubeRepository proveedorRepository,
                           LicenciaSoftwareRepository licenciaRepository,
                           AsignacionEmpleadoRepository asignacionRepository) {
        this.proveedorRepository = proveedorRepository;
        this.licenciaRepository = licenciaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (proveedorRepository.count() > 0) {
            return;
        }

        ProveedorNube aws = new ProveedorNube();
        aws.setNombrePlataforma("Amazon Web Services (AWS)");
        aws.setCategoriaServicio("Infraestructura como Servicio (IaaS)");

        ProveedorNube gcp = new ProveedorNube();
        gcp.setNombrePlataforma("Google Cloud Platform");
        gcp.setCategoriaServicio("Plataforma y Colaboración (PaaS / SaaS)");

        ProveedorNube azure = new ProveedorNube();
        azure.setNombrePlataforma("Microsoft Azure");
        azure.setCategoriaServicio("Nube Híbrida y Productividad");

        ProveedorNube github = new ProveedorNube();
        github.setNombrePlataforma("GitHub Enterprise");
        github.setCategoriaServicio("Control de Versiones y CI/CD");

        proveedorRepository.saveAll(Arrays.asList(aws, gcp, azure, github));

        LicenciaSoftware licAws = new LicenciaSoftware();
        licAws.setProveedor(aws);
        licAws.setTipoPlan("AWS Enterprise Support & Compute Pack");
        licAws.setCostoMensual(new BigDecimal("2500.00"));
        licAws.setAsientosTotales(25);

        LicenciaSoftware licGcp = new LicenciaSoftware();
        licGcp.setProveedor(gcp);
        licGcp.setTipoPlan("Google Workspace Enterprise Plus");
        licGcp.setCostoMensual(new BigDecimal("1200.50"));
        licGcp.setAsientosTotales(100);

        LicenciaSoftware licAzure = new LicenciaSoftware();
        licAzure.setProveedor(azure);
        licAzure.setTipoPlan("Microsoft 365 E5 & Azure AD Premium");
        licAzure.setCostoMensual(new BigDecimal("1850.00"));
        licAzure.setAsientosTotales(80);

        LicenciaSoftware licGithub = new LicenciaSoftware();
        licGithub.setProveedor(github);
        licGithub.setTipoPlan("GitHub Enterprise Cloud + Copilot");
        licGithub.setCostoMensual(new BigDecimal("950.00"));
        licGithub.setAsientosTotales(40);

        licenciaRepository.saveAll(Arrays.asList(licAws, licGcp, licAzure, licGithub));

        AsignacionEmpleado asig1 = new AsignacionEmpleado();
        asig1.setLicencia(licAws);
        asig1.setCorreoEmpleado("carlos.martinez@empresa.com");
        asig1.setFechaAsignacion(LocalDate.of(2026, 1, 10));
        asig1.setEstatusActivo(true);

        AsignacionEmpleado asig2 = new AsignacionEmpleado();
        asig2.setLicencia(licGcp);
        asig2.setCorreoEmpleado("carlos.martinez@empresa.com");
        asig2.setFechaAsignacion(LocalDate.of(2026, 1, 10));
        asig2.setEstatusActivo(true);

        AsignacionEmpleado asig3 = new AsignacionEmpleado();
        asig3.setLicencia(licGcp);
        asig3.setCorreoEmpleado("maria.fernandez@empresa.com");
        asig3.setFechaAsignacion(LocalDate.of(2026, 2, 1));
        asig3.setEstatusActivo(true);

        AsignacionEmpleado asig4 = new AsignacionEmpleado();
        asig4.setLicencia(licGithub);
        asig4.setCorreoEmpleado("maria.fernandez@empresa.com");
        asig4.setFechaAsignacion(LocalDate.of(2026, 2, 1));
        asig4.setEstatusActivo(true);

        AsignacionEmpleado asig5 = new AsignacionEmpleado();
        asig5.setLicencia(licAzure);
        asig5.setCorreoEmpleado("roberto.gonzalez@empresa.com");
        asig5.setFechaAsignacion(LocalDate.of(2026, 3, 15));
        asig5.setEstatusActivo(true);

        AsignacionEmpleado asig6 = new AsignacionEmpleado();
        asig6.setLicencia(licGithub);
        asig6.setCorreoEmpleado("ana.lopes@empresa.com");
        asig6.setFechaAsignacion(LocalDate.of(2026, 4, 20));
        asig6.setEstatusActivo(false);

        asignacionRepository.saveAll(Arrays.asList(asig1, asig2, asig3, asig4, asig5, asig6));

        System.out.println("Datos iniciales cargados en la base de datos (saas_manager).");
    }
}
