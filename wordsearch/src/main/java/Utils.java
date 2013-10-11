import java.util.Date;

public class Utils {

    public final static int MS_IN_DAY = 60 * 60 * 24 * 1000;

    public static int intFromDate(Date date) {
        return (int) (date.getTime() / MS_IN_DAY);
    }
}
