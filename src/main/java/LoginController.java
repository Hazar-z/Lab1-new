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
                errorLabel.setText("");
                UsersApp.openWelcomeWindow();
                loginButton.setDisable(false);
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
