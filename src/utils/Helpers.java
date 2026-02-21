package utils;

import java.io.IOException;
import java.util.Scanner;

public class Helpers {

    public enum SessionType {
        Undefined,
        Normal, // Single timer, modifiable
        plan // series of timers in a planned schedule
    }

    public static boolean isNumber(String flag) {
        try {
            Integer.parseInt(flag);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void prettyPrint(String toPrint) throws InterruptedException {
        for (int i = 0; i < toPrint.length(); i++) {
            System.out.print(toPrint.charAt(i));
            Thread.sleep(2);
        }
        System.out.println();
    }

    public static void listenForInput(Scanner sc, Timer timer, SessionType currSession) throws IOException {
        String userInput = sc.nextLine();
        boolean isInputNeeded = (currSession == SessionType.Normal);
        while (!userInput.isBlank() && isInputNeeded) {
            if (isNumber(userInput)) {
                timer.setMinutes(Integer.parseInt(userInput));
                System.out.println("The timer is now set at " + timer.getMinutes() + " min");
                if (timer.getMinutes() > 60) {
                    System.out.println("--WARNING: the session is over an hour");
                }
                System.out.println("Press enter to continue");
            }
            userInput = sc.nextLine();
        }
    }

}