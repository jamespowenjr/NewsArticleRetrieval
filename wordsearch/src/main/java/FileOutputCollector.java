public class FileOutputCollector implements ResultCollector<WordMatch> {

    @Override
    public void collect(WordMatch result) {
        // TODO: write results to file in some format
    }


    public FileOutputCollector(String path) {
        outputPath_ = path;
    }

    private String outputPath_;
}
