package me.bruno;


/**
 * Utils for file names, modified to only have used methods
 *
 * @author Apache
 */
public class FilenameUtils {

    private static boolean isSeparator(char ch) {
        return ch == '/' || ch == '\\';
    }

    public static int getPrefixLength(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int len = filename.length();
            if (len == 0) {
                return 0;
            } else {
                char ch0 = filename.charAt(0);
                if (ch0 == ':') {
                    return -1;
                } else if (len == 1) {
                    if (ch0 == '~') {
                        return 2;
                    } else {
                        return isSeparator(ch0) ? 1 : 0;
                    }
                } else {
                    int ch1;
                    int posUnix;
                    if (ch0 == '~') {
                        ch1 = filename.indexOf(47, 1);
                        posUnix = filename.indexOf(92, 1);
                        if (ch1 == -1 && posUnix == -1) {
                            return len + 1;
                        } else {
                            ch1 = ch1 == -1 ? posUnix : ch1;
                            posUnix = posUnix == -1 ? ch1 : posUnix;
                            return Math.min(ch1, posUnix) + 1;
                        }
                    } else {
                        ch1 = filename.charAt(1);
                        if (ch1 == 58) {
                            ch0 = Character.toUpperCase(ch0);
                            if (ch0 >= 'A' && ch0 <= 'Z') {
                                return len != 2 && isSeparator(filename.charAt(2)) ? 3 : 2;
                            } else {
                                return -1;
                            }
                        } else if (isSeparator(ch0) && isSeparator((char)ch1)) {
                            posUnix = filename.indexOf(47, 2);
                            int posWin = filename.indexOf(92, 2);
                            if ((posUnix != -1 || posWin != -1) && posUnix != 2 && posWin != 2) {
                                posUnix = posUnix == -1 ? posWin : posUnix;
                                posWin = posWin == -1 ? posUnix : posWin;
                                return Math.min(posUnix, posWin) + 1;
                            } else {
                                return -1;
                            }
                        } else {
                            return isSeparator(ch0) ? 1 : 0;
                        }
                    }
                }
            }
        }
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            return Math.max(lastUnixPos, lastWindowsPos);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int extensionPos = filename.lastIndexOf(46);
            int lastSeparator = indexOfLastSeparator(filename);
            return lastSeparator > extensionPos ? -1 : extensionPos;
        }
    }

    public static String getPrefix(String filename) {
        if (filename == null) {
            return null;
        } else {
            int len = getPrefixLength(filename);
            if (len < 0) {
                return null;
            } else {
                return len > filename.length() ? filename + '/' : filename.substring(0, len);
            }
        }
    }



    public static String getFullPath(String filename) {
        return doGetFullPath(filename, true);
    }

    private static String doGetFullPath(String filename, boolean includeSeparator) {
        if (filename == null) {
            return null;
        } else {
            int prefix = getPrefixLength(filename);
            if (prefix < 0) {
                return null;
            } else if (prefix >= filename.length()) {
                return includeSeparator ? getPrefix(filename) : filename;
            } else {
                int index = indexOfLastSeparator(filename);
                if (index < 0) {
                    return filename.substring(0, prefix);
                } else {
                    int end = index + (includeSeparator ? 1 : 0);
                    return filename.substring(0, end);
                }
            }
        }
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        } else {
            int index = indexOfLastSeparator(filename);
            return filename.substring(index + 1);
        }
    }

    public static String getBaseName(String filename) {
        return removeExtension(getName(filename));
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        } else {
            int index = indexOfExtension(filename);
            return index == -1 ? "" : filename.substring(index + 1);
        }
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        } else {
            int index = indexOfExtension(filename);
            return index == -1 ? filename : filename.substring(0, index);
        }
    }


}
