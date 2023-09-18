import java.io.IOException;
import java.util.Scanner;
public class Main
{
    /*
        Jingle from https://freesound.org/people/phantastonia/sounds/270602/#
     */

    static final int MINUTE = 60;
    public static void main(String[] args) throws InterruptedException, IOException
    {
        /*
            Info and constants
         */
        prettyPrint("- JTimer - ");
        prettyPrint("- Flags:");
        prettyPrint("-l to lower volume, -L to lower it dramatically, a number to begin timer immediately");
        prettyPrint("- Examples:");
        prettyPrint("- java -jar JTimer.jar");
        prettyPrint("- java -jar JTimer.jar -L (for a much lower volume)");
        prettyPrint("- java -jar JTimer.jar -l 20 (to start lower, without any more prompts)");
        String filename = "jingle.wav";


        float volume = 0;
        int minutes = -1;
        boolean invalidNumInput = true;
        /*
            Parse flags
         */
        for (String flag : args)
        {
            if (isNumber(flag))
            {
                minutes = Integer.parseInt(flag);
                if (minutes < 0) minutes = -minutes;
                invalidNumInput = false;
                continue;
            }
            if (flag.length() != 2) continue;
            if (flag.charAt(0) != '-') continue;

            switch (flag.charAt(1))
            {
                case 'L' -> volume -= 20;
                case 'l' -> volume -= 10;
            }
        }

        /*
            Get input, or skip if user entered number already
         */
        if (invalidNumInput)
        {
            String testText = "'test' to check the volume, ";
            String lowerText = "'lower' to decrease volume, ";
            String undoText = "'undo' to cancel previous 'lower' command";
            prettyPrint("Enter the number of minutes to begin, or:\n" + testText + lowerText + undoText);
        }


        /*
            Validate input
         */
        Scanner in = new Scanner(System.in);
        while (invalidNumInput)
        {
            String input = in.nextLine();
            boolean skipParse = false;
            if (input.toLowerCase().contains("lower"))
            {
                prettyPrint("Lowered volume");
                volume -= 10;
                skipParse = true;
            }
            if (input.toLowerCase().contains("undo"))
            {
                prettyPrint("Undid lowering");
                volume = Math.min(0, volume + 10);
                skipParse = true;
            }
            if (input.toLowerCase().contains("test"))
            {
                prettyPrint("Playing ");
                Audio.play(filename, volume);
                skipParse = true;
            }
            if (skipParse) continue;
            try
            {
                minutes = Integer.parseInt(input);
                if (minutes < 0) minutes = -minutes;
                invalidNumInput = false;
            }
            catch (Exception e)
            {
                prettyPrint("Enter a valid number \\ command");
                // Continue loop
            }
        }

        /*
            Another validation
         */
        if (minutes > 60)
        {
            prettyPrint("You have entered more than an hour ("+minutes+" min), are you sure? press enter to continue");
            listenForEnter();
        }

        /*
            Main loop - print to console each minute, play a jingle at the end, wait for enter, repeat.
         */
        while (true)
        {
            consoleClear();
            countdown(minutes);
            Audio.play(filename, volume);
            flush();
            System.out.println("Press enter to restart");
            listenForEnter();
        }
    }

    private static boolean isNumber(String flag)
    {
        try
        {
            Integer.parseInt(flag);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    private static void flush() throws IOException
    {
        int ignored;
        ignored = System.in.read(new byte[System.in.available()]);
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
            Thread.sleep(MINUTE * 1000L);
        }
    }

    private static void listenForEnter() throws IOException
    {
        int ignored = System.in.read(new byte[2]); // Enter
        ignored = System.in.read(new byte[System.in.available()]); // Whatever else there is besides enter
    }

    private static void prettyPrint(String toPrint) throws InterruptedException
    {
        for (int i = 0; i < toPrint.length(); i++)
        {
            System.out.print(toPrint.charAt(i));
            Thread.sleep(5);
        }
        System.out.println();
    }
}
