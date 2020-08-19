package TreeWalker.coding.AvoidDoubleBraceInitialization;

import java.util.ArrayList;
import java.util.List;

public class Test {
    List<Integer> list1 = new ArrayList<Integer>() {  // violation without filter

        {
            add(1);
        }
    };

    List<Integer> list2 = new ArrayList<Integer>() {  // violation without filter

        {
            add(1);
        }
    };
}
