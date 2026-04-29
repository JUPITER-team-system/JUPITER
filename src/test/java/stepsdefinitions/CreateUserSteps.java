package stepsdefinitions;

import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Clan;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.interfaces.UserRepository;
import com.management.jupiter.services.AdminService;
import com.management.jupiter.services.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateUserSteps {
    private UserService userService;
    private UserRepository userRepository;
    private AdminService adminService;

    private String username;
    private String email;
    private String password;
    private Role role;
    private Clan clan;
    private TlType tlType;

    private User createdUser;
    private Exception creationException;

    @Before
    public void setUp() {
        userService = mock(UserService.class);
        userRepository = mock(UserRepository.class);
        adminService = new AdminService(userService, userRepository);

        username = null;
        email = null;
        password = null;
        role = null;
        clan = null;
        tlType = null;
        createdUser = null;
        creationException = null;
    }

    @Given("an administrator is authenticated")
    public void anAdministratorIsAuthenticated() {
        // Authentication is covered by login.feature. This scenario starts from an authorized admin action.
    }

    @Given("a valid username {string}")
    public void aValidUsername(String username) {
        this.username = username;
    }

    @Given("a valid email {string}")
    public void aValidEmail(String email) throws Exception {
        this.email = email;
        when(userService.emailExists(email)).thenReturn(false);
    }

    @Given("a valid password {string}")
    public void aValidPassword(String password) {
        this.password = password;
    }

    @Given("the role {string}")
    public void theRole(String role) {
        this.role = Role.valueOf(role.toUpperCase());
    }

    @Given("the target clan is {string}")
    public void theTargetClanIs(String clan) {
        this.clan = "NONE".equalsIgnoreCase(clan) ? null : Clan.valueOf(clan.toUpperCase());
    }

    @Given("the TL type is {string}")
    public void theTlTypeIs(String tlType) {
        this.tlType = "NONE".equalsIgnoreCase(tlType) ? null : TlType.valueOf(tlType.toUpperCase());
    }

    @Given("the email {string} already exists")
    public void theEmailAlreadyExists(String email) throws Exception {
        when(userService.emailExists(email)).thenReturn(true);
    }

    @When("the administrator submits the user creation request")
    public void theAdministratorSubmitsTheUserCreationRequest() {
        try {
            createdUser = adminService.createUser(username, email, password, role, clan, tlType);
        } catch (Exception exception) {
            creationException = exception;
        }
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        assertNotNull(createdUser);
        assertNull(creationException);
        assertEquals(email, createdUser.getEmail());
        assertEquals(username, createdUser.getUsername());
    }

    @Then("the user should have the role {string}")
    public void theUserShouldHaveTheRole(String expectedRole) {
        assertNotNull(createdUser);
        assertEquals(Role.valueOf(expectedRole.toUpperCase()), createdUser.getRole());
    }

    @Then("the creation fails with error message {string}")
    public void theCreationFailsWithErrorMessage(String expectedMessage) {
        assertNull(createdUser);
        assertNotNull(creationException);
        assertEquals(expectedMessage, creationException.getMessage());
    }

    @Then("the user repository should save the created user")
    public void theUserRepositoryShouldSaveTheCreatedUser() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());
        assertEquals(createdUser, userCaptor.getValue());
    }

    @Then("the user repository should not save any user")
    public void theUserRepositoryShouldNotSaveAnyUser() {
        verify(userRepository, never()).save(any(User.class));
    }
}
