package com.lon.utils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @projectName: lon_v3
 * @package: com.lon.lon_v3.utils
 * @className: CollectionsUtils
 * @author: LONZT
 * @description: TODO
 * @date: 2023/6/20 16:31
 * @version: 1.0
 */
public class CollectionsUtils {

        /**
         * @param map:
             * @param value:
          * @return Set<K>
         * @author lonzt
         * @description 通过value获取key
         * @date 2023/6/20 16:32
         */
        public static <K, V> Set<K> getKeysByStream(Map<K, V> map, V value) {
            return map.entrySet()
                    .stream()
                    .filter(kvEntry -> Objects.equals(kvEntry.getValue(), value))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
        }

}
