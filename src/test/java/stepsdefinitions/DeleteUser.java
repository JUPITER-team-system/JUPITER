package stepsdefinitions;

import com.management.jupiter.models.User;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class DeleteUser {
    private String originalCsvContent;
    private String idOrEmail;


    private static final Path USERS_CSV =
            Path.of("data/users.csv");

    @Before("@delete-user")
    public void saveData() throws IOException {
        originalCsvContent = Files.readString(USERS_CSV);
    }
    @After("@delete-user")
    public void restoreCsv() throws IOException {
        Files.writeString(USERS_CSV, originalCsvContent);
    }

    @Given("an administrator is authenticate")
    public void anAdministratorIsAuthenticate() throws Exception {
        UserServices.LoginService("juan@gmail.com", "12345");
    }

    @Given("a valid id or email {string}")
    public void getEmailOrIdValid(String idOrEmail){
        this.idOrEmail = idOrEmail;
    }

    @When("the administrator submits the user deletion request")
    public void submitDeleteRequest(){
        AdminService.deleteUser(idOrEmail);
    }

    @Then("the user should be deleted successfully")
    public void userDeleted(){
        AdminRepositoryImpl userRepository = new AdminRepositoryImpl();
        Optional<User> user = userRepository.findByEmail(idOrEmail);
        assertNull(user.orElse(null));
    }
}
