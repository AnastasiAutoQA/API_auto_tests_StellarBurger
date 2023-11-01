package order;
import base.BaseTestOrder;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Ingredients;
import model.Orders;
import org.junit.Test;
import model.OrderAutoGenerator;
import model.OrderRequest;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;
public class  TestOrder extends BaseTestOrder {
    private boolean isOrderCreated;
    private OrderRequest orderRequest;
    private int statusCode;
    private Orders orders;
    private Ingredients ingredients;

    @DisplayName("Получение списка заказов")
    @Description("Проверить, что в тело ответа возвращается список заказов")
    @Test
    public void shouldReturnOrdersList() {
        ValidatableResponse receiveResponse = orderConfig.getAllOrders(); // Получаем список заказов
        statusCode = receiveResponse.extract().statusCode(); // Получаем статус код ответа (должно быть 200 при успешном)
        orders = receiveResponse.extract().body().as(Orders.class); // Получили список заказов
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды, задали сообщ об ошибке, если они не равны
        assertNotNull("The list of orders is not provided", orders); // Проверили, что полученный список не пустой
    }
    @DisplayName("Получение списка ингредиентов")
    @Description("Проверить, что в тело ответа возвращается список всех ингредиентов")
    @Test
    public void shouldReturnIngredientsList() {
        ValidatableResponse receiveResponse = orderConfig.getIngredientsList(); // Получаем список ингредиентов
        statusCode = receiveResponse.extract().statusCode(); // Получаем статус код ответа (должно быть 200 при успешном)
        ingredients = receiveResponse.extract().body().as(Ingredients.class); // Получили список ингредиентов
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды, задали сообщ об ошибке, если они не равны
        assertNotNull("The list of orders is not provided", ingredients); // Проверили, что полученный список не пустой

    }
    @DisplayName("Создание заказа без ингредиентов невозможна и проверка ответа")
    @Description("Проверить, что система выдает ошибку, если нет ни одного ингредиента")
    @Test
    public void shouldNotCreateOrderWithoutIngredients(){
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали пустой заказ - не передали никаких ингредиентов
        ValidatableResponse createResponse = orderConfig.createNewOrder(orderRequest);
        statusCode = createResponse.extract().statusCode();
        isOrderCreated = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        assertEquals( HTTP_BAD_REQUEST, statusCode);
        assertFalse(isOrderCreated);
        assertEquals("Ingredient ids must be provided", message);
    }
    @DisplayName("Ошибка при попытке получить список заказов без авторизации")
    @Description("Система возвращает ошибку, если пользователь неавторизован, списка заказов нет")
    @Test
    public void shouldNotReturnOrdersListForUnauthorizedUser(){
        ValidatableResponse makeOrderResponse = userConfig.getOrdersListAuthorizedUser("");
        statusCode = makeOrderResponse.extract().statusCode();
        boolean orderListResult = makeOrderResponse.extract().path("success");
        String message = makeOrderResponse.extract().path("message");
        assertFalse(orderListResult);
        assertEquals(HTTP_UNAUTHORIZED, statusCode);
        assertEquals("You should be authorised", message);
    }
}
