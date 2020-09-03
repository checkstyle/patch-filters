package TreeWalker.coding.HiddenField;

public class Test {
    private int test;

    private void addTest(int test)  // violation without filter
    {
        System.out.println(test);
    }

    private void foo()
    {
        final int test = 1;  // violation without filter
    }
}
