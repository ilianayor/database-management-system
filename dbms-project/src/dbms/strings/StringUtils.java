package dbms.strings;

public class StringUtils {
    public static int indexOf(String str, char c) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                return i;
            }
        }

        return -1;
    }

    public static String substring(String str, int start, int end) {
        StringBuilder sb = new StringBuilder();

        for (int i = start; i < end; i++) {
            sb.append(str.charAt(i));
        }

        return sb.toString();
    }

    public static String substring(String str, int start) {
        StringBuilder sb = new StringBuilder();

        for (int i = start; i < str.length(); i++) {
            sb.append(str.charAt(i));
        }

        return sb.toString();
    }

    public static String[] split(String str, char c) {
        String[] result = new String[countNumberOfOccurrences(str, c) + 1];
        int idx = 0;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != c) {
                sb.append(str.charAt(i));
            } else {
                result[idx++] = sb.toString();
                sb = new StringBuilder();
            }
        }

        if (!sb.isEmpty()) {
            result[idx] = sb.toString();
        }

        return result;
    }

    private static int countNumberOfOccurrences(String str, char c) {
        int cnt = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                cnt++;
            }
        }

        return cnt;
    }

    public static boolean startsWith(String a, String b) {
        if (b.length() > a.length()) {
            return false;
        }

        for (int i = 0; i < b.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    public static boolean endsWith(String a, String b) {
        if (b.length() > a.length()) {
            return false;
        }

        for (int i = b.length() - 1; i >= 0; i--) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    public static String[] collectFromIndex(String[] array, int index) {
        if (index < 0 || index >= array.length) {
            return array;
        }
        String[] result = new String[array.length - index];
        int idx = 0;

        for (int i = index; i < array.length; i++) {
            result[idx++] = array[i];
        }

        return result;
    }
}
