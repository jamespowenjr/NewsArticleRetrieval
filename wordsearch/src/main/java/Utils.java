import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    public final static int MS_IN_DAY = 60 * 60 * 24 * 1000;

    public static int intFromDate(Date date) {
        return (int) (date.getTime() / MS_IN_DAY);
    }

    public static Date dateFromInt(int n) {
        return new Date(((long) n) * MS_IN_DAY);
    }

    public static List<Double> asDoubles(List<Integer> ints) {
        List<Double> doubles = new ArrayList<Double>(ints.size());
        for (Integer i : ints) {
            doubles.add((double) i);
        }
        return doubles;
    }
}
