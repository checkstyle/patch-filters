package TreeWalker.metrics.ClassFanOutComplexity;

import java.sql.Time;
import java.util.*;

public class Test {   // violation without filter
    public void test() {
        HashSet set = new HashSet();
        Map map = new HashMap();
        Date date = new Date();
        Time time = new Time(2323);
    }
}
