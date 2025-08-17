import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Test the existing hash from database
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYrONgrkHuq6Zu";
        String password = "admin123";
        
        System.out.println("Testing password: " + password);
        System.out.println("Against hash: " + existingHash);
        System.out.println("Match result: " + encoder.matches(password, existingHash));
        
        // Generate a new hash for comparison
        String newHash = encoder.encode(password);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
        
        // Test other passwords
        System.out.println("\nTesting other passwords:");
        String[] testPasswords = {"admin123", "Admin123", "ADMIN123", "admin", "123", ""};
        for (String testPwd : testPasswords) {
            System.out.println("'" + testPwd + "' matches: " + encoder.matches(testPwd, existingHash));
        }
    }
}
