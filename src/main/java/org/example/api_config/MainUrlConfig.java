package org.example.api_config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

// Доки https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf
// Создание настроек HTTP-запросов для тестирования с использованием методов и классов библиотеки REST Assured
public class MainUrlConfig {
    protected static final String URI = "https://stellarburgers.nomoreparties.site/";
    protected static RequestSpecification getHeader() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(URI)
                .build();
    }
}
