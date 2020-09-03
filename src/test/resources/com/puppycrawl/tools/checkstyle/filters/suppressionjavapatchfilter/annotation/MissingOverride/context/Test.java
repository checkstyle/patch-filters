package TreeWalker.Annotation.MissingOverride;

public class Test {

    /** {@inheritDoc} */
    public void test1() {}  // violation without filter

    /** {@inheritDoc} */
    public void test2() {}  // violation without filter

    /** {@inheritDoc} */
    private void test3() {}  // violation without filter

}
