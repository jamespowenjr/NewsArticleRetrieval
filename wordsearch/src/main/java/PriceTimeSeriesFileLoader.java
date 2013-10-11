public class PriceTimeSeriesFileLoader extends TimeSeriesFileLoader<Double> {

    public PriceTimeSeriesFileLoader(String searchDirectory) {
        super(searchDirectory);
    }

    @Override
    protected Double parseValue_(String value) {
        return Double.parseDouble(value);
    }
}
