package TreeWalker.metrics.BooleanExpressionComplexity;

public class Test {
    public void test(boolean a, boolean b) {
        boolean c = (a & b) | (b ^ a);

        boolean d = (a & b) ^ (a || b) | a;    // violation without filter

        boolean e =    // violation without filter
                (a & b) ^ (a || b) | b;

        boolean f = (a & b) ^ (a || b) | a;    // violation without filter

        boolean g =    // violation without filter
                (a & b) ^ (a || b) | b;
    }
}
