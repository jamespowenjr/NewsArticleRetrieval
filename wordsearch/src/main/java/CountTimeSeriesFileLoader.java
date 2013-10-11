public class CountTimeSeriesFileLoader extends TimeSeriesFileLoader<Integer> {

    public CountTimeSeriesFileLoader(String searchDirectory) {
        super(searchDirectory);
    }

    @Override
    protected Integer parseValue_(String value) {
        return Integer.parseInt(value);
    }
}
