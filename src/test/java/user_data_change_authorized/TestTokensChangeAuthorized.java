package user_data_change_authorized;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTokensChangeAuthorized {
    private UserAPIConfig userCreate;
    private String accessToken1;
    private String refreshToken1;
    private String accessToken2;
    private String refreshToken2;
    private int statusCode;
    private boolean isUserCreated;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Изменение токенов пользователя по refreshToken, и проверка тела ответа")
    @Description("Проверить, что пользователь может поменять оба токена по своему refreshToken")

    @Test
    public void shouldChangeTokensAuthorizedUserAndCheckResponse(){
        // Создание пользователя с логином, паролем и именем
        User user = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken1 = createResponse.extract().path("accessToken");
        refreshToken1 = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken1 != null);
        assertTrue(refreshToken1 != null);

        //Запрос на генерацию новых токенов по refreshToken
        ValidatableResponse newTokensResponse = userCreate.requestNewTokensAndCheckResponse(refreshToken1);
        statusCode = newTokensResponse.extract().statusCode();
        boolean responseResult = newTokensResponse.extract().path("success");
        accessToken2 = createResponse.extract().path("accessToken");
        refreshToken2 = createResponse.extract().path("refreshToken");
        assertEquals(HTTP_OK, statusCode);
        assertTrue(responseResult);
        assertTrue(accessToken2 != null);
        assertTrue(refreshToken2 != null);
        assertTrue(accessToken1 != accessToken2);
        assertTrue(refreshToken1 != refreshToken2);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken2);
    }
}
