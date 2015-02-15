package hh.homework.transactionexample;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * Created by fib on 04/01/15.
 */
class ResourceUtils {
    private ResourceUtils() {
    }

    public static String read(final String resourceName) throws URISyntaxException, IOException {

        final URI resourceURI = Thread.currentThread().getContextClassLoader().getResource(resourceName).toURI();
        final File resourceFile = new File(resourceURI);
        return Files.toString(resourceFile, Charset.defaultCharset());
    }
}
