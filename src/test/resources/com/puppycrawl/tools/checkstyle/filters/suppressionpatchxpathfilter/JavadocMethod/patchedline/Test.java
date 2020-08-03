package TreeWalker.JavadocMethod;

public class Test {
    /**
     * test
     * @param arg1 text
     * @param arg2 text  // violation without filter
     * @return text
     */
    public int test1(Integer arg1) {
        return 1;
    }







    /**
     * test
     * @param arg1 text
     * @return text
     */
    public int test2(Integer arg1, Integer arg2) {  // violation without filter
        return 1;
    }
}
