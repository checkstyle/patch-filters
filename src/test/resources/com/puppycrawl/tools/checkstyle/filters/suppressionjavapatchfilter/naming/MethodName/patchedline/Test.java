package TreeWalker.MethodName;

public class Test {
    public void MyMethod(int a, int b) { }  // violation without filter
    public void MyMethod(int a) { }
    public void MyMethod(String a) { }  // violation without filter
}
