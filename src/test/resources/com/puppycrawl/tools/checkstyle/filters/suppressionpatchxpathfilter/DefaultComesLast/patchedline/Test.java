package TreeWalker.coding.DefaultComesLast;

public class Test {
    public void test(int i) {
        switch (i) {
            case 1:
                break;
            case 2:
                break;
            default :  // violation without filter
                break;
            case 3:
                break;
        }
    }
}

class Test1 {
    public void test1(int i) {
        switch (i) {
            case 1:
                break;
            default:  // violation without filter
                break;
            case 2:
                break;
        }
    }
}
