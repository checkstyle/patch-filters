package TreeWalker.blocks.EmptyBlock;

public class Test {
    public void test() {
        {
            try {  // violation without filter

            }
            finally {  // violation without filter
            }
            try {  // violation without filter

            }
            finally {  // violation without filter
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
