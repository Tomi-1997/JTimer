import java.awt.*;

public class Notifier
{
    private final boolean notify;
    private final String h;
    private final String p;
    private TrayIcon trayIcon;


    public Notifier(boolean notify, String h, String p)
    {
        this.notify = notify;
        this.h = h;
        this.p = p;
    }


    public void notifyUser()
    {
        if (!notify) return;

        // Check if the system supports the tray
        if (!SystemTray.isSupported())
        {
            return;
        }

        // Create a tray icon
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(""), "Attention Grabber");
        trayIcon.setImageAutoSize(true);

        // Add the tray icon to the system tray
        try
        {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (AWTException e)
        {
            return;
        }

        // Show a notification
        trayIcon.displayMessage(this.h, this.p, TrayIcon.MessageType.NONE);
    }

    public void notifyRemove()
    {
        SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
    }
}
