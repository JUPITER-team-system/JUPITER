package stepsdefinitions;

import com.management.jupiter.models.Tl;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.ClanRepository;
import com.management.jupiter.repository.CoderRepository;
import com.management.jupiter.repository.TeamLeaderRepository;
import com.management.jupiter.services.AssignmentService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AssignmentTlSteps {

    private static final Path USERS_CSV =
            Path.of("data/users.csv");

    private String originalCsvContent;
    private AssignmentService assignmentService;
    private TeamLeaderRepository teamLeaderRepository;
    private Exception assignmentException;

    @Before("@assignment-tl")
    public void setUpScenarioData() throws IOException {
        originalCsvContent = Files.readString(USERS_CSV);
        Files.writeString(USERS_CSV, """
                1,Prog One,prog1@gmail.com,12345,TL,,PROGRAMACION
                2,Prog Two,prog2@gmail.com,12345,TL,,PROGRAMACION
                3,English One,eng1@gmail.com,12345,TL,,INGLES
                4,English Two,eng2@gmail.com,12345,TL,,INGLES
                5,English Three,eng3@gmail.com,12345,TL,,INGLES
                """);

        teamLeaderRepository = new TeamLeaderRepository();
        assignmentService = new AssignmentService(
                new ClanRepository(),
                teamLeaderRepository,
                new CoderRepository()
        );
        assignmentException = null;
    }

    @After("@assignment-tl")
    public void restoreCsv() throws IOException {
        Files.writeString(USERS_CSV, originalCsvContent);
    }

    @Given("the TL with id {int} is already assigned to the clan with id {int}")
    public void theTlWithIdIsAlreadyAssignedToTheClanWithId(int tlId, int clanId) {
        assignmentService.asignarTlAClan(String.valueOf(tlId), String.valueOf(clanId));
    }

    @When("I assign the TL with id {int} to the clan with id {int}")
    public void iAssignTheTlWithIdToTheClanWithId(int tlId, int clanId) {
        assignmentException = null;

        try {
            assignmentService.asignarTlAClan(String.valueOf(tlId), String.valueOf(clanId));
        } catch (Exception exception) {
            assignmentException = exception;
        }
    }

    @Then("the assignment should be successful")
    public void theAssignmentShouldBeSuccessful() {
        assertNull(assignmentException);
    }

    @Then("the assignment should fail with the message {string}")
    public void theAssignmentShouldFailWithTheMessage(String expectedMessage) {
        assertNotNull(assignmentException);
        assertEquals(expectedMessage, assignmentException.getMessage());
    }

    @And("the clan should have {int} TLs of type {string}")
    public void theClanShouldHaveTlsOfType(int expectedCount, String tlType) {
        int actualCount = assignmentService.obtenerTlsDeClanPorTipo("1", TlType.valueOf(tlType)).size();
        assertEquals(expectedCount, actualCount);
    }

    @And("the TL with id {int} should be assigned to clan {string}")
    public void theTlWithIdShouldBeAssignedToClan(int tlId, String clanName) {
        Tl tl = teamLeaderRepository.findById(String.valueOf(tlId));
        assertNotNull(tl);
        assertEquals(1, tl.getClans().size());
        assertEquals(clanName, tl.getClans().getFirst().getName());
    }
}
