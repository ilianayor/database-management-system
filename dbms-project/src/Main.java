import dbms.command.Command;
import dbms.executor.Executor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Executor executor = new Executor();
        Scanner scanner = new Scanner(System.in);

        String line = "";
        while (!line.equals(Command.QUIT.toString())) {
            line = scanner.nextLine();
            executor.execute(line);
        }
    }
}
