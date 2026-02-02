package utils;

public class Helpers {
    
    // general helpers
    public static boolean isNumber(String flag) {
        try {
            Integer.parseInt(flag);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getScreenTimeSumString(int screenTimeSumMinutes) {
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

}