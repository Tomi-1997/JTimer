import java.io.IOException;
import java.util.Scanner;
public class Main
{
    /*
        Jingle from https://freesound.org/people/phantastonia/sounds/270602/#
     */
    public static void main(String[] args) throws InterruptedException, IOException
    {
        String filename = "jingle.wav";
        System.out.println("Enter the number of minutes");
        boolean invalid = true;
        Scanner in = new Scanner(System.in);
        int minutes = 20;
        while (invalid)
        {
            String input = in.nextLine();
            try
            {
                minutes = Integer.parseInt(input);
                invalid = false;
            }
            catch (Exception e)
            {
                System.out.println("Enter a valid number");
            }
        }

        while (true)
        {
            consoleClear();
            countdown(minutes);
            Audio.play(filename);
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
            Thread.sleep(60 * 1000);
        }
    }

    private static void listenForEnter() throws IOException
    {
        int ignored = System.in.read(new byte[2]);
        ignored = System.in.read(new byte[System.in.available()]);
    }
}
