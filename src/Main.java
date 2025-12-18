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

//TODO add overall break time
// TODO add change time flag
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

    public void run(String[] args) throws InterruptedException, IOException {
        // Required variables
        String folderName = "jingles"; // Folder to play jingles from
        String textFileName = ".JTimer_Screen_Time"; // Saved screen time between program runs (date, sum)
        Timer timer = new Timer();

        // Init jingle list to play in the end of a session
        ArrayList<String> files = new ArrayList<>();
        initJingles(files, folderName);

        parseFlags(args, timer);

        // If input in flags, skip- else print info and get user input
        if (timer.minutesInputMissing)
            printInfo();

        // Get user input for volume commands or to start the program
        Scanner in = new Scanner(System.in);
        try {
            while (timer.minutesInputMissing) {
                String input = in.nextLine();
                boolean skipParse = false;
                if (input.toLowerCase().contains("lower")) {
                    prettyPrint("Lowered volume");
                    volume -= 10;
                    skipParse = true;
                }
                if (input.toLowerCase().contains("undo")) {
                    prettyPrint("Undid lowering");
                    volume = Math.min(0, volume + 10);
                    skipParse = true;
                }
                if (input.toLowerCase().contains("test")) {
                    prettyPrint("Playing ");
                    playRandom(files, volume);
                    skipParse = true;
                }
                if (input.toLowerCase().contains("notify")) {
                    notification = !notification;
                    prettyPrint("Notification Active: " + notification);
                    skipParse = true;
                }
                if (skipParse)
                    continue;
                if (isNumber(input)) {
                    timer.addedMinutes(Integer.parseInt(input));
                } else {
                    prettyPrint("Enter a valid number \\ command");
                    // Continue loop
                }
            }

            // Print warning if entered a large amount
            if (timer.getMinutes() > 60) {
                prettyPrint("You have entered more than an hour (" + timer.getMinutes()
                        + " min), are you sure? press enter to continue");
                listenForInput(in, timer);
            }

            // Try to read from file accumulated minutes
            // If earlier date, reset
            parseLogFile(textFileName);

            // Notification when it is time to rest
            Notifier notifier = new Notifier(notification, "JTimer:", "Time for a break");

            // Main loop -
            // 1 print to console each minute
            // 2 play a jingle at the end
            // 3 wait for enter
            while (true) {
                consoleClear();
                countdown(timer.getMinutes(), textFileName);

                // Countdown over
                notifier.notifyUser();
                playRandom(files, volume);
                // flush(in);
                flush();
                System.out.println("Stopped at:");
                System.out.println(Calendar.getInstance().getTime());
                System.out.println("Overall Screen Time: " + getScreenTimeSumString());
                System.out.println("Put a number to modify time");
                System.out.println("Otherwise, press enter to restart");

                // Awaiting user
                listenForInput(in, timer);
                notifier.notifyRemove();
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Main appInstance = new Main();
        appInstance.run(args);
    }

    private class Timer {
        protected boolean minutesInputMissing = true; // Did the program start without minutes as a flag
        private int minutes = -1; // Minute amount between each session

        private void setMinutes(int minutes) {
            if (minutes < 0)
                minutes = -minutes;
            this.minutes = minutes;
        }

        public int getMinutes() {
            return this.minutes;
        }

        public void addedMinutes(int minutes) {
            setMinutes(minutes);
            minutesInputMissing = false;
        }

    }

    // general helpers
    private static boolean isNumber(String flag) {
        try {
            Integer.parseInt(flag);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void parseFlags(String[] flags, Timer timer) {
        for (String flag : flags) {
            if (isNumber(flag)) {
                timer.addedMinutes(Integer.parseInt(flag));
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
            }
        }
    }

    private static void countdown(int minutes, String filename) throws InterruptedException {
        for (int i = minutes; i > 0; i--) {
            System.out.println(i + " minutes left");
            Thread.sleep(SEC_IN_MINUTE * MILLI_TO_SEC);
            screenTimeSumMinutes++;
            writeLogFile(filename, screenTimeSumMinutes);
        }
    }

    private static String getScreenTimeSumString() {
        int screenTime = screenTimeSumMinutes;
        int hours = 0, minutes;
        String ans = "";
        while (screenTime >= 60) {
            screenTime = screenTime - 60;
            hours++;
        }
        minutes = screenTime;
        ans += hours > 9 ? hours : "0" + hours;
        ans += ":";
        ans += minutes > 9 ? minutes : "0" + minutes;
        return ans;
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

    private static void prettyPrint(String toPrint) throws InterruptedException {
        for (int i = 0; i < toPrint.length(); i++) {
            System.out.print(toPrint.charAt(i));
            Thread.sleep(2);
        }
        System.out.println();
    }

    // audio helpers
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