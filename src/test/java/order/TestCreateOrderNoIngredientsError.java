package order;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.api_config.OrderApiConfig;
import org.junit.Before;
import org.junit.Test;
import test_data_models.OrderAutoGenerator;
import test_data_models.OrderRequest;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static org.junit.Assert.*;

public class TestCreateOrderNoIngredientsError {
    private boolean isOrderCreated;
    private OrderApiConfig orderCreate;
    private OrderRequest orderRequest;
    private int statusCode;

    @Before
    public void setUp() {
        orderCreate = new OrderApiConfig();
    }

    @DisplayName("Создание заказа без ингредиентов невозможна и проверка ответа")
    @Description("Проверить, что система выдает ошибку, если нет ни одного ингредиента")
    @Test
    public void shouldNotCreateOrderWithoutIngredients(){
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали пустой заказ - не передали никаких ингредиентов
        ValidatableResponse createResponse = orderCreate.createNewOrder(orderRequest);
        statusCode = createResponse.extract().statusCode();
        isOrderCreated = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        assertEquals( HTTP_BAD_REQUEST, statusCode);
        assertFalse(isOrderCreated);
        assertEquals("Ingredient ids must be provided", message);
    }
}
