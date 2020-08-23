package TreeWalker.coding.EmptyStatement;

public class Test {
    public void test(int i) {
        switch (i)
        {
            case 1 :
                ;  // violation without filter
            default :
                ;
        }

        switch (i)
        {
            case 1 :
                ;  // violation without filter
            default :
                ;  // violation without filter
        }
    }
}
