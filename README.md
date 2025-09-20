# JTimer
JTimer is a productivity java console application to moderate your screen time.
## Usage
1. Download JTimer.jar from releases. <br>
2. Open command line in the same folder. <br>
3. Type
   ```
   java -jar JTimer.jar
   ```
4. Type a number, or a command from the list below.

<br><br>
In a bit more detail: <br>
You set a certain time limit, for example 20 minutes. <br>
Every 20 minutes a jingle will play, indicating for a break. <br>
Once you are back, press enter to reset the timer. <br>
<br>
Screen time is accumulated and logged to a local file which stores the current date and screen time. <br>
If a different date is found, screen time is reset. <br>
## Flags
-l to lower volume
```bash
java -jar JTimer.jar -l
```
-L to lower it dramatically
```bash
java -jar JTimer.jar -L
```
A number to begin timer immediately
```bash
java -jar JTimer.jar 20
```
## Commands
Once you run the program, you can enter the number of minutes to begin, or:
- 'test' to check the volume
- 'lower' to decrease volume
- 'undo' to cancel previous 'lower' command
- 'notify' to cancel or activate notification bar