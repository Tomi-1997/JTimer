import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Schedule {
    public record Task(String label, int time) {
        public void info() {
            System.out.println("Task <" + label + ">: " + time + " min");
        }
    }

    private boolean repeat;
    private String title;
    private ArrayList<Task> taskCollection;

    public Schedule() {
        this.taskCollection = new ArrayList<>();
        this.repeat = false; // no repeatition by default
        this.title = "";
    }

    public Schedule(String filename) {
        this();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            this.proccessJson(builder.toString());
        } catch (FileNotFoundException e) {
            System.out.println("File not found");;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRepeatition(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isRepeat() {
        return this.repeat;
    }

    public void addNewTask(String label, int time) {
        Task task = new Task(label, time);
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

    public void printScheduleInfo() {
        System.out.println("Schedule " + this.title);
        System.out.println("Repeat Enabled: " + this.repeat);
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void proccessJson(String jsonText) {
        // process the outer object
        JSONObject base = new JSONObject(jsonText);
        this.setRepeatition(base.getBoolean("repeat"));
        this.setTitle(base.getString("title"));

        // process the inner list
        JSONArray taskArray = base.getJSONArray("tasks");
        for (int i = 0; i < taskArray.length(); i++) {
            JSONObject taskObject = taskArray.getJSONObject(i);
            String label = taskObject.getString("label");
            int time = taskObject.getInt("time");
            this.addNewTask(label, time);
        }
    }
}