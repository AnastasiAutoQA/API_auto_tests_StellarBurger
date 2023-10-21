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
import java.util.ArrayList;
import java.util.Arrays;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

public class TestGetOrdersListAuthorizedUser {
    private UserAPIConfig userCreate;
    private String accessToken;
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isOrderCreated;
    private boolean isUserLoggedIn;
    private OrderApiConfig orderCreate;
    private OrderRequest orderRequest;
    private Object orders;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
        orderCreate = new OrderApiConfig();
    }

    @DisplayName("Авторизованный пользователь делает заказ и возвращается список заказов авторизованного пользователя")
    @Description("Проверить, что система связывает заказ с конкретным пользователем, если пользователь авторизовался")
    @Test
    public void shouldCreateOrderAndReturnOrdersListForAuthorizedUser() {
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

        //Создаем заказ под авторизованным пользователем - на эндпоинт передаем accessToken и детали заказа
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(OrderAutoGenerator.getOrderWithSomeIngredients(2)); // Задали ингредиенты - заполнили список хэшами ингредиентов
        String[] ingredientsSentList = orderRequest.getIngredients(); //Получили список игредиентов, которые передаем при создании заказа, чтоб потом сравнить в респонсе заказа
        ArrayList<String> arrayIngredientsSentList = new ArrayList<>(Arrays.asList(ingredientsSentList)); //Преобразовали строку с ингредиентами в массив
        ValidatableResponse createOrderResponse = userCreate.createOrdersAuthorizedUser(accessToken, orderRequest);
        statusCode = createOrderResponse.extract().statusCode();
        isOrderCreated = createOrderResponse.extract().path("success");
        String name1 = createOrderResponse.extract().path("name");
        int number1 = createOrderResponse.extract().path("order.number");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);
        assertTrue(number1 != 0);

        //Проверяем, что у нового пользователя есть этот заказ, номер и детали заказа те же (имя, ингредиенты) (посылаем реквест на /api/orders + accessToken)
        ValidatableResponse makeOrderResponse = userCreate.getOrdersListAuthorizedUser(accessToken);
        statusCode = makeOrderResponse.extract().statusCode();
        orders = makeOrderResponse.extract().body().as(Order.class);
        int number2 = makeOrderResponse.extract().path("orders[0].number");
        String name2 = makeOrderResponse.extract().path("orders[0].name");
        ArrayList<String> ingredientsReceivedList = makeOrderResponse.extract().path("orders[0].ingredients");
        assertEquals(HTTP_OK, statusCode);
        assertEquals(number1, number2);
        assertEquals(name1, name2);
        assertEquals(arrayIngredientsSentList, ingredientsReceivedList); // Сравнили ингредиенты при создании заказа и в респонсе заказа пользователя
        assertNotNull(orders);
        assertEquals(HTTP_OK, statusCode);
        assertNotNull(orders);

        System.out.println(arrayIngredientsSentList);
        System.out.println(ingredientsReceivedList);
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }

}
