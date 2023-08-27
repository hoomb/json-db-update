package de.hoomit.projects.jsondbupdate.utils;

public class FilenameUtils {
    public static String removeExtension(final String filename) {
        return filename != null && filename.lastIndexOf(".") > 0 ? filename.substring(0, filename.lastIndexOf(".")) : filename;
    }
}
