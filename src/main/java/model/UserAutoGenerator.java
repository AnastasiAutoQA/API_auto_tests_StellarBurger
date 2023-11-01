package model;
import org.apache.commons.lang3.RandomStringUtils;

// Методы для автогенерации данных Пользователя
public class UserAutoGenerator {
    public static User getRandomUserData() {
        String email = RandomStringUtils.randomAlphanumeric(6) + "@"
        + RandomStringUtils.randomAlphabetic(5) + "." + RandomStringUtils.randomAlphabetic(2);
        String password = RandomStringUtils.randomAlphanumeric(10);
        String name = RandomStringUtils.randomAlphanumeric(10);
        return new User(email, password, name);
    }
    public static String getRandomPassword() {
        String password = RandomStringUtils.randomAlphanumeric(5);
        return password;
    }
    public static String getRandomEmail() {
        String email = (RandomStringUtils.randomAlphanumeric(5) + "@"
                + RandomStringUtils.randomAlphabetic(5) + "." + RandomStringUtils.randomAlphabetic(2));
        return email;
    }
    public static String getRandomName() {
        String name = RandomStringUtils.randomAlphanumeric(10);
        return name;
    }
}
