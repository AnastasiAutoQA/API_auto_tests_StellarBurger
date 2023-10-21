package login;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.api_config.UserAPIConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test_data_models.User;
import test_data_models.UserAutoGenerator;
import test_data_models.UserLogin;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestWrongLogin {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Авторизация не пройдет при неправильном Логине- имейле")
    @Description("Система должна возвращать ошибку при неправильном Логине")
    @Test
    public void shouldNotLoginIncorrectEmail(){
        User user = UserAutoGenerator.getRandomUserData(); // Создание пользователя с логином, паролем и именем
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        String initialEmail = user.getEmail(); // Получили Логин пользователя
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        // Задали пользователю другой рандомный Логин
        user.setEmail(UserAutoGenerator.getRandomEmail());
        ValidatableResponse loginResponse = userCreate.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        String message = loginResponse.extract().path("message");

        //Сравнили полученный код и сообщ - должна быть ошибка, тк Логин не совпадает
        assertEquals(HTTP_UNAUTHORIZED, statusCode);
        assertEquals("email or password are incorrect", message);

        // Вернули первоначальный логин
        user.setEmail(initialEmail);
        loginResponse = userCreate.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        accessToken = loginResponse.extract().path("accessToken");// Достаем accessToken
        assertEquals(HTTP_OK, statusCode);
        assertTrue(accessToken != null);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }
}