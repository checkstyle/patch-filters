package TreeWalker.blocks.RightCurly;

public class Test {
    public void test() {
        int x = 0;
        while (true) {
            try {
                if (x > 0)
                {
                    break;
                }  // violation without filter
                else if (x < 0)
                {
                    ;
                }  // violation without filter
                else
                {
                    break;
                }

            }  // violation without filter
            catch (Exception e)
            {
                break;  //changed
            }  // violation without filter
            finally
            {
                break;
            }
        }

        try {

        }  // violation without filter
        catch (Exception e)
        {

        }
    }
}
