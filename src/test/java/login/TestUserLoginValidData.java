package login;
import org.example.api_config.UserAPIConfig;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import test_data_models.User;
import test_data_models.UserAutoGenerator;
import test_data_models.UserLogin;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUserLoginValidData {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isUserLoggedIn;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Вход систему пользователя с имейлом и паролем, и проверка тела ответа")
    @Description("Проверить, что пользователь может залогиниться;\n" +
            "    - для входа в систему нужно передать все обязательные поля - email, password;\n" +
            "    - успешный запрос возвращает статус код 200;\n" +
            "    - тело ответа содержит accessToken, refreshToken и success: true")

    @Test
    public void shouldLoginUserAndCheckResponse() {
        // Создание пользователя с логином, паролем и именем
        User user = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);

        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userCreate.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserLoggedIn);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

    }
    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }
}
