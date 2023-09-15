import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
public class Main
{
    /*
        Jingle from https://freesound.org/people/phantastonia/sounds/270602/#
     */

    static int minute = 60;
    public static void main(String[] args) throws InterruptedException, IOException
    {
        /*
            Info and constants
         */
        System.out.println("- JTimer - ");
        System.out.println("- Usages:");
        System.out.println("- java -jar JTimer.jar");
        System.out.println("- java -jar JTimer.jar -l (for a lower volume)");
        System.out.println("- java -jar JTimer.jar -L (for a much lower volume)");
        String filename = "jingle.wav";
        float volume = 0;

        /*
            Parse flags
         */
        for (String flag : args)
        {
            if (flag.length() != 2) continue;
            if (flag.charAt(0) != '-') continue;

            switch (flag.charAt(1))
            {
                case 'L' -> volume -= 20;
                case 'l' -> volume -= 10;
            }
        }

        /*
            Get input
         */
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the number of minutes");
        boolean invalid = true;
        int minutes = 20;

        while (invalid)
        {
            String input = in.nextLine();
            try
            {
                minutes = Integer.parseInt(input);
                if (minutes < 0) minutes = -minutes;
                invalid = false;
            }
            catch (Exception e)
            {
                System.out.println("Enter a valid number");
                // Continue loop
            }
        }

        // Entered a high number
        if (minutes > 60)
        {
            System.out.println("You have entered more than an hour ("+minutes+" min), are you sure? press enter to continue");
            listenForEnter();
        }

        while (true)
        {
            consoleClear();
            countdown(minutes);
            Audio.play(filename, volume);
            System.out.println("Press enter to restart");
            listenForEnter();
        }
    }

    private static void consoleClear() throws IOException, InterruptedException
    {
        final String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows"))
        {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        else
        {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        }
    }

    private static void countdown(int minutes) throws InterruptedException
    {
        for (int i = minutes; i > 0; i--)
        {
            System.out.println(i + " minutes left");
            Thread.sleep(minute * 1000L);
        }
    }

    private static void listenForEnter() throws IOException
    {
        int ignored = System.in.read(new byte[2]);
        ignored = System.in.read(new byte[System.in.available()]);
    }
}
