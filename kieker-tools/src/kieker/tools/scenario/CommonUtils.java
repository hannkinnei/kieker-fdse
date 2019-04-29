package kieker.tools.scenario;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.alibaba.druid.util.Utils.read;

/**
 * @author IcedSoul
 * @date 19-4-29 下午2:09
 */
public class CommonUtils {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

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

    public static String getStringTime(Long time){
        return simpleDateFormat.format(time);
    }

    public static Long getLongTime(String time) throws ParseException {
        return simpleDateFormat.parse(time).getTime();
    }

    public static String getNow(){
        return simpleDateFormat.format(new Date());
    }
}
