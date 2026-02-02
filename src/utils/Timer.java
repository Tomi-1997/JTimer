package utils;

public class Timer {
    public boolean minutesInputMissing = true; // Did the program start without minutes as a flag
    private int minutes = -1; // Minute amount between each session

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
}
