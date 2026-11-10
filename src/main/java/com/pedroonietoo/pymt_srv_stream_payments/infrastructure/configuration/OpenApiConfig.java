package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.configuration;

import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.configuration.properties.OpenApiProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    private final OpenApiProperties properties;

    public OpenApiConfig(OpenApiProperties properties) {
        this.properties = properties;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        List<Server> servers = properties.servers() == null
                ? Collections.emptyList()
                : properties.servers().stream()
                .map(s -> new Server().url(s.url()).description(s.description()))
                .toList();

        return new OpenAPI()
                .info(new Info()
                        .title(properties.title())
                        .version(properties.version())
                        .description(properties.description())
                        .contact(new Contact()
                                .name(properties.contact().name())
                                .email(properties.contact().email())
                                .url(properties.contact().url()))
                        .license(new License()
                                .name(properties.license().name())
                                .url(properties.license().url())))
                .servers(servers);
    }
}