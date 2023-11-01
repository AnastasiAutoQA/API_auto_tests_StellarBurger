package changes.unauthorized;
import base.BaseTestUser;
import io.restassured.response.ValidatableResponse;
import model.UserLogin;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import model.UserAutoGenerator;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

// Перед каждым тестом происходит создание пользователя с логином, паролем и именем
// и получение респонса и accessToken - BaseTestUser @Before
// После каждого теста происходит удаление пользователя- BaseTestUser @After

public class TestUnauthorizedUserData extends BaseTestUser {
    private int statusCode;
    private boolean isUserCreated;
    private boolean isUserDeleted;
    private String email;
    private String name;
    private String emailChanged;
    private String nameChanged;
    private String passwordChanged;
    private boolean isInfoReceived;
    private boolean isUserLoggedOut;
    private String message;
    @DisplayName("Редактирование информации о пользователе невозможно без авторизации, без accessToken, и проверка тела ответа")
    @Description("Проверить, что система возвращает ошибку, если обновлять пользователя без авторизации- без accessToken")
    @Test
    public void shouldNotChangeUnauthorizedUserInfoAndCheckResponse() {
        email = createResponse.extract().path("user.email");
        name = createResponse.extract().path("user.name");
        //Редактируем информацию о пользователе - email, name, в метод передаем его токен и проверяем респонс
        emailChanged = (UserAutoGenerator.getRandomEmail()).toLowerCase();
        passwordChanged = (UserAutoGenerator.getRandomPassword());
        nameChanged = UserAutoGenerator.getRandomName();
        user.setEmail(emailChanged);
        user.setPassword(passwordChanged);
        user.setName(nameChanged);
        ValidatableResponse getInfoResponse = userConfig.editUser("", user); //Не передаем токен
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        message = getInfoResponse.extract().path("message");
        assertEquals(HTTP_UNAUTHORIZED, statusCode); // Сравнили статус коды и ответ
        assertFalse(isInfoReceived);
        assertEquals("You should be authorised", message);
        // Проверяем, что у пользователя ничего не поменялось
        ValidatableResponse getUserDataResponse = userConfig.getUserData(accessToken);
        assertEquals(name, getUserDataResponse.extract().path("user.name"));
        assertEquals(email, getUserDataResponse.extract().path("user.email"));
    }
    @DisplayName("Ошибка при получении инфо о пользователе без авторизации, без accessToken, и проверка тела ответа")
    @Description("Проверить, что получение инфо о пользователе невозможно без авторизации - пустой токен")
    @Test
    public void shouldNotGetDataUnauthorizedUserAndCheckResponseEmptyToken() {
        //Получаем информацию о пользователе, в метод НЕ передаем его токен и проверяем респонс
        ValidatableResponse getResponse = userConfig.getUserData("");
        statusCode = getResponse.extract().statusCode();
        isInfoReceived = getResponse.extract().path("success"); //должно быть false
        message = getResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_UNAUTHORIZED, statusCode); // Сравнили статус коды и ответ
        assertFalse(isInfoReceived);
        assertEquals("You should be authorised", message);
    }
    @DisplayName("Ошибка при получении инфо о пользователе без авторизации, без accessToken, и проверка тела ответа")
    @Description("Проверить, что система выдает ошибку при попытке получения инфо о пользователе с неверным токеном")
    @Test
    public void shouldNotGetDataUnauthorizedUserAndCheckResponseWrongToken() {
        //Получаем информацию о пользователе, в метод передаем Рандомный токен и проверяем респонс
        ValidatableResponse getResponse = userConfig.getUserData(RandomStringUtils.randomAlphanumeric(9));
        statusCode = getResponse.extract().statusCode();
        isInfoReceived = getResponse.extract().path("success"); //должно быть false
        message = getResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_FORBIDDEN, statusCode); // Сравнили статус коды и ответ
        assertFalse(isInfoReceived);
        assertEquals("jwt malformed", message);
    }
    @DisplayName("Ошибка при выходе неавторизованного пользователя из системы, и проверка тела ответа")
    @Description("Проверить, что неавторизованный пользователь не может выйти из системы без refreshToken - пустой токен")
    @Test
    public void shouldNotLogoutUnauthorizedUserAndCheckResponseEmptyToken(){
        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        accessToken = loginResponse.extract().path("accessToken");

        //Логаут - выход пользователя из системы Без refreshToken
        ValidatableResponse logoutResponse = userConfig.logoutUserAndCheckResponse("");
        statusCode = logoutResponse.extract().statusCode();
        isUserLoggedOut = logoutResponse.extract().path("success");
        message = logoutResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_NOT_FOUND, statusCode);
        assertFalse(isUserLoggedOut);
        assertEquals( "User is still logged in","Token required", message);
    }
    @DisplayName("Ошибка при выходе неавторизованного пользователя из системы, и проверка тела ответа")
    @Description("Проверить, что неавторизованный пользователь не может выйти из системы по неверному refreshToken")
    @Test
    public void shouldNotLogoutUnauthorizedUserAndCheckResponseWrongToken(){
        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        accessToken = loginResponse.extract().path("accessToken");

        //Логаут - выход пользователя из системы c Рандомным refreshToken
        ValidatableResponse logoutResponse = userConfig.logoutUserAndCheckResponse(RandomStringUtils.randomAlphanumeric(8));
        statusCode = logoutResponse.extract().statusCode();
        isUserLoggedOut = logoutResponse.extract().path("success");
        message = logoutResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_NOT_FOUND, statusCode);
        assertFalse(isUserLoggedOut);
        assertEquals( "Token required", message);
    }
    @DisplayName("Ошибка при запросе на смену токенов без авторизации по refreshToken, и проверка тела ответа")
    @Description("Проверить, что система выдает ошибку при запросе на смену токенов без refreshToken")
    @Test
    public void shouldNotChangeTokensUnauthorizedUserAndCheckResponse(){
        //Запрос на генерацию новых токенов Без авторизации по refreshToken
        ValidatableResponse newTokensResponse = userConfig.requestNewTokensAndCheckResponse("");
        statusCode = newTokensResponse.extract().statusCode();
        boolean responseResult = newTokensResponse.extract().path("success");
        message = newTokensResponse.extract().path("message");
        assertEquals(HTTP_UNAUTHORIZED, statusCode);
        assertFalse(responseResult);
        assertEquals("Token is invalid", message);
    }

    @DisplayName("Удаление пользователя невозможно без accessToken, и проверка тела ответа.")
    @Description("Проверить, что нельзя удалять пользователя без авторизации по accessToken - пустой токен.")
    @Test
    public void shouldNotDeleteUnauthorizedUserEmptyToken(){
        //Удаляем пользователя, в метод Не передаем его токен и проверяем респонс
        ValidatableResponse deleteResponse = userConfig.deleteUser("");
        statusCode = deleteResponse.extract().statusCode();
        isUserDeleted = deleteResponse.extract().path("success"); //должно быть false
        message = deleteResponse.extract().path("message");
        assertEquals(HTTP_UNAUTHORIZED, statusCode); // Сравнили статус коды и ответ
        assertFalse(isUserDeleted);
        assertEquals("You should be authorised", message);
    }
    @DisplayName("Удаление пользователя невозможно с неверным значением accessToken, и проверка тела ответа.")
    @Description("Проверить, что система выдает ошибку при попытке удалить пользователя с неверным accessToken.")
    @Test
    public void shouldNotDeleteUnauthorizedUserWrongToken(){
        //Удаляем пользователя, в метод передаем Рандомный токен и проверяем респонс
        ValidatableResponse deleteResponse = userConfig.deleteUser(RandomStringUtils.randomAlphanumeric(8));
        statusCode = deleteResponse.extract().statusCode();
        isUserDeleted = deleteResponse.extract().path("success"); //должно быть false
        message = deleteResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_FORBIDDEN, statusCode); // Сравнили статус коды и ответ
        assertFalse(isUserDeleted);
        assertEquals("jwt malformed", message);
    }



}
