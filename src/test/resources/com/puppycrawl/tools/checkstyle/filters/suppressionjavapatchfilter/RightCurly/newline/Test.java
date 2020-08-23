package TreeWalker.blocks.RightCurly;

public class Test2 {
    public void test(int x) {
        if (x > 0)
        {
            System.out.println(x);
        }  // violation without filter
        else if (x < 0)
        {
            ;
        }
    }
}
