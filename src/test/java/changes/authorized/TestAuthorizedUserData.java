package changes.authorized;
import base.BaseTestUser;
import io.restassured.response.ValidatableResponse;
import model.UserLogin;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import model.User;
import model.UserAutoGenerator;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

// Перед каждым тестом происходит создание пользователя с логином, паролем и именем
// и получение респонса и accessToken - BaseTestUser @Before
// После каждого теста происходит удаление пользователя- BaseTestUser @After
public class TestAuthorizedUserData extends BaseTestUser {
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private String email;
    private String name;
    private String emailChanged;
    private String nameChanged;
    private boolean isInfoReceived;
    private String emailReceived;
    private String nameReceived;
    private boolean isUserLoggedIn;
    private boolean isUserLoggedOut;
    private String accessToken1;
    private String refreshToken1;
    private String accessToken2;
    private String refreshToken2;
    private String message;
    private String email1;
    private String email2;
    private String name1;
    private String name2;
    private String password;
    private boolean isUserDeleted;

    @DisplayName("Получение информации о пользователе по его accessToken, и проверка тела ответа")
    @Description("Проверить, что система возвращает инфо о пользователе (email,name), используя accessToken")
    @Test
    public void shouldGetDataAuthorizedUserAndCheckResponse() {
        email = createResponse.extract().path("user.email");
        name = createResponse.extract().path("user.name");
        //Получаем информацию о пользователе, в метод передаем его токен и проверяем респонс
        ValidatableResponse getInfoResponse = userConfig.getUserData(accessToken);
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        emailReceived = getInfoResponse.extract().path("user.email");
        nameReceived = getInfoResponse.extract().path("user.name");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isInfoReceived);
        assertEquals(email, emailReceived);
        assertEquals(name, nameReceived);
    }

    @DisplayName("Редактирование информации о пользователе по его accessToken, и проверка тела ответа")
    @Description("Проверить, что система обновляет инфо о пользователе (email, name), используя accessToken")
    @Test
    public void shouldChangeEmailAndNameAuthorizedUserAndCheckResponse() {
        email = createResponse.extract().path("user.email");
        name = createResponse.extract().path("user.name");
        //Редактируем информацию о пользователе - email, name, в метод передаем его токен и проверяем респонс
        emailChanged = (UserAutoGenerator.getRandomEmail()).toLowerCase();
        nameChanged = UserAutoGenerator.getRandomName();
        user.setEmail(emailChanged);
        user.setName(nameChanged);
        ValidatableResponse getInfoResponse = userConfig.editUser(accessToken, user);
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при редактировании пользователя
        emailReceived = getInfoResponse.extract().path("user.email");
        nameReceived = getInfoResponse.extract().path("user.name");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isInfoReceived);
        assertEquals(emailChanged, emailReceived);
        assertEquals(nameChanged, nameReceived);

        //Проверяем, что пользователь успешно логинится с новым имейлом
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserLoggedIn);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);
    }
    @DisplayName("Редактирование информации о пользователе по его accessToken, и проверка тела ответа")
    @Description("Проверить, что система обновляет Пароль пользователя (password), используя accessToken")
    @Test
    public void shouldChangePasswordAuthorizedUserAndCheckResponse() {
        email = createResponse.extract().path("user.email");
        name = createResponse.extract().path("user.name");
        //Редактируем информацию о пользователе - password, в метод передаем его токен и проверяем респонс
        user.setPassword(UserAutoGenerator.getRandomPassword()); // Задали пользователю другой пароль
        ValidatableResponse getInfoResponse = userConfig.editUser(accessToken, user);
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        emailReceived = getInfoResponse.extract().path("user.email");
        nameReceived = getInfoResponse.extract().path("user.name");
        password = getInfoResponse.extract().path("user.password");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isInfoReceived);
        assertEquals(email, emailReceived);
        assertEquals(name, nameReceived);
        assertEquals(password, null);// в теле ответа не должно быть пароля, только имейл и имя

        //Проверяем, что пользователь успешно логинится с новым паролем
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        boolean isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserLoggedIn);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);
    }
    @DisplayName("Изменение токенов пользователя по refreshToken, и проверка тела ответа")
    @Description("Проверить, что пользователь может поменять оба токена по своему refreshToken")
    @Test
    public void shouldChangeTokensAuthorizedUserAndCheckResponse(){
        accessToken1 = createResponse.extract().path("accessToken");
        refreshToken1 = createResponse.extract().path("refreshToken");
        //Запрос на генерацию новых токенов по refreshToken
        ValidatableResponse newTokensResponse = userConfig.requestNewTokensAndCheckResponse(refreshToken1);
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
    @DisplayName("Ошибка при попытке добавления существующего имейла авторизованным пользователем, и проверка тела ответа")
    @Description("Проверить, что система возвращает ошибку, если при редактировании данных о пользователе" +
            "такой email уже существует, с авторизацией по accessToken")
    @Test
    public void existingEmailErrorWhenEditAuthorizedUserAndCheckResponse() {
        // Создание user-1 с логином, паролем и именем и получение респонса
        User user1 = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse1 = userConfig.createNewUser(user1);
        statusCode = createResponse1.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse1.extract().path("success"); // Вытащили сообщение при создании пользователя
        email1 = createResponse1.extract().path("user.email");
        name1 = createResponse1.extract().path("user.name");
        accessToken1 = createResponse1.extract().path("accessToken");
        refreshToken1 = createResponse1.extract().path("refreshToken");

        // Создание user-2 с логином, паролем и именем и получение респонса
        User user2 = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse2 = userConfig.createNewUser(user2);
        statusCode = createResponse2.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse2.extract().path("success"); // Вытащили сообщение при создании пользователя
        email2 = createResponse2.extract().path("user.email");
        name2 = createResponse2.extract().path("user.name");
        accessToken2 = createResponse2.extract().path("accessToken");
        refreshToken2 = createResponse2.extract().path("refreshToken");

        //Редактируем информацию о user-2, чтобы его email2 = email1, в метод передаем токен-2 от user-2 и проверяем респонс
        user2.setEmail(email1);
        ValidatableResponse getInfoResponse = userConfig.editUser(accessToken2, user2);
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        String message = getInfoResponse.extract().path("message");
        assertEquals(HTTP_FORBIDDEN, statusCode); // Сравнили статус коды и ответ
        assertFalse(isInfoReceived);
        assertEquals("User with such email already exists", message);

        String[] accessTokenList = {accessToken1, accessToken2};
        for(int i = 0; i < accessTokenList.length; i++){
            userConfig.deleteUser(accessTokenList[i]);}
    }

    @DisplayName("Выход пользователя из системы, и проверка тела ответа")
    @Description("Проверить, что пользователь может выйти из системы по своему refreshToken")
    @Test
    public void shouldLogoutAuthorizedUserAndCheckResponse(){
        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        //Логаут - выход пользователя из системы по refreshToken
        ValidatableResponse logoutResponse = userConfig.logoutUserAndCheckResponse(refreshToken);
        statusCode = logoutResponse.extract().statusCode();
        isUserLoggedOut = logoutResponse.extract().path("success");
        message = logoutResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isUserLoggedOut);
        assertEquals( "User is still logged in","Successful logout", message);
    }
    @DisplayName("Удаление пользователя по его accessToken, и проверка тела ответа")
    @Description("Проверить, что пользователя можно удалять, авторизовавшись по accessToken")
    @Test
    public void shouldDeleteAuthorizedUser(){
        //Удаляем пользователя, в метод передаем его токен accessToken и проверяем респонс
        ValidatableResponse deleteResponse = userConfig.deleteUser(accessToken);
        statusCode = deleteResponse.extract().statusCode();
        isUserDeleted = deleteResponse.extract().path("success");
        message = deleteResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_ACCEPTED, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserDeleted);
        assertEquals("User successfully removed", message);
        // Пытаемся залогиниться- проверяем, что такого юзера не находится
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        message = loginResponse.extract().path("message");
        assertEquals(HTTP_UNAUTHORIZED, statusCode);
        assertEquals("email or password are incorrect", message);
    }
}
