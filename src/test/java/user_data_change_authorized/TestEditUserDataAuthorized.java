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
import test_data_models.UserLogin;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestEditUserDataAuthorized {
    private UserAPIConfig userCreate;
    private String accessToken;
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

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Редактирование информации о пользователе по его accessToken, и проверка тела ответа")
    @Description("Проверить, что система обновляет инфо о пользователе (email, name, password), используя accessToken")
    @Test
    public void shouldChangeEmailAndNameAuthorizedUserAndCheckResponse() {
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
        nameChanged = UserAutoGenerator.getRandomName();
        user.setEmail(emailChanged);
        user.setName(nameChanged);
        ValidatableResponse getInfoResponse = userCreate.editUser(accessToken, user);
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при редактировании пользователя
        emailReceived = getInfoResponse.extract().path("user.email");
        nameReceived = getInfoResponse.extract().path("user.name");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isInfoReceived);
        assertEquals(emailChanged, emailReceived);
        assertEquals(nameChanged, nameReceived);

        //Проверяем, что пользователь успешно логинился с новым имейлом
        ValidatableResponse loginResponse = userCreate.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        boolean isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserLoggedIn);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);
    }
    @Test
    public void shouldChangePasswordAuthorizedUserAndCheckResponse() {
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

        //Редактируем информацию о пользователе - password, в метод передаем его токен и проверяем респонс
        user.setPassword(UserAutoGenerator.getRandomPassword()); // Задали пользователю другой пароль
        ValidatableResponse getInfoResponse = userCreate.editUser(accessToken, user);
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        emailReceived = getInfoResponse.extract().path("user.email");
        nameReceived = getInfoResponse.extract().path("user.name");
        String password = getInfoResponse.extract().path("user.password");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isInfoReceived);
        assertEquals(email, emailReceived);
        assertEquals(name, nameReceived);
        assertEquals(password, null);// в теле ответа не должно быть пароля, только имейл и имя

        //Проверяем, что пользователь успешно логинился с новым паролем
        ValidatableResponse loginResponse = userCreate.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        boolean isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserLoggedIn);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }
}




