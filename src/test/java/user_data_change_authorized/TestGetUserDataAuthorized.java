package user_data_change_authorized;
import io.restassured.response.ValidatableResponse;
import org.example.api_config.UserAPIConfig;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import test_data_models.User;
import test_data_models.UserAutoGenerator;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestGetUserDataAuthorized {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private String email;
    private String name;
    private boolean isInfoReceived;
    private String emailReceived;
    private String nameReceived;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Получение информации о пользователе по его accessToken, и проверка тела ответа")
    @Description("Проверить, что система возвращает инфо о пользователе (email,name), используя accessToken")

    @Test
    public void shouldGetDataAuthorizedUserAndCheckResponse() {
        User user = UserAutoGenerator.getRandomUserData(); // Создание пользователя с логином, паролем и именем
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        email = createResponse.extract().path("user.email");
        name = createResponse.extract().path("user.name");
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        //Получаем информацию о пользователе, в метод передаем его токен и проверяем респонс
        ValidatableResponse getInfoResponse = userCreate.getUserData(accessToken);
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        emailReceived = getInfoResponse.extract().path("user.email");
        nameReceived = getInfoResponse.extract().path("user.name");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isInfoReceived);
        assertEquals(email, emailReceived);
        assertEquals(name, nameReceived);
    }
    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }
}
