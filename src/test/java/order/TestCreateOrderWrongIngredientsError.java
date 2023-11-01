package order;
import base.BaseTestOrder;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import model.OrderAutoGenerator;
import model.OrderRequest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestCreateOrderWrongIngredientsError extends BaseTestOrder {
    private boolean isOrderCreated;
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

    @DisplayName("Создание заказа с неверным хэш id ингредиентов невозможна и проверка ответа")
    @Description("Проверить, что система выдает ошибку, если переданы неверные хэш id ингредиентов")
    @Test
    public void shouldNotCreateOrderWithWrongIngredients(){
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(this.ingredients); // Передали рандомные ингредиенты
        ValidatableResponse createResponse = orderConfig.createNewOrder(orderRequest);
        statusCode = createResponse.extract().statusCode();
        assertEquals(HTTP_INTERNAL_ERROR, statusCode);
    }
}
