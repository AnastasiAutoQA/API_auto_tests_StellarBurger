package test_data_models;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api_config.OrderApiConfig;

public class OrderAutoGenerator {
    public static OrderRequest getBlankOrder() {
        String[] ingredients = {};
        return new OrderRequest(ingredients);
    }
    public static String[] getOrderWithSomeIngredients(int numberOfIngr){
        String[] ingredients = new String[numberOfIngr];
        for (int i = 0; i <= (ingredients.length - 1); i++) {
            ingredients[i] = OrderApiConfig.getIngredientId(i).toString();
        }
        return ingredients;
    }
    public static String[] getOrderWithWrongIngredients(int numberOfIngr) {
        String[] ingredients = new String[numberOfIngr];
        for (int i = 0; i <= (ingredients.length - 1); i++) {
            ingredients[i] = RandomStringUtils.randomAlphanumeric(8);
        }
        return ingredients;
    }
}
