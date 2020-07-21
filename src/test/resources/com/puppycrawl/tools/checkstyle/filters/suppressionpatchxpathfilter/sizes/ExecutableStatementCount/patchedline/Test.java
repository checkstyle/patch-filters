package TreeWalker.ExecutableStatementCount;

public class Test {
    public void test() {  // violation context
        while (true) {
            Runnable runnable = new Runnable() {
                public void run() {
                    while (true) {
                    }
                }
            };
            System.out.println();
            new Thread(runnable).start();
        }
    }
}
