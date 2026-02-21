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
    "title": "Pomodoro",
    "tasks": [
        {
            "label": "Work",
            "time": 20
        },
        {
            "label": "Break",
            "time": 5
        }
    ]
}
```
During a schedule run you can see the current task label and for how long it would run.<br>
When a task ends, press enter to continue to the next scheduled timer.
> It is important to label the tasks properly - In a way *you* can understand.

Screentime is accumalated during a schedule plan too.
For more of a complex plan see [below.](#complex-schedule-plan)

> Note: You can use all flags with a plan. However when putting in a number the plan is prioritised.

## Commands and Flags
You can start the program with most flags, once on info screen you can put either the flag or verbose as a command.

|Flags  |Verbose|Description|
|-----  |-------|-----------|
|-p     |plan   | Start a planned session with given JSON file|
|-t     |test   |Test current volume|
|-u     |undo   |Undo volume change (only on info screen)|
|-i     |info   |See usage examples (only on info screen)|
|-l     |lower  |Lower volume|
|-L     |Lower  |Much lower volume (only as argument)|
|-n     |notify |Disable/Enable notification bar (Enabled on default)|

## More Examples
### Regular Usage

```bash
# To start immediately a timer on 20 minutes
java -jar JTimer.jar 20 

# To start lower, without any more prompts
java -jar JTimer.jar -l 20 

# To test volume upon prompting immediately, but without a timer yet
java -jar JTimer.jar -t

# start a 10 min timer, without notification bar, on much lower sound, and test volume
java -jar JTimer.jar -L -n -t 10 # -L equals to -l twice
```

### Complex Schedule Plan
Based on [The minimalist drawing plan](https://www.youtube.com/watch?v=HLzs_8kgaAY)

It is less than an hour long schedule for a warm up before drawing. <br>
The tasks (simplified for the sake of the example):

- 3 min circles in perspective.

- 3 min parallel lines and curves

- 10 min cube and cylinder rotation.

- 10 min form manipulation.

- 10 min figure construction. 

- 10 min figure as silhouette and organic forms.

- 10 min self-critique.

Since it's a one time routine, there is no need to enable repeatition.

```JSON
{
    "repeat": false,
    "title": "The Minimalist Drawing Plan",
    "tasks": [
        {
            "label": "circles in perspective",
            "time": 3
        },
        {
            "label": "parallel lines and curves",
            "time": 3
        },
        {
            "label": "cube and celinder rotation",
            "time": 10
        },
        {
            "label": "form manipulation",
            "time": 10
        },
        {
            "label": "figure construction",
            "time": 10
        },
        {
            "label": "figure as silhouette and organic forms",
            "time": 10
        },
        {
            "label": "self-critique",
            "time": 10
        }
    ]
}
```