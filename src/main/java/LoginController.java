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
    private void handleClose() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleLogin() {
        String userName = userNameField.getText() == null ? "" : userNameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (userName.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // loginButton.setDisable(true);
        errorLabel.setText("");
        errorLabel.setStyle("-fx-text-fill: #c62828;");

        UsersApp.dispatchLoginAttempt(userName, password, new UsersApp.LoginAttemptCallbacks() {
            @Override
            public void onWelcome() {
                Platform.runLater(() -> {
                    errorLabel.setText("");

                    // 1. Get the current root container (the VBox)
                    javafx.scene.Parent root = loginButton.getScene().getRoot();

                    // 2. Create a Fade Out transition
                    javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                            javafx.util.Duration.millis(600), root
                    );
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);

                    // 3. When the fade is finished, open the new window
                    fadeOut.setOnFinished(e -> {
                        UsersApp.openWelcomeWindow();
                        // Note: If openWelcomeWindow closes the current stage,
                        // the transition ends cleanly here.
                    });

                    fadeOut.play();
                });
            }

            @Override
            public void onInvalidCredentials() {
                errorLabel.setText("Invalid username or password.");
                errorLabel.setStyle("-fx-text-fill: red;");
                loginButton.setDisable(false);
            }

            @Override
            public void onAccountLocked() {
                int t = UsersApp.getLockDurationSeconds();
                errorLabel.setText("This account is temporarily locked. Try again in up to " + t + " seconds.");
                errorLabel.setStyle("-fx-text-fill: red;");
                loginButton.setDisable(false);
            }

            @Override
            public void onLockoutWithWait() {
                int t = UsersApp.getLockDurationSeconds();
                errorLabel.setText("Too many failed attempts. Locked for " + t + " seconds. Please wait...");
                errorLabel.setStyle("-fx-text-fill: red;");
                //loginButton.setDisable(true);
            }

            @Override
            public void onLockoutEnded() {
                errorLabel.setText("Lockout ended. You may try again (up to " + UsersApp.getMaxFailedAttempts() + " attempts).");
                errorLabel.setStyle("-fx-text-fill: #2e7d32;");
                loginButton.setDisable(false);
            }
        });
    }
}
