package utils;

public class Helpers {

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
}