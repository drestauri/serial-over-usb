# serial-over-usb
Basic framework for receiving data over USB in Java. Used for receiving from Arduino.

This project was created as a starting point for reading serial data from an Arduino
to a RaspberryPi. As of 3/12/2020 this has only been confirmed to read the Arduino
data in via Windows PC.

## Installation

Download and add the Serial class to your project. 

Google and download RXTX for Java (RXTXcomm.jar). This download comes with a Jar and 2 .dll files. Per the included
install instructions with those, put the jar in <JAVA_HOME>\jre\lib\ext & the dlls in <JAVA_HOME>\jre\bin. In Eclipse/STS4,
go to Project -> properties -> Java build path -> Libraries -> Add External Jars. Find your RXTX jar. Expand the JAR's
details. Select Native library location and edit it to point to RXTX's shared library location (\bin folder above)

Some discussion use to help setup this code:
	https://stackoverflow.com/questions/15996345/java-arduino-read-data-from-the-serial-port

## Usage

Of course, make sure your DATA_RATE matches the Baud rate of your Arduino or whatever device your using. In my case,
it was 9600. I didn't write to my Arduino for my project so I commented "output" out. 
Note that about 1 in 5 data points from my Arduino triggered the exception violation so I just commented that out as well.
It didn't make anything crash, just made my output window a little busy so use it to your delight.

Once all the dependencies have been figured out for the Serial class, usage is pretty straight forward:

```java
impot home.utils.Serial

Serial serial = new Serial(3); // the number refers to the comm port, e.g. COM3 which is typical for Arduino on PC
```

## Contributing
This code will not be changed as it's used a template and will serve as the base for other/future projects. 
