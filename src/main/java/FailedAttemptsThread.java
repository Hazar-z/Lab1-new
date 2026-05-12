import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FailedAttemptsThread extends Thread {
    private final User user;
    private final int maxAttempts;
    private final int lockDurationSeconds; // Added to track duration for printing

    public FailedAttemptsThread(User user, int maxAttempts, int lockDurationSeconds) {
        this.user = user;
        this.maxAttempts = maxAttempts;
        this.lockDurationSeconds = lockDurationSeconds;
        setName("FailedAttemptsThread-" + user.getUsername());
    }

    @Override
    public void run() {
        synchronized (user) {
            user.increaseFailedAttempts();

            if (user.getFailedAttempts() >= maxAttempts) {
                user.blockUser();
            }

            // Printing the requested information to the console
            System.out.println("\n--- Login Attempt Details ---");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Attempt Number: " + user.getFailedAttempts() + " / " + maxAttempts);

            // System.out.println("Thread name: " + Thread.currentThread().getName());
            //  System.out.println("Username: " + user.getUsername());
            //   System.out.println("Failed attempts: " + user.getFailedAttempts());
            //  System.out.println("Max attempts: " + maxAttempts);
            //   System.out.println("Blocked: " + user.isBlocked());
            //   System.out.println("Blocked time: " + user.getBlockedTime());

            if (user.isBlocked()) {
                // Requirement 3: Print the time of locking
                java.time.LocalDateTime lockTime = java.time.LocalDateTime.now();
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

                System.out.println("STATUS: USER LOCKED");
                System.out.println("Lockout Hour: " + lockTime.format(formatter));
            }
            System.out.println("-------------------------");
        }
    }
}
