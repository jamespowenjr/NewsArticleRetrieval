import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class TimeSeriesFileLoader extends FileLoader<TimeSeries<Integer, Double>> {

    public Set<String> getAllSeriesNames() {
        Set<String> names = new HashSet<String>();


        return names;
    }


    public TimeSeriesFileLoader(String searchDirectory) {
        searchDirectory_ = searchDirectory;
    }


    private String searchDirectory_;
    private final static String FILE_EXTENSION_ = "csv";

    // TODO: match this up with the actual date format used.
    // TODO: also maybe make these configurable if there's time
    private final static DateFormat DATE_FORMAT_ = new SimpleDateFormat("");
    private final static String FIELD_SEPARATOR_ = ",";

    private final static Logger logger_ = Logger.getLogger(TimeSeriesFileLoader.class);


    @Override
    protected String createPath_(String query) {
        return new File(searchDirectory_, query + FILE_EXTENSION_).toString();
    }


    @Override
    protected TimeSeries<Integer, Double> parseFile_(String query, String path, FileInputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        int lineNumber = 0;
        TimeSeries<Integer, Double> timeSeries = new TimeSeries<Integer, Double>(query);

        while ((line = reader.readLine()) != null) {
            ++lineNumber;
            String[] fields = line.split(FIELD_SEPARATOR_);

            Date date;
            double value;
            try {
                if (fields.length != 2) {
                    throw new ParseException("All lines must have exactly 2 fields", 0);
                }
                date = DATE_FORMAT_.parse(fields[0]);
                value = Double.parseDouble(fields[1]);
            } catch (ParseException e) {
                logger_.error(String.format("Skipping line %d in file %s: %s", lineNumber, path, e.getMessage()));
                continue;
            }

            timeSeries.addEntry(Utils.intFromDate(date), value);
        }

        return timeSeries;
    }
}
