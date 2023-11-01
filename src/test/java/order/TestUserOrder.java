package order;
import base.BaseTestUserOrder;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import model.*;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;
public class TestUserOrder extends BaseTestUserOrder {
    private String refreshToken;
    private int statusCode;
    private boolean isUserCreated;
    private boolean isOrderCreated;
    private boolean isUserLoggedIn;
    private OrderRequest orderRequest;
    private Object orders;

    // Перед каждым тестом происходит создание пользователя с логином, паролем и именем
    // и получение респонса и accessToken - BaseTestUserOrder @Before
    // После каждого теста происходит удаление пользователя- BaseTestUserOrder @After
    @DisplayName("Создание заказа авторизованным пользователем")
    @Description("Проверить, что система успешно создает заказ, если пользователь авторизован по accessToken")
    @Test
    public void shouldMakeOrderForAuthorizedUserAndReturnOrdersList() {
        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        accessToken = loginResponse.extract().path("accessToken");

        //Создаем заказ под авторизованным пользователем - передаем на эндпоинт accessToken и детали заказа
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(OrderAutoGenerator.getOrderWithSomeIngredients(2)); // Задали ингредиенты - заполнили список хэшами ингредиентов
        ValidatableResponse createOrderResponse = userConfig.createOrdersAuthorizedUser(accessToken, orderRequest);
        statusCode = createOrderResponse.extract().statusCode();
        isOrderCreated = createOrderResponse.extract().path("success");
        int number = createOrderResponse.extract().path("order.number");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);
        assertTrue(number != 0);
    }
    @DisplayName("Авторизованный пользователь делает заказ и возвращается список заказов авторизованного пользователя")
    @Description("Проверить, что система связывает заказ с конкретным пользователем, если пользователь авторизовался")
    @Test
    public void shouldCreateOrderAndReturnOrdersListForAuthorizedUser() {
        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        accessToken = loginResponse.extract().path("accessToken");

        //Создаем заказ под авторизованным пользователем - на эндпоинт передаем accessToken и детали заказа
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(OrderAutoGenerator.getOrderWithSomeIngredients(2)); // Задали ингредиенты - заполнили список хэшами ингредиентов
        String[] ingredientsSentList = orderRequest.getIngredients(); //Получили список игредиентов, которые передаем при создании заказа, чтоб потом сравнить в респонсе заказа
        ArrayList<String> arrayIngredientsSentList = new ArrayList<>(Arrays.asList(ingredientsSentList)); //Преобразовали строку с ингредиентами в массив
        ValidatableResponse createOrderResponse = userConfig.createOrdersAuthorizedUser(accessToken, orderRequest);
        statusCode = createOrderResponse.extract().statusCode();
        isOrderCreated = createOrderResponse.extract().path("success");
        String name1 = createOrderResponse.extract().path("name");
        int number1 = createOrderResponse.extract().path("order.number");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);
        assertTrue(number1 != 0);

        //Проверяем, что у нового пользователя есть этот заказ, номер и детали заказа те же (имя, ингредиенты) (посылаем реквест на /api/orders + accessToken)
        ValidatableResponse makeOrderResponse = userConfig.getOrdersListAuthorizedUser(accessToken);
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
    @DisplayName("Неавторизованный пользователь собирает заказ, но возвращается пустой список заказов этого неавторизованного пользователя")
    @Description("Проверить, что система не связывает заказ с конкретным пользователем, если пользователь Не авторизовался")
    @Test
    public void shouldCreateOrderWithUnauthorizedUserButReturnBlankOrderList() {
        //Создаем заказ без авторизации и логина (передаем пустое поле токена)
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(OrderAutoGenerator.getOrderWithSomeIngredients(2)); // Задали ингредиенты - заполнили список хэшами ингредиентов
        ValidatableResponse createOrderResponse = userConfig.createOrdersAuthorizedUser("", orderRequest);
        statusCode = createOrderResponse.extract().statusCode();
        isOrderCreated = createOrderResponse.extract().path("success");
        int number = createOrderResponse.extract().path("order.number");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);
        assertTrue(number != 0);

        //Проверяем, что у пользователя нет заказов - список заказов пустой order: [] (посылаем реквест на /api/orders + accessToken)
        ValidatableResponse makeOrderResponse = userConfig.getOrdersListAuthorizedUser(accessToken);
        statusCode = makeOrderResponse.extract().statusCode();
        orders = makeOrderResponse.extract().path("order");
        assertEquals(HTTP_OK, statusCode);
        assertEquals(null, orders); // Проверили, что полученный список заказов этого пользователя пустой
    }
    @DisplayName("Невторизованный пользователь собирает заказ, потом логиниться," +
            "детали заказа не затираются и авторизованный пользователь может сделать тот же заказ и не создавать заново")
    @Description("Проверить, что система не удаляет детали заказа неавторизованного пользователя после логина," +
            "связывает заказ с этим пользователем после авторизации," +
            "и возвращает список заказов этого пользователя с этим заказом - возвращаются те же ингредиенты и имя заказа")
    @Test
    public void shouldKeepOrderMadeByUnauthorizedUserAndReturnSameOrderAfterLogin() {
        //Создаем заказ без авторизации и логина (передаем пустое поле токена)
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(OrderAutoGenerator.getOrderWithSomeIngredients(2)); // Задали ингредиенты - заполнили список хэшами ингредиентов
        String[] ingredientsSentList = orderRequest.getIngredients(); //Получили список игредиентов, чтоб потом сравнить в респонсе заказа
        ArrayList<String> arrayIngredientsSentList = new ArrayList<>(Arrays.asList(ingredientsSentList)); //Преобразовали строку с ингредиентами в массив
        ValidatableResponse createOrderResponse = userConfig.createOrdersAuthorizedUser("", orderRequest);
        statusCode = createOrderResponse.extract().statusCode();
        isOrderCreated = createOrderResponse.extract().path("success");
        int number = createOrderResponse.extract().path("order.number");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);
        assertTrue(number != 0);

        //Проверяем, что у пользователя нет заказов - список заказов пустой order: [] (посылаем реквест на /api/orders + accessToken)
        ValidatableResponse makeOrderResponse = userConfig.getOrdersListAuthorizedUser(accessToken);
        statusCode = makeOrderResponse.extract().statusCode();
        orders = makeOrderResponse.extract().path("order");
        assertEquals(HTTP_OK, statusCode);
        assertEquals(null, orders); // Проверили, что полученный заказов этого пользователя список пустой

        // Логинимся и забираем из респонса нужные данные
        ValidatableResponse loginResponse = userConfig.loginUserAndCheckResponse(UserLogin.fromUser(user));
        statusCode = loginResponse.extract().statusCode();
        isUserLoggedIn = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken");
        refreshToken = loginResponse.extract().path("refreshToken");
        assertEquals(HTTP_OK, statusCode); // Сравнили статус коды и ответ
        assertTrue("The user is not created", isUserLoggedIn);
        assertTrue(accessToken != null);
        assertTrue(refreshToken != null);

        //Авторизованный пользователь делает этот заказ
        ValidatableResponse orderResponseAfterLogin = userConfig.createOrdersAuthorizedUser(accessToken, orderRequest);
        statusCode = orderResponseAfterLogin.extract().statusCode();
        isOrderCreated = orderResponseAfterLogin.extract().path("success");
        String name1 = orderResponseAfterLogin.extract().path("name");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);

        //Проверяем, что у этого пользователя есть заказ, детали заказа такие же как до авторизации - имя заказа и ингредиенты (посылаем реквест на /api/orders + accessToken)
        ValidatableResponse makeOrderResponseAuthorized = userConfig.getOrdersListAuthorizedUser(accessToken);
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
}
