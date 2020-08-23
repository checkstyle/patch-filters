package TreeWalker.DescendantToken;

public class Test {
    public void test(Integer i) {
        if (i == null) {  // violation without filter
            System.out.println();
        }

        switch (i)  // violation without filter
        {
            default:
                System.out.println();
                break;
        }

    }

    public void test1(Object i) {
        if (i == null) {  // violation without filter
            System.out.println();
        }
        if (i == null) {  // violation without filter
            System.out.println();
        }
    }
}
