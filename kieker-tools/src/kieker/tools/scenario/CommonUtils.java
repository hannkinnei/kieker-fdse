package kieker.tools.scenario;

import java.io.IOException;
import java.io.InputStream;

import static com.alibaba.druid.util.Utils.read;

/**
 * @author IcedSoul
 * @date 19-4-29 下午2:09
 */
public class CommonUtils {
    public static String readFromResource(String resource) throws IOException {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if (in == null) {
                in = CommonUtils.class.getResourceAsStream(resource);
            }
            if(in == null)
                return null;
            return read(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
