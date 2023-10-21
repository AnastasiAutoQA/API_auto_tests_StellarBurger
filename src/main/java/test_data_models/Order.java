package test_data_models;
/*Json {
        "success": true,
        "orders": [
        {
        "_id": "652874719ed280001b370d37",
        "ingredients": [
        "61c0c5a71d1f82001bdaaa6d",
        "61c0c5a71d1f82001bdaaa6f",
        "61c0c5a71d1f82001bdaaa72"
        ],
        "status": "done",
        "name": "Spicy бессмертный флюоресцентный бургер",
        "createdAt": "2023-10-12T22:34:25.496Z",
        "updatedAt": "2023-10-12T22:34:25.700Z",
        "number": 22297
        }*/

import java.util.Date;

public class Order {
    private String _id;
    private String[] ingredients; //список хэшей ингредиентов
    private String status;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private int number;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
