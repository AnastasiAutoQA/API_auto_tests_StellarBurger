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
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.*;

public class TestEditWithDuplicatedEmailAuthorized {
    private UserAPIConfig userCreate;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isInfoReceived;
    private String accessToken1;
    private String refreshToken1;
    private String accessToken2;
    private String refreshToken2;
    private String email1;
    private String name1;
    private String email2;
    private String name2;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Ошибка при попытке добавления существующего имейла авторизованным пользователем, и проверка тела ответа")
    @Description("Проверить, что система возвращает ошибку, если при редактировании данных о пользователе\n" +
            "такой email уже существует, с авторизацией по accessToken")

    @Test
    public void existingEmailErrorWhenEditAuthorizedUserAndCheckResponse() {
        // Создание user-1 с логином, паролем и именем и получение респонса
        User user1 = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse1 = userCreate.createNewUser(user1);
        statusCode = createResponse1.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse1.extract().path("success"); // Вытащили сообщение при создании пользователя
        email1 = createResponse1.extract().path("user.email");
        name1 = createResponse1.extract().path("user.name");
        accessToken1 = createResponse1.extract().path("accessToken");
        refreshToken1 = createResponse1.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken1 != null);
        assertTrue(refreshToken1 != null);

        // Создание user-2 с логином, паролем и именем и получение респонса
        User user2 = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse2 = userCreate.createNewUser(user2);
        statusCode = createResponse2.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse2.extract().path("success"); // Вытащили сообщение при создании пользователя
        email2 = createResponse2.extract().path("user.email");
        name2 = createResponse2.extract().path("user.name");
        accessToken2 = createResponse2.extract().path("accessToken");
        refreshToken2 = createResponse2.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken2 != null);
        assertTrue(refreshToken2 != null);

        //Редактируем информацию о user-2, чтобы его email2 = email1, в метод передаем токен-2 от user-2 и проверяем респонс
        user2.setEmail(email1);
        ValidatableResponse getInfoResponse = userCreate.editUser(accessToken2, user2);
        statusCode = getInfoResponse.extract().statusCode();
        isInfoReceived = getInfoResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        String message = getInfoResponse.extract().path("message");
        assertEquals(HTTP_FORBIDDEN, statusCode); // Сравнили статус коды и ответ
        assertFalse(isInfoReceived);
        assertEquals("User with such email already exists", message);
    }

    // Удалили пользователей, указав их токены accessToken
    @After
    public void clearDown() {
    String[] accessTokenList = {accessToken1, accessToken2};
        for(int i = 0; i < accessTokenList.length; i++){
            userCreate.deleteUser(accessTokenList[i]);
        }
    }
}
