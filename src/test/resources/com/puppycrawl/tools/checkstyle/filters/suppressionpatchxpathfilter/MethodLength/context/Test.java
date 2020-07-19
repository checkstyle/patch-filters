public class Test {
    private String name;

    public void test() { // violation context, should be suppressed after patching
        int a = 123;
        int b = 234;
    }
}
