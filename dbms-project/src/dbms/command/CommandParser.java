package dbms.command;

import dbms.strings.StringUtils;

public class CommandParser {
    public Pair parse(String line) {
        String command = StringUtils.split(line, ' ')[0];
        int indexOfFirstSpace = StringUtils.indexOf(line, ' ');

        if (indexOfFirstSpace == -1) {
            return new Pair(command, null);
        }

        String args = StringUtils.substring(line, StringUtils.indexOf(line, ' ') + 1);

        return new Pair(command, args);
    }
}
