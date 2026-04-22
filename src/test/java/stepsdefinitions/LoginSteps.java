package stepsdefinitions;

//import com.management.jupiter.controllers.UserController;
//import com.management.jupiter.models.User;
//import com.management.jupiter.services.UserServices;
//import com.management.jupiter.util.scanner.ScannerUtil;
//import com.management.jupiter.views.LoginView;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Scanner;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//
//public class LoginSteps {
//
//    private String email;
//    private String password;
//    private User authenticatedUser;
//    private Exception loginException;
//
//    @Given("the user enters the email {string}")
//    public void theUserEntersTheEmail(String email) {
//        this.email = email;
//    }
//
//    @Given("the user enters the password {string}")
//    public void theUserEntersThePassword(String password) {
//        this.password = password;
//    }
//
//    @When("the login is submitted")
//    public void theLoginIsSubmitted() {
//        authenticatedUser = null;
//        loginException = null;
//
//        String simulatedInput = email + "\n" + password + "\n";
//        UserController controller = new UserController();
//        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
//
//        ScannerUtil testInput = new ScannerUtil(new Scanner(in));
//        LoginView loginView = new LoginView(testInput, controller);
//
//        try {
//            authenticatedUser = loginView.login();
//        } catch (Exception exception) {
//            loginException = exception;
//        }
//    }
//
//    @Then("the login should authenticate the user")
//    public void theLoginShouldAuthenticateTheUser() {
//        assertNotNull(authenticatedUser);
//        assertEquals(email, authenticatedUser.getEmail());
//        assertNull(loginException);
//    }
//
//    @Then("the login should be rejected due to invalid credentials")
//    public void theLoginShouldBeRejectedDueToInvalidCredentials() {
//        assertNull(authenticatedUser);
//        assertNull(loginException);
//    }
//
//    @Then("the login should fail with the message {string}")
//    public void theLoginShouldFailWithTheMessage(String expectedMessage) {
//        assertNull(authenticatedUser);
//        assertNotNull(loginException);
//        assertEquals(expectedMessage, loginException.getMessage());
//    }
//}
