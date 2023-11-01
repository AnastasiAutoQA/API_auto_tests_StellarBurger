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
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TestCreateOrderWithIngredients extends BaseTestOrder {
    private String[] ingredients;
    private boolean isOrderCreated;
    private OrderRequest orderRequest;
    private int number;
    private int statusCode;

    public TestCreateOrderWithIngredients(String[] ingredients, boolean isOrderCreated) {
        this.ingredients = ingredients;
        this.isOrderCreated = isOrderCreated;
    }

    //В параметрах в методе создания игредиентов задаем количество ингредиентов,
    // а сами ингредиенты подставятся методом из списка имеющихся валидных ингредиентов
    @Parameterized.Parameters(name = "хэш id ингредиента: {0}, создан ли заказ {1}")
    public static Object[][] getIngredientsData() {
        return new Object[][]{
                {OrderAutoGenerator.getOrderWithSomeIngredients(1), true},
                {OrderAutoGenerator.getOrderWithSomeIngredients(2), true},
                {OrderAutoGenerator.getOrderWithSomeIngredients(3), true},
                {OrderAutoGenerator.getOrderWithSomeIngredients(4), true},
                {OrderAutoGenerator.getOrderWithSomeIngredients(5), true},
                {OrderAutoGenerator.getOrderWithSomeIngredients(6), true},
                {OrderAutoGenerator.getOrderWithSomeIngredients(8), true}
        };
    }

    @DisplayName("Создание заказа с указанием разных ингредиентов и проверка ответа")
    @Description("Проверить, что можно создать заказ с одним или несколькими ингредиентами")
    @Test
    public void shouldCreateNewOrderWithSomeIngredients() {
        orderRequest = OrderAutoGenerator.getBlankOrder(); // Создали заказ с пустым списком ингредиентов
        orderRequest.setIngredients(this.ingredients); // Задали ингредиенты - заполнили список хэшами ингредиентов
        ValidatableResponse createResponse = orderConfig.createNewOrder(orderRequest);
        statusCode = createResponse.extract().statusCode();
        isOrderCreated = createResponse.extract().path("success");
        number = createResponse.extract().path("order.number");
        assertEquals("The status code is invalid", HTTP_OK, statusCode);
        assertTrue(isOrderCreated);
        assertTrue("The order is not created", number != 0);
    }
}



