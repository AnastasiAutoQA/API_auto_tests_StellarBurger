package client;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.OrderRequest;
import static io.restassured.RestAssured.given;

// Доки https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf
public class OrderApiConfig extends MainUrlConfig {
    private static final String ORDERS_URI = URI + "api/orders"; // POST, GET(+accessToken)
    private static final String ORDERS_LIST_URI = URI + "api/orders/all"; //GET
    private static final String INGREDIENTS_LIST_URI = URI + "api/ingredients"; // GET

    @Step("Получение инфо о всех заказах")
    public ValidatableResponse getAllOrders() {
        return given()
                .spec(getHeader())
                .get(ORDERS_LIST_URI)
                .then();
    }
    @Step("Получение данных об ингредиентах")
    public ValidatableResponse getIngredientsList(){
        return given()
                .spec(getHeader())
                .get(INGREDIENTS_LIST_URI)
                .then();
    }
    @Step
    public static String getIngredientId(int i){
        String ingredient = given()
                .spec(getHeader())
                .get(INGREDIENTS_LIST_URI)
                .then()
                .extract().path("data[%s]._id", String.valueOf(i));
        return ingredient;
    }
    @Step("Создание нового заказа")
    public ValidatableResponse createNewOrder(OrderRequest orderRequest){
        return given()
                .spec(getHeader())
                .body(orderRequest)
                .post(ORDERS_URI)
                .then();
    }

}
