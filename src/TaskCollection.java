import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class TaskCollection {
    public record Task(String name, int time) {
    }

    boolean repeat;
    private ArrayList<Task> taskCollection;

    public TaskCollection() {
        this.taskCollection = new ArrayList<>();
        this.repeat = false; // no repeatition by default
    }

    public void setRepeatition(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isRepeat() {
        return this.repeat;
    }

    public void addNewTask(String name, int time) {
        Task task = new Task(name, time);
        taskCollection.add(task);
    }

    public int getSize() {
        return this.taskCollection.size();
    }

    public Task getTaskAt(int index) {
        if (this.taskCollection.isEmpty()) {
            return null;
        }
        if (index < 0 || index >= this.taskCollection.size()) {
            return null;
        }
        Task task = this.taskCollection.get(index);
        return task;
    }
    
    public TaskCollection (String filename) {
        this();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            this.proccessJson(builder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void proccessJson(String jsonText) {
        // process the outer object
        JSONObject base = new JSONObject(jsonText);
        boolean repeat = base.getBoolean("Repeat");
        this.setRepeatition(repeat);

        // process the inner list
        JSONArray taskArray = base.getJSONArray("TaskList");
        for (int i = 0; i < taskArray.length(); i++) {
            JSONObject taskObject = taskArray.getJSONObject(i);
            String name = taskObject.getString("name");
            int time = taskObject.getInt("time");
            this.addNewTask(name, time);
        }
    }
}