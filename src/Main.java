import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import utils.Timer;
import utils.Helpers.SessionType;
import static utils.Helpers.prettyPrint;
import static utils.Helpers.listenForInput;

import static utils.Logger.writeLogFile;
import static utils.Logger.parseLogFile;

public class Main {
    /*
     * Jingles from pixabay.com,
     * https://freesound.org/people/phantastonia/sounds/270602/#
     */

    // static int screenTimeSumMinutes = 0;
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
    private Schedule schedule;

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
        this.schedule = null;
    }

    // main class methods
    public void init() throws IOException {
        initJingles(this.files, this.folderName);
    }

    public void run(String[] args) throws InterruptedException, IOException {
        parseFlags(args);

        // If input in flags, skip- else print info and get user input
        if (this.timer.isInit())
            parser.printHelp();

        // Get user input for volume commands or to start the program
        Scanner in = new Scanner(System.in);
        try {
            parseCommands(in);
            // Try to read from file accumulated minutes
            // If earlier date, reset
            parseLogFile(textFileName, this.timer);

            // Notification when it is time to rest
            Notifier notifier = new Notifier(notification, "JTimer:", "Time for a break");

            if (currSessionType == SessionType.Normal) {
                runNormalSession(in, notifier);
            } else {
                runSchedule(in, notifier);
            }

        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private void runSchedule(Scanner in, Notifier notifier) throws InterruptedException, IOException {
        if (this.schedule == null) {
            System.out.println("Schedule undefined");
            return;
        }
        if (this.schedule.getSize() < 1) {
            System.out.println("Schedule is empty");
            return;
        }
        int taskIndex = 0;
        int numOfTasks = this.schedule.getSize();
        while (taskIndex < numOfTasks) {
            consoleClear();
            // print schedule details
            this.schedule.printScheduleInfo();
            System.out.println("Running task " + (taskIndex + 1) + "/" + numOfTasks);

            // print task details
            Schedule.Task currTask = this.schedule.getTaskAt(taskIndex);
            currTask.info();
            this.timer.setMinutes(currTask.time());

            countdown();

            // Countdown over
            notifier.notifyUser();
            playRandom(this.files, this.volume);
            flush();
            System.out.println("Stopped at:");
            System.out.println(Calendar.getInstance().getTime());
            System.out.println("Overall Screen Time: " + timer.getScreenTimeSumString());
            System.out.println("Press enter to continue");

            // Awaiting user
            listenForInput(in, timer, currSessionType);
            notifier.notifyRemove();

            taskIndex = schedule.isRepeat() ? (taskIndex + 1) % numOfTasks : (taskIndex + 1);
        }
    }

    private void runNormalSession(Scanner in, Notifier notifier) throws InterruptedException, IOException {
        // Print warning if entered a large amount
        if (this.timer.getMinutes() > 60) {
            prettyPrint("--WARNING: the session is over an hour (" + this.timer.getMinutes()
                    + " min)");
            prettyPrint("Insert a number to change time, otherwise press enter to continue");
            listenForInput(in, this.timer, currSessionType);
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
            System.out.println("Overall Screen Time: " + timer.getScreenTimeSumString());
            System.out.println("Put a number to modify time");
            System.out.println("Otherwise, press enter to restart");

            // Awaiting user
            listenForInput(in, timer, currSessionType);
            notifier.notifyRemove();
        }
    }

    private void parseFlags(String[] flags) {
        for (int i = 0; i < flags.length; i++) {
            String flag = flags[i];
            String key = parser.parseArgs(flag, true);
            switch (key) {
                case "#" -> {
                    if (currSessionType == SessionType.Undefined) {
                        this.timer.setMinutes(Integer.parseInt(flag));
                        currSessionType = SessionType.Normal;
                    }
                }
                case "L" -> {
                    volume -= 20;
                }
                case "l" -> {
                    volume -= 10;
                }
                case "p" -> {
                    if (i + 1 >= flag.length()) {
                        continue;
                    }
                    currSessionType = SessionType.plan;
                    String filename = flags[i + 1];
                    schedule = new Schedule(filename);
                    this.timer.initTimer();
                }
                case "n" -> {
                    notification = !notification;
                }
                case "t" -> {
                    playRandom(files, volume);
                }
                case "u" -> {
                    continue;
                }
                case null, default -> {
                    continue;
                }
            }
        }
    }

    private void countdown() throws InterruptedException {
        int minutes = this.timer.getMinutes();
        for (int i = minutes; i > 0; i--) {
            System.out.println(i + " minutes left");
            Thread.sleep(SEC_IN_MINUTE * MILLI_TO_SEC);
            this.timer.updateScreenTime();
            writeLogFile(this.textFileName, this.timer.getScreenTime());
        }
    }

    private void parseCommands(Scanner in) throws InterruptedException {
        while (this.timer.isInit()) {
            String line = in.nextLine();
            String input = this.parser.parseArgs(line, false);
            switch (input) {
                case "#" -> {
                    this.timer.setMinutes(Integer.parseInt(line));
                    currSessionType = SessionType.Normal;
                }
                case "l" -> {
                    prettyPrint("Lowered volume");
                    volume -= 10;
                }

                case "u" -> {
                    prettyPrint("Undid lowering");
                    volume = Math.min(0, volume + 10);

                }
                case "t" -> {
                    prettyPrint("Playing");
                    playRandom(this.files, this.volume);

                }
                case "n" -> {
                    notification = !notification;
                    prettyPrint("Notification Enabled-> " + notification);

                }
                case "p" -> {
                    String filename = parser.parseFilename(line);
                    if (filename == null) {
                        prettyPrint("--Error: Missing filename");
                    } else {
                        currSessionType = SessionType.plan;
                        schedule = new Schedule(filename);
                        this.timer.initTimer();
                    }

                }
                case null, default -> {
                    prettyPrint("Please enter a valid command \\ number");

                }

            }
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

    // audio helpers
    private static void initJingles(ArrayList<String> files, String filename) throws IOException {
        /**
         * Init jingle list to play in the end of a session
         */
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