/*
 * */
package com.synectiks.process.server.utilities;

public class ObjectUtils {

    private ObjectUtils() {}

    /**
     * Short hand for getting the object identity which is useful to distinguish between instances that override
     * {@link Object#toString()} and/or {@link Object#hashCode()}, especially in log statements.
     *
     * @param object any POJO
     * @return the hex string portion of {@link Object#toString()}
     */
    public static String objectId(Object object) {
        return Integer.toHexString(System.identityHashCode(object));
    }
}
