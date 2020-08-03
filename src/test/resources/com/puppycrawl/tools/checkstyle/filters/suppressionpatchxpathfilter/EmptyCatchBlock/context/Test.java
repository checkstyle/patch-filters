public class Test {
    public static void main(String[] args) {
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) { // violation without filter
        }
    }
}
