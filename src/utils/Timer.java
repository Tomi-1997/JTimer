package utils;

public class Timer {
    public boolean minutesInputMissing = true; // Did the program start without minutes as a flag
    private int minutes = -1; // Minute amount between each session
    private static int screenTimeSumMinutes = 0;

    public void setMinutes(int minutes) {
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

    public int getScreenTime(){
        return screenTimeSumMinutes;
    }

    public void updateScreenTime(){
        screenTimeSumMinutes++;
    }

    public void setScreenTime(int num){
        screenTimeSumMinutes = num;
    }

    public String getScreenTimeSumString() {
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
