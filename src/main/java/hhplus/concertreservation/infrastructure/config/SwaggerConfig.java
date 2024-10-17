package hhplus.concertreservation.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }
    private Info apiInfo() {
        return new Info()
                .title("Concert Reservation Service API Docs")
                .description("콘서트 예약 서비스 API 명세입니다")
                .version("1.0.0");
    }
}
