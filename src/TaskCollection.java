import java.util.ArrayList;

public class TaskCollection {
    boolean repeat;
    private ArrayList<Task> taskCollection;

    public TaskCollection(){
        this.taskCollection = new ArrayList<>();
        this.repeat = false; // no repeatition by default
    }

    public void setRepeatition(boolean repeat){
        this.repeat = repeat;
    }

    public boolean isRepeat(){
        return this.repeat;
    }

    public void addNewTask(String mission, int time){
        Task task = new Task(mission, time);
        taskCollection.add(task);
    }

    public int getSize(){
        return this.taskCollection.size();
    }

    public Task getTaskAt(int index){
        if(this.taskCollection.isEmpty()) {
            return null;
        }
        if(index < 0 || index >= this.taskCollection.size()){
            return null;
        }
        Task task = this.taskCollection.get(index);
        return task;
    }
}