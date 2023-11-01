package base;
import client.OrderApiConfig;
import client.UserAPIConfig;
import org.junit.After;
import org.junit.Before;

public class BaseTestOrder {
    protected static OrderApiConfig orderConfig;
    protected static UserAPIConfig userConfig;

    @Before
    public void setUp() {
        orderConfig = new OrderApiConfig();
        userConfig = new UserAPIConfig();
    }

}
