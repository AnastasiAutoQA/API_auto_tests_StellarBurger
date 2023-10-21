package order;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.api_config.OrderApiConfig;
import org.example.api_config.UserAPIConfig;
import org.junit.Before;
import org.junit.Test;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.*;

public class TestGetOrdersListUnauthorizedUserError {
    private UserAPIConfig userCreate;
    private int statusCode;
    private OrderApiConfig orderCreate;

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
        orderCreate = new OrderApiConfig();
    }
    @DisplayName("Ошибка при попытке получить список заказов без авторизации")
    @Description("Система возвращает ошибку, если пользователь неавторизован, списка заказов нет")
    @Test
    public void shouldNotReturnOrdersListForUnauthorizedUser(){
        ValidatableResponse makeOrderResponse = userCreate.getOrdersListAuthorizedUser("");
        statusCode = makeOrderResponse.extract().statusCode();
        boolean orderListResult = makeOrderResponse.extract().path("success");
        String message = makeOrderResponse.extract().path("message");
        assertFalse(orderListResult);
        assertEquals(HTTP_UNAUTHORIZED, statusCode);
        assertEquals("You should be authorised", message);
    }
}
