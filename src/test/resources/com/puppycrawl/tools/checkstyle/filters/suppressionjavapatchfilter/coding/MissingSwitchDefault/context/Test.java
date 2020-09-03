package TreeWalker.coding.MissingSwitchDefault;

public class Test {
    public void test(int i) {
        switch (i) {  // violation without filter
            case 1:
                break;
            case 2:
                break;
        }
    }
}
