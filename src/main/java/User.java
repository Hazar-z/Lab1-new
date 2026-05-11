public class User implements Comparable<User> {
    private String username;
    private String password;

    private int failedAttempts;
    private boolean blocked;
    private long blockedTime;

    public User(String username, String password) {
        validateUsername(username);
        validatePassword(password);

        this.username = username;
        this.password = password;

        this.failedAttempts = 0;
        this.blocked = false;
        this.blockedTime = 0;
    }

    public String getUsername() {
        return username;
    }

    // optional, useful if your other code uses getEmail()
    public String getEmail() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public synchronized int getFailedAttempts() {
        return failedAttempts;
    }

    public synchronized void increaseFailedAttempts() {
        failedAttempts++;
    }

    public synchronized void resetFailedAttempts() {
        failedAttempts = 0;
    }

    public synchronized boolean isBlocked() {
        return blocked;
    }

    public synchronized long getBlockedTime() {
        return blockedTime;
    }

    public synchronized void blockUser() {
        blocked = true;
        blockedTime = System.currentTimeMillis();
    }

    public synchronized void unlockUser() {
        blocked = false;
        blockedTime = 0;
        failedAttempts = 0;
    }

    private void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Please enter a valid Email as username");
        }

        if (username.length() > 50) {
            throw new IllegalArgumentException("Username is too long, try something shorter");
        }

        if (!isValidEmail(username)) {
            throw new IllegalArgumentException("Please enter a valid Email as username");
        }
    }

    private void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Pleas enter a valid password.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("Your password is too short, add more characters");
        }

        if (password.length() > 12) {
            throw new IllegalArgumentException("Your password is too long, try a shorter one");
        }

        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Pleas enter a valid password.");
        }
    }

    private boolean isValidEmail(String email) {
        int atIndex = email.indexOf('@');
        int lastDotIndex = email.lastIndexOf('.');

        if (atIndex <= 0 || lastDotIndex <= atIndex + 1 || lastDotIndex == email.length() - 1) {
            return false;
        }

        String part1 = email.substring(0, atIndex);
        String part2 = email.substring(atIndex + 1, lastDotIndex);
        String part3 = email.substring(lastDotIndex + 1);

        if (part1.isEmpty() || part2.isEmpty() || part3.isEmpty()) {
            return false;
        }

        for (int i = 0; i < part1.length(); i++) {
            char ch = part1.charAt(i);
            if (!(Character.isLetterOrDigit(ch) || ch == '.' || ch == '_' || ch == '-' || ch == '+' || ch == '%')) {
                return false;
            }
        }

        if (!Character.isLetterOrDigit(part2.charAt(0))) {
            return false;
        }

        for (int i = 0; i < part2.length(); i++) {
            char ch = part2.charAt(i);
            if (!(Character.isLetterOrDigit(ch) || ch == '.' || ch == '-')) {
                return false;
            }
        }

        if (part3.length() < 2) {
            return false;
        }

        for (int i = 0; i < part3.length(); i++) {
            if (!Character.isLetter(part3.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);

            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (ch == '#' || ch == '@' || ch == '!' || ch == '+' || ch == '=' ||
                    ch == '$' || ch == '%' || ch == '^' || ch == '&' || ch == '*' ||
                    ch == '(' || ch == ')' || ch == '-' || ch == '_') {
                hasSymbol = true;
            } else {
                return false;
            }
        }

        return hasLetter && hasDigit && hasSymbol;
    }

    @Override
    public int compareTo(User other) {
        return this.username.compareTo(other.username);
    }

    @Override
    public String toString() {
        return username + " " + password;
    }
}