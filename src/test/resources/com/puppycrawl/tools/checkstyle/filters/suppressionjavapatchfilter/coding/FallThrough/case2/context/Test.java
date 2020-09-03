package TreeWalker.coding.FallThrough;

public class Test {
    public void test(int i) {
        switch (i) {
            case 6:
                i++;
            case 7:  // violation without filter
                i++;
        }

        switch (i) {
            case 8:
                i++;
            case 9:  // violation without filter
                i++;
                System.out.println();
        }

        switch (i) {
            case 10:
                i++;
            case 11:  // violation without filter
                i++;
                System.out.println();
        }

        switch (i) {
            case 0:
                i++;
                break;
            case 1:
                i++;
            case 2:  // violation without filter
        }
    }
}
