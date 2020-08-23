package TreeWalker.coding.UnnecessarySemicolonInEnumeration;

public class Test {
    enum One {
        A,B,;  // violation without filter
    }
    enum Two {
        A,;  // violation without filter
    }
    enum Three {
        A,B();
    }
    enum Four {
        A,B,;  // violation without filter
    }
}
