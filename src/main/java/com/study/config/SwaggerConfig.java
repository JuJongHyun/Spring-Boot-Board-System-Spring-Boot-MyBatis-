package com.study.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("게시판 REST API")
                        .description("Spring Boot + MyBatis 기반 게시판 REST API 명세서")
                        .version("v1.0.0"))
                .servers(List.of(new Server().url("/").description("Local Server")));
    }
}
