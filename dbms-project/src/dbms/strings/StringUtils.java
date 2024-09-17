package dbms.strings;

import dbms.keyword.Keyword;

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

    public static String extractSubstringAfter(String args, String keyword) {
        int indexOfWhereKeyword = StringUtils.indexOf(args, keyword);
        int offset = indexOfWhereKeyword + keyword.length();
        String substr = StringUtils.substring(args, offset);
        int indexOfFirstNonEmptyLetter = StringUtils.extractIndexOfFirstNonEmptyLetter(substr);

        return StringUtils.substring(args, indexOfFirstNonEmptyLetter + offset);
    }

    public static String substring(String str, int start) {
        StringBuilder sb = new StringBuilder();

        for (int i = start; i < str.length(); i++) {
            sb.append(str.charAt(i));
        }

        return sb.toString();
    }

    public static int indexOf(String a, String b) {
        if (a == null || b == null || b.length() > a.length()) {
            return -1;
        }

        if (b.isEmpty()) {
            return 0;
        }

        for (int i = 0; i <= a.length() - b.length(); i++) {
            int j;

            for (j = 0; j < b.length(); j++) {
                if (a.charAt(i + j) != b.charAt(j)) {
                    break;
                }
            }

            if (j == b.length()) {
                return i;
            }
        }

        return -1;
    }

    public static String[] split(String str, String delimiter) {
        if (str == null || delimiter == null || delimiter.isEmpty()) {
            return new String[]{};
        }

        int delimiterCount = countOccurrences(str, delimiter);
        String[] result = new String[delimiterCount + 1];

        int start = 0;
        int resultIndex = 0;
        for (int i = 0; i <= str.length() - delimiter.length(); i++) {
            if (isDelimiterAt(str, delimiter, i)) {
                result[resultIndex++] = StringUtils.substring(str, start, i);
                start = i + delimiter.length();
                i = start - 1;
            }
        }

        result[resultIndex] = StringUtils.substring(str, start);

        return result;
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

    private static int countOccurrences(String text, String delimiter) {
        int count = 0;

        for (int i = 0; i <= text.length() - delimiter.length(); i++) {
            if (isDelimiterAt(text, delimiter, i)) {
                count++;
                i += delimiter.length() - 1;
            }
        }

        return count;
    }

    private static boolean isDelimiterAt(String text, String delimiter, int index) {
        for (int j = 0; j < delimiter.length(); j++) {
            if (text.charAt(index + j) != delimiter.charAt(j)) {
                return false;
            }
        }

        return true;
    }

    public static String join(String[] parts, String delimiter) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            if (i != parts.length - 1) {
                sb.append(delimiter);
            }
        }

        return sb.toString();
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

    public static boolean contains(String str, String substr) {
        if (substr.length() > str.length()) {
            return false;
        }

        for (int i = 0; i < str.length() - substr.length(); i++) {
            if (str.charAt(i) == substr.charAt(0)) {
                boolean isSubstring = true;

                for (int j = 0; j < substr.length(); j++) {
                    if (str.charAt(i + j) != substr.charAt(j)) {
                        isSubstring = false;
                        break;
                    }
                }

                if (isSubstring) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isLessThan(String a, String b) {
        if (a == null || b == null) {
            return false;
        }

        int minLength = min(a.length(), b.length());

        for (int i = 0; i < minLength; i++) {
            char charA = a.charAt(i);
            char charB = b.charAt(i);

            if (charA < charB) {
                return true;
            } else if (charA > charB) {
                return false;
            }
        }

        return a.length() < b.length();
    }

    private static int min(int a, int b) {
        if (a < b) {
            return a;
        }

        return b;
    }

    public static void sortLexicographically(String[] arr) {
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    String temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static int extractIndexOfFirstNonEmptyLetter(String s) {
        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) >= 'a' && s.charAt(i) <= 'z') {
                return i;
            }

            if(s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') {
                return i;
            }
        }

        return -1;
    }

    public static int countUnique(String[] strings) {
        if (strings == null) {
            return 0;
        }

        if (strings.length == 1) {
            return 1;
        }

        sortLexicographically(strings);
        int cnt = 1;
        String last = strings[0];

        for (int i = 1; i < strings.length; i++) {
            if (!last.equals(strings[i])) {
                cnt++;
            }

            last = strings[i];
        }

        return cnt;
    }
}
