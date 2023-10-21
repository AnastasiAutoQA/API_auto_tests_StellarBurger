package user_data_change_unauthourized;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api_config.UserAPIConfig;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import test_data_models.User;
import test_data_models.UserAutoGenerator;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class TestDeleteUserUnauthorizedError {

    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isUserDeleted;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Удаление пользователя невозможно без accessToken, и проверка тела ответа.")
    @Description("Проверить, что нельзя удалять пользователя без авторизации по accessToken.")

    @Test
    public void shouldNotDeleteUnauthorizedUserEmptyToken(){
        // Создание пользователя с логином, паролем и именем
        User user = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        //Удаляем пользователя, в метод Не передаем его токен и проверяем респонс
        ValidatableResponse deleteResponse = userCreate.deleteUser("");
        statusCode = deleteResponse.extract().statusCode();
        isUserDeleted = deleteResponse.extract().path("success"); //должно быть false
        String message = deleteResponse.extract().path("message");
        assertEquals(HTTP_UNAUTHORIZED, statusCode); // Сравнили статус коды и ответ
        assertFalse(isUserDeleted);
        assertEquals("You should be authorised", message);
    }

    @Test
    public void shouldNotDeleteUnauthorizedUserWrongToken(){
        // Создание пользователя с логином, паролем и именем
        User user = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        //Удаляем пользователя, в метод передаем Рандомный токен и проверяем респонс
        ValidatableResponse deleteResponse = userCreate.deleteUser(RandomStringUtils.randomAlphanumeric(8));
        statusCode = deleteResponse.extract().statusCode();
        isUserDeleted = deleteResponse.extract().path("success"); //должно быть false
        String message = deleteResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_FORBIDDEN, statusCode); // Сравнили статус коды и ответ
        assertFalse(isUserDeleted);
        assertEquals("jwt malformed", message);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }
}
