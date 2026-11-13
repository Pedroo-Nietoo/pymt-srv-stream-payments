package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.configuration;

import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.configuration.properties.OpenApiProperties;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenApiConfigTest {

    @Test
    void shouldCreateOpenApiWithNullServers() {
        OpenApiProperties properties = mock(OpenApiProperties.class, Mockito.RETURNS_DEEP_STUBS);

        when(properties.servers()).thenReturn(null);

        OpenApiConfig config = new OpenApiConfig(properties);
        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI);
        assertTrue(openAPI.getServers().isEmpty());
    }

    @Test
    void shouldCreateOpenApiWithProvidedServers() {
        OpenApiProperties properties = mock(OpenApiProperties.class, Mockito.RETURNS_DEEP_STUBS);

        OpenApiProperties.ServerItem mockServerItem = mock(OpenApiProperties.ServerItem.class);
        when(mockServerItem.url()).thenReturn("http://localhost:8080");
        when(mockServerItem.description()).thenReturn("Local Environment");

        when(properties.servers()).thenReturn(List.of(mockServerItem));

        OpenApiConfig config = new OpenApiConfig(properties);
        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI);
        assertEquals(1, openAPI.getServers().size());
        assertEquals("http://localhost:8080", openAPI.getServers().get(0).getUrl());
        assertEquals("Local Environment", openAPI.getServers().get(0).getDescription());
    }
}