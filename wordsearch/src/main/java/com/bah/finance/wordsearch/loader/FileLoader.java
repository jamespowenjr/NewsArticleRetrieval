package com.bah.finance.wordsearch.loader;

import com.bah.finance.wordsearch.util.PropertyException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class FileLoader<T> implements Loader<T, String> {

    @Override
    public void configure(Properties props) throws PropertyException {

    }

    @Override
    public T load(String query) {
        String path = createPath_(query);
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return parseFile_(query, path, stream);
        } catch (IOException e) {
            logger_.error(String.format("Error loading file %s: %s", file.toString(), e.getMessage()));
            return null;
        } finally {
            if (stream != null) {
                try {
                        stream.close();
                } catch (IOException e) { }
            }
        }
    }


    private static final Logger logger_ = Logger.getLogger(FileLoader.class);


    protected abstract String createPath_(String query);

    protected abstract T parseFile_(String query, String path, FileInputStream stream) throws IOException;
}
