package TreeWalker.ReturnCount;

public class Test {
    public void test(int error) {
        if (error > 100) {
            return;
        } else if (error > 50) {
            return;
        } else if (error > 40) {
            System.out.println("ok");
        } else if (error > 30) {
            return;
        } else {
            System.out.println("pass");
        }
    }
}
