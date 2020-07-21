package TreeWalker.AnonInnerLength;

public class Test {
    private Runnable mRunnable1 = new Runnable() {  // violation context
        public void run()
        {
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
            System.identityHashCode("running");
        }
    };
}
