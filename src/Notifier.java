import java.awt.*;

public class Notifier {
    private final boolean notify;
    private final String header;
    private final String paragraph;
    private TrayIcon trayIcon;

    public Notifier(boolean notify, String header, String paragraph) {
        this.notify = notify;
        this.header = header;
        this.paragraph = paragraph;
    }

    public void notifyUser() {
        if (!isNotifiable()) {
            return;
        }
        trayIcon.displayMessage(this.header, this.paragraph, TrayIcon.MessageType.NONE);
    }

    public void notifyUserCustom(String header, String paragraph) {
        if (!isNotifiable()) {
            return;
        }
        trayIcon.displayMessage(this.header + header, paragraph, TrayIcon.MessageType.NONE);
    }

    public void notifyRemove() {
        SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
    }

    private boolean isNotifiable() {
        if (!notify) {
            return false;
        }

        if (!SystemTray.isSupported()) {
            return false;
        }

        // Create a tray icon
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(""), "Attention Grabber");
        trayIcon.setImageAutoSize(true);

        // Add the tray icon to the system tray
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (AWTException e) {
            return false;
        }

        return true;
    }
}
