package user_data_change_unauthourized;
import org.apache.commons.lang3.RandomStringUtils;
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
import static org.junit.Assert.*;

public class TestLogoutUserUnauthorizedError {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isUserLoggedIn;
    private boolean isUserLoggedOut;
    private String message;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Ошибка при выходе неавторизованного пользователя из системы, и проверка тела ответа")
    @Description("Проверить, что неавторизованный пользователь не может выйти из системы без refreshToken")

    @Test
    public void shouldNotLogoutUnauthorizedUserAndCheckResponseEmptyToken(){
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

        //Логаут - выход пользователя из системы Без refreshToken
        ValidatableResponse logoutResponse = userCreate.logoutUserAndCheckResponse("");
        statusCode = logoutResponse.extract().statusCode();
        isUserLoggedOut = logoutResponse.extract().path("success");
        message = logoutResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_NOT_FOUND, statusCode);
        assertFalse(isUserLoggedOut);
        assertEquals( "User is still logged in","Token required", message);
    }
    @Test
    public void shouldNotLogoutUnauthorizedUserAndCheckResponseWrongToken(){
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

        //Логаут - выход пользователя из системы c Рандомным refreshToken
        ValidatableResponse logoutResponse = userCreate.logoutUserAndCheckResponse(RandomStringUtils.randomAlphanumeric(8));
        statusCode = logoutResponse.extract().statusCode();
        isUserLoggedOut = logoutResponse.extract().path("success");
        message = logoutResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_NOT_FOUND, statusCode);
        assertFalse(isUserLoggedOut);
        assertEquals( "Token required", message);
    }
    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }

}
