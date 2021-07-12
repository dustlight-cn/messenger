package plus.messenger.core.utils;

import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UriUtils {

    public static Map<String, String> getParameter(URI uri) {
        String query = uri.getRawQuery();
        if (query == null || !StringUtils.hasText(query))
            return null;
        Map<String, String> map = new HashMap();
        String[] kvs = query.split("&");
        for (String kv : kvs) {
            String[] tmp = kv.split("=", 2);
            if (tmp.length != 2)
                continue;
            String key = URLDecoder.decode(tmp[0], StandardCharsets.UTF_8),
                    value = URLDecoder.decode(tmp[1], StandardCharsets.UTF_8);
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + "," + value);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
}
