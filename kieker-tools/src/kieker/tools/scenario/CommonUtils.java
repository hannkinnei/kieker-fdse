package kieker.tools.scenario;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static String read(InputStream in) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            throw new IllegalStateException(var3.getMessage(), var3);
        }

        return read((Reader)reader);
    }

    public static String read(Reader reader) {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[4096];
            boolean var3 = false;

            int n;
            while(-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }

            return writer.toString();
        } catch (IOException var4) {
            throw new IllegalStateException("read error", var4);
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
