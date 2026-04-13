Feature: Delete user
  As the administrator, I want to delete users to manage the user in the application.

  @delete-user
  Scenario Outline: Delete user successfully
    Given an administrator is authenticated
    And a valid id or email "<value>"
    When the administrator submits the user deletion request
    Then the user should be deleted successfully
    Examples:
      | value              |
      | 1                  |
      | emmanuel@gmail.com |