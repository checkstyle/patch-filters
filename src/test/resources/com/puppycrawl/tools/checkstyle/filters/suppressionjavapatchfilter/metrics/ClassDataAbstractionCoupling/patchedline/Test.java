package TreeWalker.metrics.ClassDataAbstractionCoupling;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Test {  // violation without filter
    public void test() {
        HashSet set = new HashSet();
        Map map = new HashMap();
        Date date = new Date();
        Time time = new Time(2323);
    }
}

class Test1 {  // violation without filter
    public void test() {
        HashSet set = new HashSet();
        Map map = new HashMap();
        Date date = new Date();
        Time time = new Time(2323);
    }
}
