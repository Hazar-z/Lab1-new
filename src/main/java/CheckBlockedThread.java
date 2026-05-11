public class CheckBlockedThread extends Thread {

    private final User user;
    private final int blockTimeSeconds;
    private boolean allowedToLogin;

    public CheckBlockedThread(User user, int blockTimeSeconds) {
        this.user = user;
        this.blockTimeSeconds = blockTimeSeconds;
        this.allowedToLogin = false;

        setName("CheckBlockedThread-" + user.getEmail());
    }

    @Override
    public void run() {

        synchronized (user) {

            if (!user.isBlocked()) {
                allowedToLogin = true;
                return;
            }

            long now = System.currentTimeMillis();

            long passedTime =
                    now - user.getBlockedTime();

            long blockTimeMillis =
                    blockTimeSeconds * 1000L;

            if (passedTime >= blockTimeMillis) {

                user.unlockUser();

                allowedToLogin = true;

            } else {

                try {

                    Thread.sleep(blockTimeMillis - passedTime);

                    user.unlockUser();

                    allowedToLogin = true;

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    allowedToLogin = false;
                }
            }

            System.out.println("Thread name: "
                    + Thread.currentThread().getName());

            System.out.println("Username: "
                    + user.getEmail());

            System.out.println("Blocked: "
                    + user.isBlocked());

            System.out.println("Blocked time: "
                    + user.getBlockedTime());

            System.out.println("Allowed to login: "
                    + allowedToLogin);
        }
    }

    public boolean isAllowedToLogin() {
        return allowedToLogin;
    }
}