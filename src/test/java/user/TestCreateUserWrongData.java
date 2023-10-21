package user;
import org.example.api_config.UserAPIConfig;
import org.junit.Before;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import test_data_models.User;
import test_data_models.UserAutoGenerator;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestCreateUserWrongData {
    private final String email;
    private final String password;
    private final String name;
    private boolean isUserCreated;
    private UserAPIConfig userCreate;
    private User user;
    private int statusCode;

    public TestCreateUserWrongData(String email, String password, String name, boolean isUserCreated){
        this.email = email;
        this.password = password;
        this.name = name;
        this.isUserCreated = isUserCreated;
    }

    // Задаем набор тестовых данных {email, password, name, isUserCreated} с провальными значениями
    @Parameterized.Parameters(name = "email: {0}, password: {1}, name: {2}, создан ли пользователь: {3}")
    public static Object[][] userTestData() {
        return new Object[][]{
                {UserAutoGenerator.getRandomEmail(), UserAutoGenerator.getRandomPassword(), "", false},
                {UserAutoGenerator.getRandomEmail(), UserAutoGenerator.getRandomPassword(), null, false},
                {UserAutoGenerator.getRandomEmail(), "", "", false},
                {UserAutoGenerator.getRandomEmail(), "", null, false},
                {UserAutoGenerator.getRandomEmail(), null, null, false},
                {"", UserAutoGenerator.getRandomPassword(), UserAutoGenerator.getRandomName(), false},
                {null, UserAutoGenerator.getRandomPassword(), UserAutoGenerator.getRandomName(), false},
                {"", UserAutoGenerator.getRandomPassword(), "", false},
                {"", UserAutoGenerator.getRandomPassword(), null, false},
                {null, UserAutoGenerator.getRandomPassword(), null, false},
                {"", "", UserAutoGenerator.getRandomName(), false},
                {"", null, UserAutoGenerator.getRandomName(), false},
                {null, "", UserAutoGenerator.getRandomName(), false},
                {null, null, UserAutoGenerator.getRandomName(), false},
                {"", "", "", false},
                {null, null, null, false}
        };
    }

    @Before
    public void setUp() {
        userCreate = new UserAPIConfig();
    }

    @DisplayName("Ошибка в ответе при отсутствии одного или нескольких обязательных полей.")
    @Description("Проверить, что пользователя нельзя создать, если одно из обязательных полей отсутствует:\n" +
            "    - email;\n" +
            "    - password;\n" +
            "    - name.")

    @Test
    public void shouldNotCreateUserWithoutRequiredFields() {
        user = new User();
        user.setEmailPasswordAndName(this.email, this.password, this.name); // Задали пользователю данные из параметров
        ValidatableResponse createResponse = userCreate.createNewUser(user);
        statusCode = createResponse.extract().statusCode(); // Вытащили статус код респонса для сравнения
        String errorMessage = createResponse.extract().path("message"); // Вытащили сообщ респонса для сравнения ниже
        assertEquals("The status code is invalid", HTTP_FORBIDDEN, statusCode);
        assertFalse(isUserCreated);
        assertEquals("Email, password and name are required fields", errorMessage);
    }

}
