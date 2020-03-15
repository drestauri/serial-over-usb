# serial-over-usb
Basic framework for receiving serial data over USB in Java.

This project was created as a starting point for reading serial data from an Arduino
to a RaspberryPi. This has been confirmed to work on both a Windows PC and RaspberryPi.

## Installation

Download and add this Serial class to your project. 

Try the link below to download jSerialComm for Java (or google it):
```
https://fazecast.github.io/jSerialComm/
```
Add to build path for your project. I usually create a lib folder in my project and copy the
JAR file there. Then in Eclipse/STS4, right click the JAR -> build path -> add to build path.


Some discussion I used to help setup this code: 
```
https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example
```

## Usage
Of course, make sure your DATA_RATE matches the Baud rate of your Arduino or whatever device your using. In my case,
it was 9600. I didn't write to my Arduino for my project so I commented "output" out. 
Note that about 1 in 5 data points from my Arduino triggered the exception violation so I just commented that out as well.
It didn't make anything crash, just made my output window a little busy so use it to your delight.

Once all the dependencies have been figured out for the Serial class, usage is pretty straight forward:

```java
import home.utils.Serial

Serial serial = new Serial(args[0]);
```

Then once exported to a JAR it can be run from the command line as follows:
```
java -jar serial-over-usb COM3
```

## Anticipated Updates
1. Convert the data in the buffer to an actual value instead of just printing the buffer and resetting
2. Add framework to allow a user to get the last value in integer format
3. Improve the logic to handle all characters (currently optimized for numbers only)
4. Implement the output stream to the Arduino

## Contributing
This code will not be changed much once the above updates are implemented as it's used a template and will serve as
a baseline for future projects. 
