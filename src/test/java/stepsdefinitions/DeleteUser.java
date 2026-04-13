package stepsdefinitions;

import com.management.jupiter.services.UserServices;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteUser {
    private String originalCsvContent;
    private String idOrEmail;


    private static final Path USERS_CSV =
            Path.of("src/main/java/com/management/jupiter/persistance/users.csv");

    @Before("@delete-user")
    public void saveData() throws IOException {
        originalCsvContent = Files.readString(USERS_CSV);
    }
    @After("@delete-user")
    public void restoreCsv() throws IOException {
        Files.writeString(USERS_CSV, originalCsvContent);
    }

    @Given("an administrator is authenticated")
    public void anAdministratorIsAuthenticated() throws Exception {
        UserServices.LoginService("juan@gmail.com", "12345");
    }

    @Given("a valid id or email {string}")
    public void getEmailOrIdValid(String idOrEmail){
        this.idOrEmail = idOrEmail;
    }

    @When("the administrator submits the user deletion request")
    public void submitDeleteRequest(){

    }
}
