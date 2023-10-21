package order;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.api_config.OrderApiConfig;
import org.example.api_config.UserAPIConfig;
import org.junit.After;
import org.junit.Before;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import test_data_models.*;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class TestMakeOrderAuthorizedUser {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isOrderCreated;
    private boolean isUserLoggedIn;
    private OrderApiConfig orderCreate;
    private OrderRequest orderRequest;

    public TestMakeOrderAuthorizedUser() { }

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
        orderCreate = new OrderApiConfig();
    }

    @DisplayName("Создание заказа авторизованным пользователем")
    @Description("Проверить, что система успешно создает заказ, если пользователь авторизован по accessToken")
    @Test
    public void shouldMakeOrderForAuthorizedUserAndReturnOrdersList() {
        // Создание пользователя с логином, паролем и именем
        User user = UserAutoGenerator.getRandomUserData();
        ValidatableResponse createResponse = userCreate.createNewUser(user); // и получение респонса
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код ответа при создании пользователя
        isUserCreated = createResponse.extract().path("success"); // Вытащили сообщение при создании пользователя
        accessToken = createResponse.extract().path("accessToken");
        refreshToken = createResponse.extract().path("refreshToken");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue(isUserCreated);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userCreate.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserLoggedIn);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        //Создаем заказ под авторизованным пользователем - передаем на эндпоинт accessToken и детали заказа
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(OrderAutoGenerator.getOrderWithSomeIngredients(2)); // Задали ингредиенты - заполнили список хэшами ингредиентов
        ValidatableResponse createOrderResponse = userCreate.createOrdersAuthorizedUser(accessToken, orderRequest);
        statusCode = createOrderResponse.extract().statusCode();
        isOrderCreated = createOrderResponse.extract().path("success");
        int number = createOrderResponse.extract().path("order.number");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);
        assertTrue(number != 0);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }
}
