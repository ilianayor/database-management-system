package dbms.command;

public class Pair {
    private final String commandName;
    private final String args;

    public Pair(String commandName, String args) {
        this.commandName = commandName;
        this.args = args;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getArgs() {
        return args;
    }
}
