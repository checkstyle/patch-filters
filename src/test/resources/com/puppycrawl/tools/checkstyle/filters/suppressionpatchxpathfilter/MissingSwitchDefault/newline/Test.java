package TreeWalker.coding.MissingSwitchDefault;

public class Test {
    public void test(int i) {
        switch (i)  {  // violation without filter
            case 1:
                break;
            case 2:
                break;
        }
    }
}

class Test1 {
    public void test1(int i) {
        switch (i) {  // violation without filter
            case 3:
                break;
            case 4:
                break;
        }
    }
}
