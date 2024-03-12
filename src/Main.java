import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main
{
    /*
        Jingles from pixabay.com, https://freesound.org/people/phantastonia/sounds/270602/#
     */

    static final int MINUTE = 60;
    public static void main(String[] args) throws InterruptedException, IOException
    {
        // Required variables
        String folderName = "jingles";
        float volume = 0;
        int minutes = -1;
        boolean minInputMissing = true;

        // Init jingle list
        ArrayList<String> files = new ArrayList<>();
        initJingles(files, folderName);

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
                playRandom(files, volume);
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
            playRandom(files, volume);
            flush();
            System.out.println("Press enter to restart");
            listenForEnter();
        }
    }

    private static void initJingles(ArrayList<String> files, String filename) throws IOException
    {
        initJinglesJAR(files);
        if (files.size() > 0) return;

        File f = new File("src\\" + filename);
        for (String str : Objects.requireNonNull(f.list()))
        {
            files.add(filename + "\\" + str);
        }
    }

    private static void initJinglesJAR(ArrayList<String> files) throws IOException
    {
        CodeSource src = Main.class.getProtectionDomain().getCodeSource();

        if( src != null ) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream( jar.openStream());
            ZipEntry ze;

            while( ( ze = zip.getNextEntry() ) != null ) {
                String entryName = ze.getName();
                if( entryName.endsWith(".wav") ) {
                    files.add( entryName  );
                }
            }

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
            Thread.sleep(2);
        }
        System.out.println();
    }

    private static void playRandom(ArrayList<String> files, float volume)
    {
        int rndIndex = new Random().nextInt(files.size());
        String filename = files.get(rndIndex);
        Audio.play(filename, volume);
    }
}
