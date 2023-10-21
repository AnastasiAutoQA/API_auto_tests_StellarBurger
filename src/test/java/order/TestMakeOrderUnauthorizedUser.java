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

public class TestMakeOrderUnauthorizedUser {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isOrderCreated;
    private OrderApiConfig orderCreate;
    private OrderRequest orderRequest;
    private Object orders;

    public TestMakeOrderUnauthorizedUser() { }

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
        orderCreate = new OrderApiConfig();
    }
    @DisplayName("Неавторизованный пользователь собирает заказ, но возвращается пустой список заказов этого неавторизованного пользователя")
    @Description("Проверить, что система не связывает заказ с конкретным пользователем, если пользователь Не авторизовался")
    @Test
    public void shouldCreateOrderWithUnauthorizedUserButReturnBlankOrderList() {
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

        //Создаем заказ без авторизации и логина (передаем пустое поле токена)
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(OrderAutoGenerator.getOrderWithSomeIngredients(2)); // Задали ингредиенты - заполнили список хэшами ингредиентов
        ValidatableResponse createOrderResponse = userCreate.createOrdersAuthorizedUser("", orderRequest);
        statusCode = createOrderResponse.extract().statusCode();
        isOrderCreated = createOrderResponse.extract().path("success");
        int number = createOrderResponse.extract().path("order.number");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);
        assertTrue(number != 0);

        //Проверяем, что у пользователя нет заказов - список заказов пустой order: [] (посылаем реквест на /api/orders + accessToken)
        ValidatableResponse makeOrderResponse = userCreate.getOrdersListAuthorizedUser(accessToken);
        statusCode = makeOrderResponse.extract().statusCode();
        orders = makeOrderResponse.extract().path("order");
        assertEquals(HTTP_OK, statusCode);
        assertEquals(null, orders); // Проверили, что полученный список заказов этого пользователя пустой
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }

}
