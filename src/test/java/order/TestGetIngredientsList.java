package order;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.api_config.OrderApiConfig;
import org.junit.Before;
import org.junit.Test;
import test_data_models.Ingredients;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestGetIngredientsList {
    private OrderApiConfig orderConfig;
    private int statusCode;
    private Ingredients ingredients;

    @Before
    public void setUp() {
        orderConfig = new OrderApiConfig();
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
}
