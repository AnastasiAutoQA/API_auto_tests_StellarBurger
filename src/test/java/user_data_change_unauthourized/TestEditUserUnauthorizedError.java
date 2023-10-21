package user_data_change_unauthourized;
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
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.*;

public class TestEditUserUnauthorizedError {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private String email;
    private String name;
    private String emailChanged;
    private String nameChanged;
    private String passwordChanged;
    private boolean isInfoReceived;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Редактирование информации о пользователе невозможно без авторизации, без accessToken, и проверка тела ответа")
    @Description("Проверить, что система возвращает ошибку, если обновлять пользователя без авторизации- без accessToken")

    @Test
    public void shouldNotChangeUnauthorizedUserInfoAndCheckResponse() {
        // Создание пользователя с логином, паролем и именем
        User user = UserAutoGenerator.getRandomUserData();
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

        //Редактируем информацию о пользователе - email, name, в метод передаем его токен и проверяем респонс
        emailChanged = (UserAutoGenerator.getRandomEmail()).toLowerCase();
        passwordChanged = (UserAutoGenerator.getRandomPassword());
        nameChanged = UserAutoGenerator.getRandomName();
        user.setEmail(emailChanged);
        user.setPassword(passwordChanged);
        user.setName(nameChanged);
        ValidatableResponse getInfoResponse = userCreate.editUser("", user); //Не передаем токен
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        String message = getInfoResponse.extract().path("message");
        assertEquals(HTTP_UNAUTHORIZED, statusCode); // Сравнили статус коды и ответ
        assertFalse(isInfoReceived);
        assertEquals("You should be authorised", message);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }
}


