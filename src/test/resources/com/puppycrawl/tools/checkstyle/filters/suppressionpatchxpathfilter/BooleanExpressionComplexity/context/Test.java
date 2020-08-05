package TreeWalker.metrics.BooleanExpressionComplexity;

public class Test {
    public void test(boolean a, boolean b) {
        boolean c = (a & b) | (b ^ a);

        boolean d = (a & b) ^ (a || b) | a;

        boolean e =
                (a & b) ^ (a || b) | a;
    }
}
