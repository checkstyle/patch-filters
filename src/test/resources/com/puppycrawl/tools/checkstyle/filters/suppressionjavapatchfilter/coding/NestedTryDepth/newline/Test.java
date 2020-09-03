package TreeWalker.coding.NestedTryDepth;

public class Test {
    public void test() {
        try {
            try {
                try {  // violation without filter

                } catch (Exception e) {}
            } catch (Exception e) {}
        } catch (Exception e) {}

    }

    public void test1() {
        try {
            try {
                try {  // violation without filter
                    try {  // violation without filter

                    } catch (Exception e) {}
                } catch (Exception e) {}
            } catch (Exception e) {}
        } catch (Exception e) {}

    }

    public void test2() {
        try {
            try {
                try {  // violation without filter
                } catch (Exception e) {}
            } catch (Exception e) {}
        } catch (Exception e) {}

    }

    public void test3() {
        try {
            try {
                try {  // violation without filter
                } catch (Exception e) { }
            } catch (Exception e) {}
        } catch (Exception e) {}

    }

    public void test4() {
        try {
            try {
                try {  // violation without filter
                } catch (Exception e) { }
            } catch (Exception e) {}
        } catch (Exception e) {}

    }
}
