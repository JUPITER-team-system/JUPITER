package stepsdefinitions;

import com.management.jupiter.models.Tl;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;
import com.management.jupiter.repository.CoderRepository;
import com.management.jupiter.repository.impl.TeamLeaderRepositoryImpl;
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
    private TeamLeaderRepositoryImpl teamLeaderRepository;
    private Exception assignmentException;
    private ClanRepositoryImpl clanRepositoryImpl;

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

        clanRepositoryImpl = new ClanRepositoryImpl();
        teamLeaderRepository = new TeamLeaderRepositoryImpl(clanRepositoryImpl);
        assignmentService = new AssignmentService(
                new ClanRepositoryImpl(),
                teamLeaderRepository,
                new CoderRepository()
        );
        assignmentException = null;
    }

    @After("@assignment-tl")
    public void restoreCsv() throws IOException {
        Files.writeString(USERS_CSV, originalCsvContent);
    }

    @Given("the TL with id {string} is already assigned to the clan with id {string}")
    public void theTlWithIdIsAlreadyAssignedToTheClanWithId(String tlId, String clanId) {
        assignmentService.clanTls(tlId, clanId);
    }

    @When("I assign the TL with id {string} to the clan with id {string}")
    public void iAssignTheTlWithIdToTheClanWithId(String tlId, String clanId) {
        assignmentException = null;

        try {
            assignmentService.clanTls(tlId, clanId);
        } catch (Exception exception) {
            assignmentException = exception;
            System.out.println(assignmentException);
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
        int actualCount = assignmentService
                .obtenerTlsDeClanPorTipo(
                        "81e3578d-b9e1-440d-b8c7-57495c8cf115",
                        TlType.valueOf(tlType)
                ).size();

        assertEquals(expectedCount, 1);
    }

    @And("the TL with id {string} should be assigned to clan {string}")
    public void theTlWithIdShouldBeAssignedToClan(String tlId, String clanName) {
        Tl tl = teamLeaderRepository.findById(tlId);
        assertNotNull(tl);
        assertEquals(1, tl.getClans().size());
        assertEquals(clanName, tl.getClans().getFirst().getName());
    }
}
