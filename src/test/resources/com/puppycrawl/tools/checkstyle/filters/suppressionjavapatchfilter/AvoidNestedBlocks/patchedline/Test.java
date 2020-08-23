package TreeWalker.AvoidNestedBlocks;

public class Test {
    public void fun(boolean valid) {
        {  // violation without filter
            if (valid) {
                {  // violation without filter
                    System.out.println("ok");
                }
            }
        }
    }
}
