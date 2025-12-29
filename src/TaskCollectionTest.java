import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaskCollectionTest {
    // tests tasks and the collection behaviour
    String taskMission = "Mission";
    int taskTime = 10;
    Task basicTask = new Task(taskMission, taskTime);
    TaskCollection collection = new TaskCollection();

    @Test
    public void testTask(){
        assertEquals("Task time should be equal", taskTime, basicTask.getTime());
        assertEquals("Task missions should be equal",taskMission, basicTask.getMission()); 
    }
    
    @Test
    public void testInit(){
        assertEquals("Collection should be empty on init", 0, collection.getSize());
        assertFalse("Collection does not repeat on default", collection.isRepeat());
    }

    @Test
    public void testTaskAddition() {
        collection.addNewTask(taskMission, taskTime);
        assertEquals("Collection should have one task",1,collection.getSize());
        Task temp = collection.getTaskAt(0);
        assertEquals("Task times should be same",taskTime, temp.getTime());
        assertEquals("Task mission should be same",taskMission, temp.getMission());
    }

    @Test
    public void testGetTaskAt() {
        Task temp = collection.getTaskAt(0);
        assertNull("On empty collection, should get null",temp);
        collection.addNewTask(taskMission, taskTime);
        temp = collection.getTaskAt(0);
        assertNotNull("Must have an element at this point", temp);
        temp = collection.getTaskAt(-1);
        assertNull("On negative index, should get null",temp);
        temp = collection.getTaskAt(1);
        assertNull("On out of bounds, should get null",temp);
       
    }

    @Test
    public void testRepeat() {
        collection.setRepeatition(true);
        assertTrue("Collection repeatition is set on true",collection.isRepeat());
        collection.setRepeatition(false);
        assertFalse("Collection repeatition is set on false",collection.isRepeat());
    }
}
