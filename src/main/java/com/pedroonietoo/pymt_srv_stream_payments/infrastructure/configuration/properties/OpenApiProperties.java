package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.openapi")
public record OpenApiProperties(
        String title,
        String description,
        String version,
        Contact contact,
        License license,
        List<ServerItem> servers
) {
    public record Contact(String name, String email, String url) {}
    public record License(String name, String url) {}
    public record ServerItem(String url, String description) {}
}