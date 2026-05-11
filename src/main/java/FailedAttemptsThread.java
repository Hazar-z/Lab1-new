public class FailedAttemptsThread extends Thread {

    private final User user;
    private final int maxAttempts;

    public FailedAttemptsThread(User user, int maxAttempts) {
        this.user = user;
        this.maxAttempts = maxAttempts;
        setName("FailedAttemptsThread-" + user.getUsername());
    }

    @Override
    public void run() {
        synchronized (user) {
            user.increaseFailedAttempts();

            if (user.getFailedAttempts() >= maxAttempts) {
                user.blockUser();
            }

            System.out.println("Thread name: " + Thread.currentThread().getName());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Failed attempts: " + user.getFailedAttempts());
            System.out.println("Max attempts: " + maxAttempts);
            System.out.println("Blocked: " + user.isBlocked());
            System.out.println("Blocked time: " + user.getBlockedTime());
        }
    }
}