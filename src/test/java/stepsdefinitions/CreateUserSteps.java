package stepsdefinitions;

import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.UserRepository;
import com.management.jupiter.services.AdminService;
import com.management.jupiter.services.UserServices;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class CreateUserSteps {
    private String username;
    private String email;
    private String password;
    private Role type;

    private User userCreated;
    private User userFound;


    private String originalCsvContent;

    private Exception exception;
    private static final Path USERS_CSV =
            Path.of("data/users.csv");

    @Before("@create-user")
    public void saveData() throws IOException {
        originalCsvContent = Files.readString(USERS_CSV);
    }

    @After("@create-user")
    public void restoreCsv() throws IOException {
        Files.writeString(USERS_CSV, originalCsvContent);
    }


    @Given("an administrator is authenticated")
    public void anAdministratorIsAuthenticated() throws Exception {
        UserServices.LoginService("juan@gmail.com", "12345");
    }

    @Given("a valid username {string}")
    public void insertUsername(String username) {
        this.username = username;
    }

    @Given("a valid email {string}")
    public void insertEmail(String email) {
        this.email = email;
    }

    @Given("a valid password {string}")
    public void insertPassword(String password) {
        this.password = password;
    }

    @Given("the role {string}")
    public void insertType(String type) {
        this.type = Role.valueOf(type.toUpperCase());
    }


    @When("the administrator submits the user creation request")
    public void submitCreationUserRequest() throws Exception {
        try {
            userCreated = AdminService.createUser(
                    username,
                    email,
                    password,
                    type,
                    Clan.HAMILTON,
                    TlType.PROGRAMACION
            );
            System.out.println(userCreated);
        } catch (Exception e) {
            exception = e;
            System.out.println(e.getMessage());
        }
    }

    @Then("the user should be created successfully")
    public void userCreatedSuccessfully() {
        assertEquals(userCreated.getEmail(), email);
    }

    @Then("the user should have the role {string}")
    public void validateRoleUserCrested(String role) {
        assertEquals(Role.valueOf(role.toUpperCase()), userCreated.getRole());
    }

    @Then("the creation fails with error message {string}")
    public void validateUserExists(String expectedException){
        assertNotNull(exception);
        assertEquals(expectedException, exception.getMessage());
    }
}
