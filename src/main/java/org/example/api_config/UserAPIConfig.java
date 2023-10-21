package org.example.api_config;
import test_data_models.OrderRequest;
import test_data_models.User;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import test_data_models.UserLogin;
import static io.restassured.RestAssured.given;

// Доки https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf

public class UserAPIConfig extends MainUrlConfig {
    private static final String NEW_USER_URI = URI + "api/auth/register";
    private static final String USER_LOGIN_URI = URI + "api/auth/login";
    private static final String USER_LOGOUT_URI = URI + "api/auth/logout";
    private static final String USER_INFO_URI = URI + "api/auth/user"; // GET, DELETE, PATCH
    private static final String TOKEN_UPDATE_URI = URI + "api/auth/token";
    private static final String ORDERS_URI = URI + "api/orders"; // POST, GET(+accessToken)

    @Step("Создание нового пользователя user")
    public ValidatableResponse createNewUser(User user) {
        return given().log().all()
                .spec(getHeader())
                .body(user)
                .when()
                .post(NEW_USER_URI)
                .then().log().all();
    }
    @Step("Авторизация пользователя userLogin")
    public ValidatableResponse loginUserAndCheckResponse(UserLogin userLogin) {
        return given().log().all()
                .spec(getHeader())
                .body(userLogin)
                .when()
                .post(USER_LOGIN_URI)
                .then().log().all();
    }
    @Step("Выход пользователя из системы по его refreshToken")
    public ValidatableResponse logoutUserAndCheckResponse(String refreshToken) {
        String json = "{\"token\": \"" + refreshToken + "\"}";
        return given().log().all()
                .spec(getHeader())
                .body(json)
                .when()
                .post(USER_LOGOUT_URI)
                .then().log().all();
    }

    /* Для удаления пользователя, получения или обновления инфо о user нужен его "accessToken",
    он приходит в таком формате:
    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY1MjY5MGRhOWVkMjgwMDAxYjM3MDhkNyIsImlhdCI6MTY5NzAyNjI2NiwiZXhwIjoxNjk3MDI3NDY2fQ.VHdeLKzEO1kEk_gKKgxooWsWoYy86e3OLajI-QZufWY",
    Поэтому при аутентификации придется убрать слово с пробелами "Bearer ". */
    @Step("Получение инфо о пользователе по его accessToken")
    public ValidatableResponse getUserData(String accessToken) {
        return given().log().all()
                .spec(getHeader())
                .auth().oauth2(accessToken.replace("Bearer ", "")) // аутентификация по accessToken при выполнении запроса на удаление
                .get(USER_INFO_URI)
                .then().log().all();
    }
    @Step("Обновление/изменение инфо о пользователе по его accessToken")
    public ValidatableResponse editUser(String accessToken, User user) {
        return given().log().all()
                .spec(getHeader())
                .auth().oauth2(accessToken.replace("Bearer ", "")) // аутентификация по accessToken при выполнении запроса на удаление
                .body(user)
                .patch(USER_INFO_URI)
                .then().log().all();
    }

    @Step("Удаление пользователя по его accessToken")
    public ValidatableResponse deleteUser(String accessToken) {
        return given().log().all()
                .spec(getHeader())
                .auth().oauth2(accessToken.replace("Bearer ", "")) // аутентификация по accessToken при выполнении запроса на удаление
                .delete(USER_INFO_URI)
                .then().log().all();
    }
    @Step("Запрос на обновление токенов пользователя по его refreshToken")
    public ValidatableResponse requestNewTokensAndCheckResponse(String refreshToken) {
        String json = "{\"token\": \"" + refreshToken + "\"}";
        return given().log().all()
                .spec(getHeader())
                .body(json)
                .when()
                .post(TOKEN_UPDATE_URI)
                .then().log().all();
    }

    @Step("Создание заказа/заказов конкретного пользователя - авторизация по accessToken")
    public ValidatableResponse createOrdersAuthorizedUser(String accessToken, OrderRequest orderRequest) {
        return given().log().all()
                .spec(getHeader())
                .auth().oauth2(accessToken.replace("Bearer ", "")) // аутентификация по accessToken при выполнении запроса на удаление
                .body(orderRequest)
                .post(ORDERS_URI)
                .then().log().all();
    }

    @Step("Получение списка заказов конкретного пользователя - авторизация по accessToken")
    public ValidatableResponse getOrdersListAuthorizedUser(String accessToken) {
        return given().log().all()
                .spec(getHeader())
                .auth().oauth2(accessToken.replace("Bearer ", "")) // аутентификация по accessToken при выполнении запроса на удаление
                .get(ORDERS_URI)
                .then().log().all();
    }

}
