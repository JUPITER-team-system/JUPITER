@assignment-tl
Feature: Assign TL to clan
  The assignment service must enforce the maximum number of TLs by type per clan.

  Scenario: Assign one programming TL to a clan
    When I assign the TL with id 1 to the clan with id 1
    Then the assignment should be successful
    And the clan should have 1 TLs of type "PROGRAMACION"
    And the TL with id 1 should be assigned to clan "HAMILTON"

  Scenario: Reject a second programming TL in the same clan
    Given the TL with id 1 is already assigned to the clan with id 1
    When I assign the TL with id 2 to the clan with id 1
    Then the assignment should fail with the message "El clan 'HAMILTON' ya alcanzó el límite de 1 TL(s) de tipo PROGRAMACION."
    And the clan should have 1 TLs of type "PROGRAMACION"

  Scenario: Allow up to two English TLs in the same clan
    When I assign the TL with id 3 to the clan with id 1
    And I assign the TL with id 4 to the clan with id 1
    Then the assignment should be successful
    And the clan should have 2 TLs of type "INGLES"
    And the TL with id 3 should be assigned to clan "HAMILTON"
    And the TL with id 4 should be assigned to clan "HAMILTON"

  Scenario: Reject a third English TL in the same clan
    Given the TL with id 3 is already assigned to the clan with id 1
    And the TL with id 4 is already assigned to the clan with id 1
    When I assign the TL with id 5 to the clan with id 1
    Then the assignment should fail with the message "El clan 'HAMILTON' ya alcanzó el límite de 2 TL(s) de tipo INGLES."
    And the clan should have 2 TLs of type "INGLES"
