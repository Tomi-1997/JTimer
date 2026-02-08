import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ScheduleTest {
    // tests tasks and the collection behaviour
    private Schedule collection;

    @Before
    public void setUp() {
        collection = new Schedule();
    }
    
    @Test
    public void testInit(){
        assertEquals("Collection should be empty on init", 0, collection.getSize());
        assertFalse("Collection does not repeat on default", collection.isRepeat());
    }

    @Test
    public void testTaskAddition() {
        collection.addNewTask("mission", 10);
        assertEquals("Collection should have one task",1,collection.getSize());
        Schedule.Task temp = collection.getTaskAt(0);
        assertEquals("Task times should be same",10, temp.time());
        assertEquals("Task mission should be same","mission", temp.name());
    }

    @Test
    public void testGetTaskAt() {
        Schedule.Task temp = collection.getTaskAt(0);
        assertNull("On empty collection, should get null",temp);
        collection.addNewTask("mission", 10);
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

    @Test
    public void CreateFromFile(){
        collection = new Schedule("simplePlan.json");                      
        assertEquals("Supposed to be three tasks now",3, collection.getSize());
        assertTrue("Json is set to repeat", collection.isRepeat());
        Schedule.Task temp;       
        String taskname;
        for (int i = 0; i < collection.getSize(); i++){
            temp = collection.getTaskAt(i);
            taskname = "Task " + (i+1);
            assertEquals(taskname, temp.name());
            assertEquals(10, temp.time());
        }
    }
}
