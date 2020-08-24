package TreeWalker.Annotation.MissingDeprecated;

public class Test {
    @Deprecated
    public static final int MY_CONST = 123456;

    /** This javadoc is missing deprecated tag. */
    @Deprecated  // violation without filter
    public static final int COUNTER = 10;
}
