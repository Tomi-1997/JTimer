import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import utils.Timer;
import static utils.Helpers.isNumber;
import static utils.Helpers.getScreenTimeSumString;

// TODO integrate plan making
public class Main {
    /*
     * Jingles from pixabay.com,
     * https://freesound.org/people/phantastonia/sounds/270602/#
     */

    static int screenTimeSumMinutes = 0;
    static String currentDate = SimpleDateFormat.getDateInstance().format(Calendar.getInstance().getTime());
    static final int SEC_IN_MINUTE = 1; // TODO 60
    static final long MILLI_TO_SEC = 1000L;

    private float volume = 0; // Default volume level
    private boolean notification = true; // Get attention with User notification tray

    // audio files
    private ArrayList<String> files;
    private String folderName = "jingles"; // Folder to play jingles from
    private String textFileName = ".JTimer_Screen_Time"; // Saved screen time between program runs (date, sum)

    private Timer timer;
    private Parser parser;

    private enum SessionType {
        Normal, // Single timer, modifiable
        // Plan = Series of timers planned ahead
        Plan, // Single plan to go through
        RepeatingPlan // Plan that in the end of it will reset to its beginning
    }

    private SessionType currSessionType;

    public static void main(String[] args) throws InterruptedException, IOException {
        Main appInstance = new Main();
        appInstance.init();
        appInstance.run(args);
    }

    public Main() {
        this.files = new ArrayList<>();
        this.timer = new Timer();
        this.parser = new Parser();
    }

    // main class methods
    public void init() throws IOException {
        initJingles(this.files, this.folderName);
    }

    public void run(String[] args) throws InterruptedException, IOException {
        parseFlags(args);

        // If input in flags, skip- else print info and get user input
        if (this.timer.minutesInputMissing)
            printInfo();

        // Get user input for volume commands or to start the program
        Scanner in = new Scanner(System.in);
        try {
            parseCommands(in);
            // Try to read from file accumulated minutes
            // If earlier date, reset
            parseLogFile(textFileName);

            // Notification when it is time to rest
            Notifier notifier = new Notifier(notification, "JTimer:", "Time for a break");

            if (currSessionType == SessionType.Normal) {
                runNormalSession(in, notifier);
            } else {
                // runPlannedSession();
            }

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private void runNormalSession(Scanner in, Notifier notifier) throws InterruptedException, IOException {
        // Print warning if entered a large amount
        if (this.timer.getMinutes() > 60) {
            prettyPrint("--WARNING: the session is over an hour (" + this.timer.getMinutes()
                    + " min)");
            prettyPrint("Insert a number to change time, otherwise press enter to continue");
            listenForInput(in, this.timer);
        }

        // Main loop -
        // 1 print to console each minute
        // 2 play a jingle at the end
        // 3 wait for enter
        while (true) {
            consoleClear();
            countdown();

            // Countdown over
            notifier.notifyUser();
            playRandom(this.files, this.volume);
            flush();
            System.out.println("Stopped at:");
            System.out.println(Calendar.getInstance().getTime());
            System.out.println("Overall Screen Time: " + getScreenTimeSumString(screenTimeSumMinutes));
            System.out.println("Put a number to modify time");
            System.out.println("Otherwise, press enter to restart");

            // Awaiting user
            listenForInput(in, timer);
            notifier.notifyRemove();
        }
    }

    private void parseFlags(String[] flags) {
        for (String flag : flags) {
            if (isNumber(flag)) {
                this.timer.addedMinutes(Integer.parseInt(flag));
                currSessionType = SessionType.Normal;
                continue;
            }

            // If the following flags don't match the "-char" pattern, continue
            if (flag.length() != 2)
                continue;
            if (flag.charAt(0) != '-')
                continue;

            switch (flag.charAt(1)) {
                case 'L' -> volume -= 20;
                case 'l' -> volume -= 10;
                case 'p' -> {
                    currSessionType = SessionType.Plan;

                }
            }
        }
    }

    private void countdown() throws InterruptedException {
        int minutes = this.timer.getMinutes();
        for (int i = minutes; i > 0; i--) {
            System.out.println(i + " minutes left");
            Thread.sleep(SEC_IN_MINUTE * MILLI_TO_SEC);
            screenTimeSumMinutes++;
            writeLogFile(this.textFileName, screenTimeSumMinutes);
        }
    }

    private void parseCommands(Scanner in) throws InterruptedException {
        while (this.timer.minutesInputMissing) {
            String line = in.nextLine();
            String input = this.parser.parseCommand(line);
            switch (input) {
                case "#":{
                    this.timer.addedMinutes(Integer.parseInt(line));
                    currSessionType = SessionType.Normal;
                    break;
                }
                case "l":
                    prettyPrint("Lowered volume");
                    volume -= 10;
                    break;
                case "u": {
                    prettyPrint("Undid lowering");
                    volume = Math.min(0, volume + 10);
                    break;
                }
                case "t": {
                    prettyPrint("Playing");
                    playRandom(this.files, this.volume);
                    break;
                }
                case "n": {
                    notification = !notification;
                    prettyPrint("Notification Enabled: " + notification);
                    break;
                }
                case "p":{
                    //strip line from p/plan, then use it as a file
                    // if missing file ask for one within
                    break;
                }
                case null:
                default: {
                    prettyPrint("Please enter a valid command \\ number");
                    break;
                }
                    
            }
        }
    }
        // log helpers
    private static void parseLogFile(String filename) {
        Path file = Paths.get(filename);
        try {
            List<String> s = Files.readAllLines(file);
            String fileDate = s.get(0);

            // Different day, don't load data
            if (fileDate.compareTo(currentDate) != 0) {
                return;
            }

            // Parse accumulated minutes to int
            screenTimeSumMinutes = Integer.parseInt(s.get(1));
        } catch (IOException ignored) {

        }
    }

    private static void writeLogFile(String filename, int minutes) {
        try {
            PrintWriter writer = new PrintWriter(filename, StandardCharsets.UTF_8);
            writer.println(currentDate);
            writer.println(minutes);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // console helpers
    private static void flush() throws IOException {
        System.in.read(new byte[System.in.available()]);
    }

    private static void consoleClear() throws IOException, InterruptedException {
        final String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        }
    }

    private static void listenForInput(Scanner sc, Timer timer) throws IOException {
        String userInput = sc.nextLine();
        while (!userInput.trim().isEmpty()) {
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

    // printing helpers
    private static void printInfo() {
        try (InputStream is = Main.class.getResourceAsStream("info.txt")) {
            assert is != null;
            try (Scanner in = new Scanner(is)) {
                while (in.hasNext())
                    prettyPrint(in.nextLine());
            }
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Could not open info file, enter the number of minutes to begin");
        }
    }

    public static void prettyPrint(String toPrint) throws InterruptedException {
        for (int i = 0; i < toPrint.length(); i++) {
            System.out.print(toPrint.charAt(i));
            Thread.sleep(2);
        }
        System.out.println();
    }

    // audio helpers
    /**
     * Init jingle list to play in the end of a session
     */
    private static void initJingles(ArrayList<String> files, String filename) throws IOException {
        /* Ran as a JAR, add all files that end with .wav */
        initJinglesJAR(files);
        if (files.size() > 0)
            return;

        /* Ran from IDE, add all files from the given folder */
        File f = new File("src\\" + filename);
        for (String str : Objects.requireNonNull(f.list())) {
            files.add(filename + "\\" + str);
        }
    }

    private static void initJinglesJAR(ArrayList<String> files) throws IOException {
        CodeSource src = Main.class.getProtectionDomain().getCodeSource();

        if (src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry ze;

            while ((ze = zip.getNextEntry()) != null) {
                String entryName = ze.getName();
                if (entryName.endsWith(".wav")) {
                    files.add(entryName);
                }
            }

        }
    }

    private static void playRandom(ArrayList<String> files, float volume) {
        int rndIndex = new Random().nextInt(files.size());
        String filename = files.get(rndIndex);
        Audio.play(filename, volume);
    }

}