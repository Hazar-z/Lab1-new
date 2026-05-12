public class CheckBlockedThread extends Thread {

    private final User user;
    private final int blockTimeSeconds;
    private final UsersApp.LoginAttemptCallbacks callbacks;

    public CheckBlockedThread(User user, int blockTimeSeconds, UsersApp.LoginAttemptCallbacks callbacks) {
        this.user = user;
        this.blockTimeSeconds = blockTimeSeconds;
        this.callbacks = callbacks;
        setName("CheckBlockedThread-" + user.getEmail());
    }

    @Override
    public void run() {
        try {
            long remainingMillis;

            synchronized (user) {
                long now = System.currentTimeMillis();
                long passedTime = now - user.getBlockedTime();
                long blockTimeMillis = blockTimeSeconds * 1000L;
                remainingMillis = blockTimeMillis - passedTime;
            }

            // Requirement 1 & 3: Wait silently for the full duration
            if (remainingMillis > 0) {
                Thread.sleep(remainingMillis);
            }

            synchronized (user) {
                user.unlockUser();
            }

            // Requirement 1: Inform the UI that the lockout is over
            javafx.application.Platform.runLater(callbacks::onLockoutEnded);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            synchronized (user) {
                user.setTimerRunning(false);
            }
        }
    }
} // This is the end of the class