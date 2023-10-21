package order;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.api_config.OrderApiConfig;
import org.junit.Before;
import org.junit.Test;
import test_data_models.Orders;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestGetAllOrdersList {
    private OrderApiConfig orderConfig;
    private int statusCode;
    private Orders orders;

    @Before
    public void setUp() {
        orderConfig = new OrderApiConfig();
    }

    @DisplayName("Получение списка заказов")
    @Description("Проверить, что в тело ответа возвращается список заказов")
    @Test
    public void shouldReturnOdersList() {
        ValidatableResponse receiveResponse = orderConfig.getAllOrders(); // Получаем список заказов
        statusCode = receiveResponse.extract().statusCode(); // Получаем статус код ответа (должно быть 200 при успешном)
        orders = receiveResponse.extract().body().as(Orders.class); // Получили список заказов
        assertEquals("The status code is invalid", HTTP_OK, statusCode); // Сравнили статус коды, задали сообщ об ошибке, если они не равны
        assertNotNull("The list of orders is not provided", orders); // Проверили, что полученный список не пустой
    }
}
