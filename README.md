# JTimer

JTimer is a productivity java console application to moderate your screen time.

## Usage

1. Download JTimer.jar from releases. <br>
2. Open command line in the same folder. <br>
3. Type
   ```
   java -jar JTimer.jar
   ```
4. An info screen will show up. Type a number, or a command from the list [below](#commands-and-flags).

### The Way It Works
You set a certain time limit, for example 20 minutes. <br>
Every 20 minutes a jingle will play, indicating for a break.
Once you are back, press enter to reset the timer. 

> It is possible to change the minutes between sessions by typing a number instead.

Screen time is accumulated and logged to a local file which stores the current date and screen time. <br>
If a different date is found, screen time is reset. <br>

## Advanced Usage
You may create a structured plan in a json file. <br>

```bash
   java -jar JTimer.jar -p [file]
```

Let's say we want a simple pomodoro regime consists of 20 minutes of work followed by a break of 5 minutes. <br>
Instead of repeatedly changing the one timer we have, we can create a schedule in JSON like so:

```JSON
{
    "repeat": true,
    "scheduleTitle": "Pomodoro",
    "TaskList": [
        {
            "taskTitle": "Work",
            "time": 20
        },
        {
            "taskTitle": "Break",
            "time": 5
        }
    ]
}
```

Then press enter to move up to the next scheduled timer.

> Note: You can use all flags with a plan. However when putting in a number the plan is prioritised.

Screentime is accumalated during a schedule plan too.

## Commands and Flags
You can start the program with most flags, once on info screen you can put either the flag or verbose as a command.

|Flags  |Verbose|Description|
|-----  |-------|-----------|
|-p     |plan   | Start a planned session with given JSON file|
|-t     |test   |Test current volume|
|-u     |undo   |Undo volume change (only on info screen)|
|-l     |lower  |Lower volume|
|-L     |Lower  |Much lower volume (only as argument)|
|-n     |notify |Disable/Enable notification bar (Enabled on default)|
