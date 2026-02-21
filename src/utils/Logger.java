package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Logger {
    static String currentDate = SimpleDateFormat.getDateInstance().format(Calendar.getInstance().getTime());

    public static void parseLogFile(String filename, Timer timer) {
        Path file = Paths.get(filename);
        try {
            List<String> s = Files.readAllLines(file);
            String fileDate = s.get(0);

            // Different day, don't load data
            if (fileDate.compareTo(currentDate) != 0) {
                return;
            }

            // Parse accumulated minutes to int
            int screenTime = Integer.parseInt(s.get(1));
            timer.setScreenTime(screenTime);
            
        } catch (IOException ignored) {

        }
    }

    public static void writeLogFile(String filename, int minutes) {
        try {
            PrintWriter writer = new PrintWriter(filename, StandardCharsets.UTF_8);
            writer.println(currentDate);
            writer.println(minutes);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
