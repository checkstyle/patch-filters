package TreeWalker.AvoidNestedBlocks;

public class Test {
    public void fun(boolean valid) {
        {  // violation patchedline
            if (valid) {
                {  // violation patchedline
                    System.out.println("ok");
                }
            }
        }
    }
}
