package client;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

// Доки https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf
// Создание настроек HTTP-запросов для тестирования с использованием методов и классов библиотеки REST Assured
public class MainUrlConfig {
    protected static final String URI = "https://stellarburgers.nomoreparties.site/";
    protected static final String BASE_PATH = "/api";

  /*  protected static RequestSpecification getHeader() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(URI)
                .build();
    }
*/
    protected static RequestSpecification getHeader() {
        return given()
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter())
                .contentType(ContentType.JSON)
                .baseUri(URI)
                .basePath(BASE_PATH)
                ;
    }

}
