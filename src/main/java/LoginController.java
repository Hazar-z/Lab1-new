import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;

public class LoginController {
    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        if (userNameField != null) {
            userNameField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (errorLabel != null) {
                    errorLabel.setText("");
                }
            });
        }
    }

    @FXML
    private void handleClose() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleLogin() {
        String userName = userNameField.getText() == null ? "" : userNameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (userName.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Invalid Username or Password.");
            errorLabel.setStyle("-fx-text-fill: #f7f7f7;");
            return;
        }

        errorLabel.setText("");
        errorLabel.setStyle("-fx-text-fill: #f7f7f7;");

        UsersApp.dispatchLoginAttempt(userName, password, new UsersApp.LoginAttemptCallbacks() {
            @Override
            public void onWelcome() {
                Platform.runLater(() -> {
                    errorLabel.setText("");
                    javafx.scene.Parent root = loginButton.getScene().getRoot();

                    javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                            javafx.util.Duration.millis(600), root
                    );
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);

                    fadeOut.setOnFinished(e -> {
                        UsersApp.openWelcomeWindow();
                    });

                    fadeOut.play();
                });
            }

            @Override
            public void onInvalidCredentials(int attemptsMade, int maxAttempts) {
                Platform.runLater(() -> {
                    int remaining = maxAttempts - attemptsMade;
                    errorLabel.setText("Invalid Login. " + remaining + " attempts remaining.");
                    loginButton.setDisable(false);
                });
            }

            @Override
            public void onLockoutWithWait(int secondsToWait) {
                Platform.runLater(() -> {
                    // This satisfies the requirement to show the wait time on screen
                    errorLabel.setText("Locked! Please wait " + secondsToWait + " seconds.");
                    // Requirement: Prevent thread spamming (handled in UsersApp logic)
                    // Use UsersApp.getLockDurationSeconds() to show the actual time
                    //errorLabel.setText("Account Locked. Please wait " + UsersApp.getLockDurationSeconds() + " seconds.");
                    // Requirement: Keep button enabled for other users
                    loginButton.setDisable(false);
                });
            }

            @Override
            public void onAccountLocked(int secondsRemaining) {
                Platform.runLater(() -> {
                    errorLabel.setText("This account is currently locked.");
                });
            }

            @Override
            public void onLockoutEnded() {
                // Requirement 1: Show message when thread finishes its silent sleep
                Platform.runLater(() -> {
                    errorLabel.setText("Lockout Ended. You may try again.");
                    loginButton.setDisable(false);
                });
            }
        });
    }
}