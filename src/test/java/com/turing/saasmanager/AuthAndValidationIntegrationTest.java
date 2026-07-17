package com.turing.saasmanager;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthAndValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String tokenAdmin;

    @BeforeEach
    void setUp() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        tokenAdmin = responseBody.split("\"token\":\"")[1].split("\"")[0];
    }

    @Test
    void testLoginSuccess() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.rol", is("ROLE_ADMIN")));
    }

    @Test
    void testLoginFailure() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"passwordIncorrecta\"}";
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterUserAndDuplicateError() throws Exception {
        String registerJson = "{\"username\":\"nuevousuario\",\"password\":\"pass123\",\"rol\":\"ROLE_USER\"}";
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("nuevousuario")));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.mensaje", containsString("ya existe")));
    }

    @Test
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.mensaje", containsString("token JWT válido")));
    }

    @Test
    void testProtectedEndpointWithToken() throws Exception {
        mockMvc.perform(get("/api/v1/proveedores")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateProveedorDuplicate() throws Exception {
        String proveedorJson = "{\"nombrePlataforma\":\"Amazon Web Services (AWS)\",\"categoriaServicio\":\"IaaS\"}";

        mockMvc.perform(post("/api/v1/proveedores")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(proveedorJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje", containsString("ya existe")));
    }

    @Test
    void testCreateProveedorValidationError() throws Exception {
        String proveedorInvalidoJson = "{\"nombrePlataforma\":\"\",\"categoriaServicio\":\"\"}";

        mockMvc.perform(post("/api/v1/proveedores")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(proveedorInvalidoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.detalles", hasKey("nombrePlataforma")))
                .andExpect(jsonPath("$.detalles", hasKey("categoriaServicio")));
    }
}
