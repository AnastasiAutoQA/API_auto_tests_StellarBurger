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
import static org.junit.Assert.assertEquals;

public class TestUnauthorizedOrderNotLostAfterAuthorization {
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

    public TestUnauthorizedOrderNotLostAfterAuthorization() { }

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
        orderCreate = new OrderApiConfig();
    }
    @DisplayName("Невторизованный пользователь собирает заказ, потом логиниться, \n" +
            "детали заказа не затираются и авторизованный пользователь может сделать тот же заказ и не создавать заново")
    @Description("Проверить, что система не удаляет детали заказа неавторизованного пользователя после логина, \n" +
            "связывает заказ с этим пользователем после авторизации, \n" +
            "и возвращает список заказов этого пользователя с этим заказом - возвращаются те же ингредиенты и имя заказа")
    @Test
    public void shouldKeepOrderMadeByUnauthorizedUserAndReturnSameOrderAfterLogin() {
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
        String[] ingredientsSentList = orderRequest.getIngredients(); //Получили список игредиентов, чтоб потом сравнить в респонсе заказа
        ArrayList<String> arrayIngredientsSentList = new ArrayList<>(Arrays.asList(ingredientsSentList)); //Преобразовали строку с ингредиентами в массив
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
        assertEquals(null, orders); // Проверили, что полученный заказов этого пользователя список пустой

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

        //Авторизованный пользователь делает этот заказ
        ValidatableResponse orderResponseAfterLogin = userCreate.createOrdersAuthorizedUser(accessToken, orderRequest);
        statusCode = orderResponseAfterLogin.extract().statusCode();
        isOrderCreated = orderResponseAfterLogin.extract().path("success");
        String name1 = orderResponseAfterLogin.extract().path("name");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);

        //Проверяем, что у этого пользователя есть заказ, детали заказа такие же как до авторизации - имя заказа и ингредиенты (посылаем реквест на /api/orders + accessToken)
        ValidatableResponse makeOrderResponseAuthorized = userCreate.getOrdersListAuthorizedUser(accessToken);
        statusCode = makeOrderResponseAuthorized.extract().statusCode();
        orders = makeOrderResponseAuthorized.extract().body().as(Order.class);
        String name2 = makeOrderResponseAuthorized.extract().path("orders[0].name");
        ArrayList<String> ingredientsReceivedList = makeOrderResponseAuthorized.extract().path("orders[0].ingredients");
        assertEquals(HTTP_OK, statusCode);
        assertEquals(name1, name2);
        assertEquals(arrayIngredientsSentList, ingredientsReceivedList); // Сравнили ингредиенты при создании заказа и в респонсе заказа пользователя
        assertNotNull(orders); // Проверили, что полученный список не пустой

        System.out.println(arrayIngredientsSentList);
        System.out.println(ingredientsReceivedList);

    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userCreate.deleteUser(accessToken);
    }

}
