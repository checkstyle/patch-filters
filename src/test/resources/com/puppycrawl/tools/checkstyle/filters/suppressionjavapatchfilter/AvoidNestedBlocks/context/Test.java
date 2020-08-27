package TreeWalker.AvoidNestedBlocks;

public class Test {
    public void test() {
        int x = 0;

        {  // violation without filter
            int z = 1;
            int y = z;
            int h = z;
            h = 0;
        }

        {  // violation without filter
            x = 2;
        }

//        if (x == 1)
        {  // violation without filter
            x = 2;
        }

        switch (x) {
            case 0:
                // OK
                x = 3;
                break;
            case 1:
                {  // violation without filter
                    x = 1;
                }
                break;
            case 2:
                {  // violation without filter
                    x = 1;
                    break;
                }
            case 3: // test fallthrough
            default:
                System.identityHashCode("Hello");
                {  // violation without filter
                    x = 2;
                }
        }
    }

    public void test2() {
        {  // violation without filter
            System.out.println("Hello");
        }
    }
}
