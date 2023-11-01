package model;
import java.util.ArrayList;
public class Orders {
    private ArrayList<Order> orders;

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    /*@Override
    public String toString() {
        return "Orders{" +
                "orders=" + orders +
                '}';
    }*/
}