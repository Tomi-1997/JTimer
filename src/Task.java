public class Task {
    private String mission;
    private int time;

    public Task(String mission, int time){
        this.time = time;
        this.mission = mission;
    }

    public int getTime(){
        return this.time;
    }

    public String getMission(){
        return this.mission;
    }
}

