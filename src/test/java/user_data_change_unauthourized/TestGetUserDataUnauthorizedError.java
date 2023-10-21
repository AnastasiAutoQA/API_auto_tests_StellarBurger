package user_data_change_unauthourized;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api_config.UserAPIConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test_data_models.User;
import test_data_models.UserAutoGenerator;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class TestGetUserDataUnauthorizedError {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isInfoReceived;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Ошибка при получении инфо о пользователе без авторизации, без accessToken, и проверка тела ответа")
    @Description("Проверить, что получение инфо о пользователе невозможно без авторизации")

    @Test
    public void shouldNotGetDataUnauthorizedUserAndCheckResponseEmptyToken() {
        User user = UserAutoGenerator.getRandomUserData(); // Создание пользователя с логином, паролем и именем
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        //Получаем информацию о пользователе, в метод НЕ передаем его токен и проверяем респонс
        ValidatableResponse getResponse = userCreate.getUserData("");
        statusCode = getResponse.extract().statusCode();
        isInfoReceived = getResponse.extract().path("success"); //должно быть false
        String message = getResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_UNAUTHORIZED, statusCode); // Сравнили статус коды и ответ
        assertFalse(isInfoReceived);
        assertEquals("You should be authorised", message);
    }

    @Test
    public void shouldNotGetDataUnauthorizedUserAndCheckResponseWrongToken() {
        // Создание пользователя с логином, паролем и именем
        User user = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        //Получаем информацию о пользователе, в метод передаем Рандомный токен и проверяем респонс
        ValidatableResponse getResponse = userCreate.getUserData(RandomStringUtils.randomAlphanumeric(9));
        statusCode = getResponse.extract().statusCode();
        isInfoReceived = getResponse.extract().path("success"); //должно быть false
        String message = getResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_FORBIDDEN, statusCode); // Сравнили статус коды и ответ
        assertFalse(isInfoReceived);
        assertEquals("jwt malformed", message);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }

}
