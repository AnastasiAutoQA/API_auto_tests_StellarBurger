package base;
import client.UserAPIConfig;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserAutoGenerator;
import org.junit.After;
import org.junit.Before;

public class BaseTestUser {
    protected static UserAPIConfig userConfig;
    protected static User user;
    protected ValidatableResponse createResponse;
    protected static String accessToken;
    @Before
    public void setUp() {
        userConfig = new UserAPIConfig();
        user = UserAutoGenerator.getRandomUserData();
        createResponse = userConfig.createNewUser(user);
        accessToken = createResponse.extract().path("accessToken");
    }

    // Удалили пользователя, указав его токен accessToken
    @After
    public void clearDown() {
        userConfig.deleteUser(accessToken);
    }


}
