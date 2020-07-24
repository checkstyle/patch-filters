package TreeWalker.blocks.EmptyBlock;

public class Test {
    public void test() {
        {
            try {  // violation context

            }
            finally {
            }
            try {  // violation context

            }
            finally {  // violation context
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
