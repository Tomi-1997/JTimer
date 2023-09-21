# JTimer
JTimer is a small java console code \ script to remind yourself to take a break.
## Usage
Set a certain time limit, e.g 20 minutes. <br>
Every 20 minutes a jingle will play, indicating for a break. <br>
Once you are back, press enter to reset the timer. <br>
## Flags
```bash
java -jar JTimer.jar
```
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
## Input
Once you run the program, you can enter the number of minutes to begin, or:
- 'test' to check the volume
- 'lower' to decrease volume
- 'undo' to cancel previous 'lower' command
