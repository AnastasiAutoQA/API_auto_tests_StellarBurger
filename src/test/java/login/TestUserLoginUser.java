package login;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import model.UserAutoGenerator;
import model.UserLogin;
import base.BaseTestUser;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Перед каждым тестом происходит создание пользователя с логином, паролем и именем
// и получение респонса и accessToken - BaseTestUser @Before
// После каждого теста происходит удаление пользователя- BaseTestUser @After
public class TestUserLoginUser extends BaseTestUser {
    private String refreshToken;
    private int statusCode;
    private boolean isUserLoggedIn;
    private String initialEmail;
    private String initialPassword;
    private String message;
    @DisplayName("Вход систему пользователя с имейлом и паролем, и проверка тела ответа")
    @Description("Проверить, что пользователь может залогиниться;" +
            "    - для входа в систему нужно передать все обязательные поля - email, password;" +
            "    - успешный запрос возвращает статус код 200;" +
            "    - тело ответа содержит accessToken, refreshToken и success: true")

    @Test
    public void shouldLoginUserAndCheckResponse() {
        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserLoggedIn);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);
    }

    @DisplayName("Авторизация не пройдет при неправильном Логине- имейле")
    @Description("Система должна возвращать ошибку при неправильном Логине")
    @Test
    public void shouldNotLoginIncorrectEmail(){
        initialEmail = user.getEmail(); // Получили Логин пользователя
        // Задали пользователю другой рандомный Логин
        user.setEmail(UserAutoGenerator.getRandomEmail());
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        message = loginResponse.extract().path("message");
        //Сравнили полученный код и сообщ - должна быть ошибка, тк Логин не совпадает
        assertEquals(HTTP_UNAUTHORIZED, statusCode);
        assertEquals("email or password are incorrect", message);
        // Вернули первоначальный логин
        user.setEmail(initialEmail);
        loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        accessToken = loginResponse.extract().path("accessToken");// Достаем accessToken
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals(HTTP_OK, statusCode);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);
    }

    @DisplayName("Авторизация не пройдет при неправильном Пароле")
    @Description("Система должна возвращать ошибку при неправильном Пароле")
    @Test
    public void shouldNotLoginIncorrectPassword(){
        initialPassword = user.getPassword(); // Получили Пароль пользователя
        // Задали пользователю другой рандомный Пароль
        user.setPassword(UserAutoGenerator.getRandomPassword());
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        String message = loginResponse.extract().path("message");
        //Сравнили полченный код и сообщ - должна быть ошибка, тк Пароль не совпадает
        assertEquals(HTTP_UNAUTHORIZED, statusCode);
        assertEquals("email or password are incorrect", message);
        // Вернули первоначальный логин
        user.setPassword(initialPassword);
        loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        accessToken = createResponse.extract().path("accessToken");// Достаем accessToken
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals(HTTP_OK, statusCode);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);
    }
}
