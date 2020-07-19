package TreeWalker.AvoidNestedBlocks;

public class Test {
    public void fun(boolean valid) {
        {
            if (valid) {
                {
                    System.out.println("ok");
                }
            }
        }
    }
}
