import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public abstract class FileLoader<T> implements Loader<T, String> {

    @Override
    public T load(String query) {
        File file = new File(createPath_(query));
        if (!file.exists()) {
            return null;
        }

        try {
            return parseFile_(query, file);
        } catch (IOException e) {
            logger_.error(String.format("Error loading file %s: %s", file.toString(), e.getMessage()));
            return null;
        }
    }


    private static final Logger logger_ = Logger.getLogger(FileLoader.class);


    protected abstract String createPath_(String query);

    protected abstract T parseFile_(String query, File file) throws IOException;
}
