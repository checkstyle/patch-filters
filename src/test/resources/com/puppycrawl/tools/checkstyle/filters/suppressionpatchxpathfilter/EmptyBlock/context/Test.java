package TreeWalker.blocks.EmptyBlock;

public class Test {
    public void test() {
        {
            try {  // violation

            }
            finally {  // violation without filter
            }
            try {  // violation

            }
            finally {  // violation
            }
            try {
                ; // something
            }
            finally {
                System.out.println();
                ; // statement
            }
        }
    }
}
