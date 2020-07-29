package TreeWalker.coding.UnnecessarySemicolonInEnumeration;

public class Test {
    enum One {
        A,B,;  // violation without filter
    }
    enum Two {
        A,;  // violation without filter
    }
    enum Three {
        A,B();  // violation without filter
    }
    enum Four {
        A,B{};  // violation without filter
    }
    enum Five {
        A,
        B
        ;  // violation without filter
    }

    enum six {
        A,
        B,
        ;  // violation without filter
    }
    enum seven {
        A, B
    }
}
