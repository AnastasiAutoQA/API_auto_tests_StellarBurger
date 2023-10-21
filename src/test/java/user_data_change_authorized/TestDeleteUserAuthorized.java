package user_data_change_authorized;
import io.restassured.response.ValidatableResponse;
import org.example.api_config.UserAPIConfig;
import org.junit.Before;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import test_data_models.User;
import test_data_models.UserAutoGenerator;
import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDeleteUserAuthorized {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isUserDeleted;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Удаление пользователя по его accessToken, и проверка тела ответа")
    @Description("Проверить, что пользователя можно удалять, авторизовавшись по accessToken")
    @Test
    public void shouldDeleteAuthorizedUser(){
        // Создание пользователя с логином, паролем и именем
        User user = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        //Удаляем пользователя, в метод передаем его токен accessToken и проверяем респонс
        ValidatableResponse deleteResponse = userCreate.deleteUser(accessToken);
        statusCode = deleteResponse.extract().statusCode();
        isUserDeleted = deleteResponse.extract().path("success");
        String message = deleteResponse.extract().path("message");
        assertEquals("The status code is invalid", HTTP_ACCEPTED, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserDeleted);
        assertEquals("User successfully removed", message);
    }
}
