package ru.nikidzawa.app.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Swagger {

    @Bean
    public OpenAPI api () {
        return new OpenAPI().servers(
                List.of(new Server().url("http://localhost:8080")))
                .info(new Info()
                        .title("Библиотека")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Никита")
                                .email("datr1932@gmail.com"))
                );
    }
}

