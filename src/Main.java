import java.io.*;
import java.util.Scanner;
public class Main
{
    /*
        Jingle from https://freesound.org/people/phantastonia/sounds/270602/#
     */

    static final int MINUTE = 60;
    public static void main(String[] args) throws InterruptedException, IOException
    {
        // Required variables
        String filename = "jingle.wav";
        float volume = 0;
        int minutes = -1;
        boolean minInputMissing = true;

        // Parse flags
        for (String flag : args)
        {
            if (isNumber(flag))
            {
                minutes = Integer.parseInt(flag);
                if (minutes < 0) minutes = -minutes;
                minInputMissing = false;
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

        // If input in flags, skip- else print info and get user input
        if (minInputMissing) printInfo();

        // Validate user input
        Scanner in = new Scanner(System.in);
        while (minInputMissing)
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
                minInputMissing = false;
            }
            catch (Exception e)
            {
                prettyPrint("Enter a valid number \\ command");
                // Continue loop
            }
        }

        // Print warning if entered a large amount
        if (minutes > 60)
        {
            prettyPrint("You have entered more than an hour ("+minutes+" min), are you sure? press enter to continue");
            listenForEnter();
        }

        // Main loop - print to console each minute, play a jingle at the end, wait for enter, repeat.
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

    private static void printInfo()
    {
        try
        {
            InputStream is = Main.class.getResourceAsStream("info.txt");
            assert is != null;
            Scanner in = new Scanner(is);
            while (in.hasNext()) prettyPrint(in.nextLine());
        }
        catch (Exception e)
        {
//            e.printStackTrace();
            System.out.println("Could not open info file, enter the number of minutes to begin");
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
