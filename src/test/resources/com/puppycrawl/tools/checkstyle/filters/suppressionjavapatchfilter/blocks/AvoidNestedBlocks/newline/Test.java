package TreeWalker.blocks.AvoidNestedBlocks;

public class Test {
    public void fun(boolean valid) {
        {  // violation without filter
            System.out.println(valid);
        }
    }
}
