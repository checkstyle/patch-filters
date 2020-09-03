package TreeWalker.blocks.EmptyBlock;

public class Test {
    public void test() {
        {
            try {  // violation without filter

            }
            finally {  // violation without filter
            }
            try {  // violation without filter
                // something
            }
            finally  {  // violation without filter
                // something
            }
            try {
                ; // something
            }
            finally {
                ; // statement
            }
            try {  // violation

            }
            finally {  // violation
            }
        }
    }
}
