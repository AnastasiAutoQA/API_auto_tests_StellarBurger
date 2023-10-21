package test_data_models;

/*Json для создания нового пользователя, POST:
        {
        "email": "test-data@yandex.ru",
        "password": "password",
        "name": "Username"
        }*/

public class User {
    private String email;
    private String password;
    private String name;

    //Конструкторы
    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public User(String password) {
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailPasswordAndName(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

}
