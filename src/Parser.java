import java.util.HashMap;

public class Parser {
    public record Flag(String longArg, String desc) {
    }

    HashMap<String, Flag> flagMap;

    public Parser() {
        flagMap = new HashMap<>();
        initFlags();
    }

    private void initFlags() {
        flagMap.put("l", new Flag("lower", "Lower volume"));
        flagMap.put("L", new Flag("lower lower", "Much lower volume (only as argument)"));
        flagMap.put("u", new Flag("undo", "Undo volume change (only on info screen)"));
        flagMap.put("t", new Flag("test", "Test current volume"));
        flagMap.put("n", new Flag("notify", "Disable/Enable notification when timer ends (Enabled on default)"));
        flagMap.put("p", new Flag("plan", "Start a planned session with given JSON file"));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        flagMap.forEach((k, f) -> {
            builder.append(("-" + f));
            builder.append(f.longArg());
            builder.append(f.desc());
            builder.append("\n");
        });
        return builder.toString();
    }

    public String parseCommand(String cmd) {
        if (cmd == null || cmd.isEmpty()) {
            return null;
        }
        if(isNumber(cmd)) return "#";

        String key = cmd.substring(0, 1);
        if (flagMap.containsKey(key)) {
            if (cmd.length() == 1)
                return key;
            Flag value = flagMap.get(key);
            if (value.longArg.equals(cmd)) {
                return key;
            }
        }
        return null;
    }

    private static boolean isNumber(String flag) {
        try {
            Integer.parseInt(flag);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
