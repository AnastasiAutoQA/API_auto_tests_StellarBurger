package user_data_change_unauthourized;
import org.example.api_config.UserAPIConfig;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import test_data_models.User;
import test_data_models.UserAutoGenerator;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class TestTokensChangeUnauthorizedError {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Ошибка при запросе на смену токенов без авторизации по refreshToken, и проверка тела ответа")
    @Description("Проверить, что система выдает ошибку при запросе на смену токенов без refreshToken")

    @Test
    public void shouldNotChangeTokensUnauthorizedUserAndCheckResponse(){
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

        //Запрос на генерацию новых токенов Без авторизации по refreshToken
        ValidatableResponse newTokensResponse = userCreate.requestNewTokensAndCheckResponse("");
        statusCode = newTokensResponse.extract().statusCode();
        boolean responseResult = newTokensResponse.extract().path("success");
        String message = newTokensResponse.extract().path("message");
        assertEquals(HTTP_UNAUTHORIZED, statusCode);
        assertFalse(responseResult);
        assertEquals("Token is invalid", message);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }
}

