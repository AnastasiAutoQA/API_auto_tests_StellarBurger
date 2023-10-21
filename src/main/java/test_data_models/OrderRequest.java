package test_data_models;

    /*Json для создания нового заказа по хэшу ингредиентов (передать один или несколько ингредиентов):
    { "ingredients": ["61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa6e", "61c0c5a71d1f82001bdaaa6c"]
    }*/
public class OrderRequest {
        private String[] ingredients;
        public OrderRequest(String [] ingredients){
                this.ingredients = ingredients;
        }
        public String[] getIngredients() { return ingredients; }
        public void setIngredients(String[] ingredients) {
            this.ingredients = ingredients;
        }
}
