import java.util.HashMap;
import static utils.Helpers.isNumber;

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
        flagMap.put("L", new Flag("Lower", "Much lower volume (only as argument)"));
        flagMap.put("u", new Flag("undo", "Undo volume change (only on info screen)"));
        flagMap.put("t", new Flag("test", "Test current volume (only on info screen)"));
        flagMap.put("n", new Flag("notify", "Disable/Enable notification when timer ends (Enabled on default)"));
        flagMap.put("p", new Flag("plan", "Start a planned session with given JSON file (-p <path to json>) "));
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

    public String parseArgs(String arg, boolean isFlag) {
        if (arg == null || arg.strip().isEmpty()) {
            return null;
        }
        if(isNumber(arg)) return "#";
        if(isFlag){
            // strip '-'
            if(arg.charAt(0)!= '-'){
                return null;
            }
            if (arg.length() != 2){
                return null;
            }
            arg = arg.replaceFirst("-", "");
        }
        String key = arg.substring(0, 1);
        if (flagMap.containsKey(key)) {
            if (arg.length() == 1)
                return key;
            Flag value = flagMap.get(key);
            if (value.longArg.equals(arg)) {
                return key;
            }
            //two part argumemt
            if (arg.split(" ", 2)[0].equals(value.longArg)){
                return key;
            }
        }
        return null;
    }

    public String parseFilename(String arg){
        String[] parse = arg.split(" ", 2);
        if (parse.length < 2){
            return null;
        }
        String ans = parse[1];
        if(ans.isBlank()){
            return null;
        }
        return parse[1];
    }
}
