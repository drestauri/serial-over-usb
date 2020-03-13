

package home.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

// More info on the source of my code:
// https://stackoverflow.com/questions/15996345/java-arduino-read-data-from-the-serial-port

// SETUP:
// Get RXTX for Java (RXTXcomm.jar). Google it.
// Comes with a Jar and 2 .dll files
// Per the install instructions, put the jar in <JAVA_HOME>\jre\lib\ext & the dlls in \jre\bin.
// In Eclipse, Go to Project -> properties -> Java build path -> Libraries -> Add External Jars. Find your RXTX jar
// Expand the JAR's details. Select Native library location and edit it to point to RXTX's shared library location.

// Of course, make sure your DATA_RATE matches the Baud rate of your Arduino or whatever. In my case, 9600
// I didn't write to my Arduino for my project so I commented "output" out. 
// Note about 1 in 5 data points from my Arduino triggered the exception violation so I just ignore that. It didn't make
// anything crash, just make my output window a little busy.

public class Serial implements SerialPortEventListener {
    SerialPort serialPort;

    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyUSB0", // Linux
            "COM3", // Windows
    };

    private BufferedReader input;
    //private static OutputStream output;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;
    
    private int lastVal = 0;
    private boolean isDataAvail = false;

    public void initialize() {
        CommPortIdentifier portId = null;
        
        // Get a list of the current ports on this machine
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        // Check each port for a match to our expected ports list PORT_NAMES
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        
        // If we couldn't find a matching port
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        // Open the port and set the comm parameters, get the input and output streams
        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);

            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            //output = serialPort.getOutputStream();

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (Exception e) {
        	System.err.print("Error 1:");
            System.err.println(e.toString());
        }
    }

    
    // Callback? for if data is on the serial port
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine=input.readLine();
                // Make sure the string is an integer and convert it
                lastVal = convertToInt(inputLine);
                // In case it's not an integer, set the data avail flag to false to make
                // sure -1 isn't accidentally used
                if (lastVal > 0)
                	isDataAvail = true;
                else
                	isDataAvail = false;
                System.out.println(lastVal);
            } catch (Exception e) {
            	// This error prints often, about every 5 times
            	//System.err.print("Error 2:");
                //System.err.println(e.toString());
            }
        }
    }

    
    // CLose the serial port
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
    
    private int convertToInt(String s)
    {
    	boolean isNumeralsOnly = true;
    	for (int i = 0; i<s.length();i++)
    	{
    		if(s.charAt(i)<'0' || s.charAt(i)>'9')
    		{
    			System.out.print("Found in response: ");
    			System.out.println(s.charAt(i));
    			isNumeralsOnly = false;
    		}
    	}
    	if (isNumeralsOnly)
    		return Integer.parseInt(s);
    	else
    		return -1;
    }

    // Constructor. Can take a number 3 to 9 if there's a specific comm port to use
    public Serial(int ncom){
    	
        if(ncom>=3 && ncom<=9)
            PORT_NAMES[2] = "COM" + Integer.toString(ncom);
        initialize();
        Thread t=new Thread() {
            public void run() {
                try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
            }
        };
        t.start();
        System.out.println("Serial Comms Started");
    }
    
    public boolean dataAvail()
    {
    	return true;
    }
}
