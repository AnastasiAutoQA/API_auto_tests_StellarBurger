package order;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.api_config.OrderApiConfig;
import org.junit.Before;
import org.junit.Test;
import test_data_models.OrderAutoGenerator;
import test_data_models.OrderRequest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestCreateOrderWrongIngredientsError {
    private boolean isOrderCreated;
    private OrderApiConfig orderCreate;
    private OrderRequest orderRequest;
    private int statusCode;
    private String[] ingredients;

    public TestCreateOrderWrongIngredientsError(String[] ingredients, boolean isOrderCreated) {
        this.ingredients = ingredients;
        this.isOrderCreated = isOrderCreated;
    }

    @Parameterized.Parameters(name = "хэш id ингредиента: {0}, создан ли заказ {1}")
    public static Object[][] getIngredientsData() {
        return new Object[][]{
                {OrderAutoGenerator.getOrderWithWrongIngredients(1), false},
                {OrderAutoGenerator.getOrderWithWrongIngredients(3), false},
                {OrderAutoGenerator.getOrderWithWrongIngredients(4), false}

        };
    }

    @Before
    public void setUp() {
        orderCreate = new OrderApiConfig();
    }

    @DisplayName("Создание заказа с неверным хэш id ингредиентов невозможна и проверка ответа")
    @Description("Проверить, что система выдает ошибку, если переданы неверные хэш id ингредиентов")
    @Test
    public void shouldNotCreateOrderWithWrongIngredients(){
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(this.ingredients); // Передали рандомные ингредиенты
        ValidatableResponse createResponse = orderCreate.createNewOrder(orderRequest);
        statusCode = createResponse.extract().statusCode();
        assertEquals(HTTP_INTERNAL_ERROR, statusCode);
    }
}
