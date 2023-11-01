package user;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import model.User;
import model.UserAutoGenerator;
import base.BaseTestUser;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

// Перед каждым тестом происходит создание пользователя с логином, паролем и именем
// и получение респонса и accessToken - BaseTestUser @Before
// После каждого теста происходит удаление пользователя- BaseTestUser @After
public class TestUserCreate extends BaseTestUser {
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    @DisplayName("Создание нового пользователя с имейлом, паролем и именем, и проверка тела ответа")
    @Description("Проверить, что пользователя можно создать;" +
            "    - чтобы создать пользователя, нужно передать на эндпоинт все обязательные поля;" +
            "    - запрос возвращает правильный код ответа;" +
            "    - успешный запрос возвращает success: true.")
    @Test
    public void shouldCreateNewUserAndCheckResponse() {
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);
    }

    @DisplayName("Тело ответа содержит ошибку, если создается дубликат пользователя.")
    @Description("Проверить, что нельзя создать двух одинаковых пользователей.")
    @Test
    public void shouldNotCreateDuplicatedUser() {
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        // Создаем пользователя-дубликата с такими же данными
        ValidatableResponse createDuplicatedUserResponse = userConfig.createNewUser(user);
        statusCode = createDuplicatedUserResponse.extract().statusCode();
        String message = createDuplicatedUserResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_FORBIDDEN, statusCode);
        assertEquals("The user is created", "User already exists", message);
    }

    @DisplayName("Тело ответа содержит ошибку, если используется тот же логин")
    @Description("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    @Test
    public void shouldNotCreateUserWithExistingLogin() {
        User user1 = UserAutoGenerator.getRandomUserData(); //Создаем user-1 с email, паролем, именем
        String user1Login = user1.getEmail();
        User user2 = UserAutoGenerator.getRandomUserData(); // Создаем user-2 и присваиваем ему email от user-1
        user2.setEmail(user1Login);
        // Получаем ответ и статус код при создании user-1 - успешно 200
        ValidatableResponse createResponse = userConfig.createNewUser(user1);
        statusCode = createResponse.extract().statusCode();
        isUserCreated = createResponse.extract().path("success");
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        // Получаем ответ и статус код при создании user-2 - ошибка 403, так как одинаковый email
        ValidatableResponse createUserWithExistingLoginResponse = userConfig.createNewUser(user2);
        statusCode = createUserWithExistingLoginResponse.extract().statusCode();
        isUserCreated = createUserWithExistingLoginResponse.extract().path("success");
        String message = createUserWithExistingLoginResponse.extract().path("message");
        assertEquals(HTTP_FORBIDDEN, statusCode);
        assertFalse(isUserCreated);
        assertEquals("User already exists", message);
    }
}

