public class User implements Comparable<User> {
    private String username;
    private String password;

    public User(String username, String password) {
        validateUsername(username); //בדיקת תקינות שם המשתמש
        validatePassword(password); //בדיקת תקינות הסיסמה
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private void validateUsername(String username) {
        if (username == null || username.isEmpty()) {//בדיקת אם השם NULL או ריק
            throw new IllegalArgumentException("Please enter a valid Email as username");
        }

        if (username.length() > 50) {//בדיקת שהאורך לא גדול מדי (יותר מ 50 תווים)
            throw new IllegalArgumentException("Username is too long, try something shorter");
        }

        if (!isValidEmail(username)) {//בדיקת תקינות האימייל
            throw new IllegalArgumentException("Please enter a valid Email as username");
        }
    }

    private void validatePassword(String password) {
        if (password == null) {//בדיקת אם הסיסמה NULL
            throw new IllegalArgumentException("Pleas enter a valid password.");
        }

        if (password.length() < 8) {//בדיקת שהסיסמה לא קצרה מדי (פחות מ 8 תווים)
            throw new IllegalArgumentException("Your password is too short, add more characters");
        }

        if (password.length() > 12) {//בדיקת שהסיסמה לא ארוכה מדי (יותר מ 12 תווים)
            throw new IllegalArgumentException("Your password is too long, try a shorter one");
        }

        if (!isValidPassword(password)) {//בדיקת תקינות  תוכן הסיסמה
            throw new IllegalArgumentException("Pleas enter a valid password.");
        }
    }

    private boolean isValidEmail(String email) {
        int atIndex = email.indexOf('@');//בדיקת מיקום התו @
        int lastDotIndex = email.lastIndexOf('.');//בדיקת מיקום התו . האחרון

        if (atIndex <= 0 || lastDotIndex <= atIndex + 1 || lastDotIndex == email.length() - 1) {
            return false;//בדיקת מיקום התווים @ ו . והאם הם נמצאים במקומות תקינים
        }

        String part1 = email.substring(0, atIndex);
        String part2 = email.substring(atIndex + 1, lastDotIndex);
        String part3 = email.substring(lastDotIndex + 1);

        if (part1.isEmpty() || part2.isEmpty() || part3.isEmpty()) {
            return false;//בדיקת שהחלקים של האימייל לא ריקים
        }

        for (int i = 0; i < part1.length(); i++) {
            char ch = part1.charAt(i);
            if (!(Character.isLetterOrDigit(ch) || ch == '.' || ch == '_' || ch == '-' || ch == '+' || ch == '%')) {
                return false;//בדיקת תקינות התווים בחלק הראשון של האימייל
            }
        }

        if (!Character.isLetterOrDigit(part2.charAt(0))) {
            return false;
        }

        for (int i = 0; i < part2.length(); i++) {
            char ch = part2.charAt(i);
            if (!(Character.isLetterOrDigit(ch) || ch == '.' || ch == '-')) {
                return false;//בדיקת תקינות התווים בחלק השני של האימייל
            }
        }

        if (part3.length() < 2) {
            return false;//לוודות שהסיומת מינמום 2 תווים
        }

        for (int i = 0; i < part3.length(); i++) {
            if (!Character.isLetter(part3.charAt(i))) {
                return false;//לוודות שהסיומת מכילה רק אותיות
            }
        }

        return true;//אם כל הבדיקות עברו בהצלחה, האימייל תקין
    }

    private boolean isValidPassword(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);

            if (Character.isLetter(ch)) {
                hasLetter = true;//בדיקת אם יש לפחות אות אחת
            } else if (Character.isDigit(ch)) {
                hasDigit = true;//בדיקת אם יש לפחות ספרה אחת
            } else if (ch == '#' || ch == '@' || ch == '!' || ch == '+'||  ch == '='|| ch == '$' || ch == '%' || ch == '^' || ch == '&' || ch == '*' || ch == '(' || ch == ')' || ch == '-' || ch == '_' ) {
                hasSymbol = true;//בדיקת אם יש לפחות סימן אחד
            } else {
                return false; // any other character is not allowed
            }
        }

        return hasLetter && hasDigit && hasSymbol;//password is valid if it has at least one letter, one digit, and one symbol
    }


    @Override
    public int compareTo(User other) {
        // השוואה בין משתמשים לפי שם המשתמש (לפי סדר אלפביתי)
        // משמש למיון רשימת משתמשים באמצעות Collections.sort

        return this.username.compareTo(other.username);
    }

    @Override
    public String toString() {
        // קובע איך האובייקט יודפס כאשר משתמשים ב- System.out.println
        // מחזיר מחרוזת שמכילה את שם המשתמש והסיסמה

        return username + " " + password;
    }
}