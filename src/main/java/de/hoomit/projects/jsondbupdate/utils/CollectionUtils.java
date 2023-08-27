package de.hoomit.projects.jsondbupdate.utils;

import java.util.Collection;

public class CollectionUtils {

    public static boolean isNotEmpty(final Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
}
