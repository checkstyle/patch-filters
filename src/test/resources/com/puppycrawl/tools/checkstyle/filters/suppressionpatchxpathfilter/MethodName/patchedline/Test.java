package TreeWalker.MethodName;

public class Test {
    public void MyMethod(int a, int b) { }  // violation newline/patchedline
    public void MyMethod(int a) { }
    public void MyMethod(String a) { }  // violation newline/patchedline
}
