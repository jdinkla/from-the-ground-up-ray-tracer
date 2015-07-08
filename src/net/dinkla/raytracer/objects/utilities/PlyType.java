package net.dinkla.raytracer.objects.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 14.06.2010
 * Time: 20:21:12
 * To change this template use File | Settings | File Templates.
 */
public enum PlyType {

    CHAR("char", Short.class, 1),
    UCHAR("uchar", Short.class, 1),
    SHORT("short", Short.class, 2),
    USHORT("ushort", Short.class, 2),
    INT("int", Integer.class, 4),
    UINT("uint", Integer.class, 4),
    FLOAT("float", Integer.class, 4),
    DOUBLE("double", Integer.class, 8);

    static public final Map<String, PlyType> map;

    static {
        map = new HashMap<String, PlyType>();
        map.put("char", CHAR);
        map.put("uchar", UCHAR);
        map.put("short", SHORT);
        map.put("ushort", USHORT);
        map.put("int", INT);
        map.put("uint", UINT);
        map.put("float", FLOAT);
        map.put("double", DOUBLE);
    }

    final String key;
    final Class clazz;
    final int size;

    PlyType(final String key, final Class clazz, final int size) {
        this.key = key;
        this.clazz = clazz;
        this.size = size;
    }

}
