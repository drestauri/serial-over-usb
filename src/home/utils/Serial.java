package home.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.util.Enumeration;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;


// More info on this code:
// https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example

// SETUP:
// Get the jSerialComm JAR from:
//	https://fazecast.github.io/jSerialComm/
// Add it to the build path of the project and all should work ok.
// I read some stuff only works on windows due to the way Linux does serial comm so
// this may only work on windows

public class Serial implements SerialPortDataListener{
	private SerialPort serialPort = null;
	
	private char[] dataBuffer = new char[8];
	private int bufIndex = 0;
	
	/*private static final String PORT_NAMES[] = {
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
			};*/
	private static String PORT_NAME;

	private BufferedReader input;
	//private static OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;
	
	private int lastVal = 0;
	private boolean isDataAvail = false;

	public void initialize() {
		// Get a list of the current ports on this machine
		SerialPort[] ports = SerialPort.getCommPorts();
		
		// Check each port for a match to our expected ports list PORT_NAMES
		System.out.println("Found Ports:");
		for (SerialPort port : ports) 
		{
			String currPortId = port.getSystemPortName();
			System.out.println(currPortId);
			
			/*for (String portName : PORT_NAMES)
			{
				if (currPortId.equals(portName))
				{
					serialPort = port;
					break;
				}
			}*/
			
			if (currPortId.equals(PORT_NAME))
			{
				serialPort = port;
				break;
			}
		}
		
		
		// If we couldn't find a matching port
		if (serialPort == null) {
			System.out.println("Could not find desired COM port.");
			return;
		}

		// Open the port and set the comm parameters, get the input and output streams
		try 
		{
			System.out.print("Attempting to open port: ");
			System.out.println(serialPort.getSystemPortName());
			if(serialPort.openPort())
			{
			System.out.println("Port opened successfully.");
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			}
			else {
				System.out.println("Unable to open the port.");
			}
		            
			serialPort.setComPortParameters(DATA_RATE, 8, 1, SerialPort.NO_PARITY);
			serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
		
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			//output = serialPort.getOutputStream();
		
			serialPort.addDataListener(this);
			// TODO: Comment this out to see if necessary
			serialPort.notifyAll();
			//serialPort.notifyOnDataAvailable(true);
		}
		catch (Exception e) {
			System.err.print("Error 1 (this always gets called once for some reason):");
			System.err.println(e.toString());
		}
	}

	@Override
	public int getListeningEvents() 
	{
		// LISTENING_EVENT_DATA_AVAILABLE = any data has been received
		// LISTENING_EVENT_DATA_RECEIVED = data read from serial port
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}
  
	// Callback? for if data is on the serial port
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		// LISTENING_EVENT_DATA_AVAILABLE = any data has been received
		// LISTENING_EVENT_DATA_RECEIVED = data read from serial port
		if(oEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
			return;

		processDataAvailable();
		//processDataReceived(oEvent.getReceivedData());
	}
   
	private void processDataAvailable()
	{
		// Create a buffer the size of the bytes available
		byte[] newData = new byte[serialPort.bytesAvailable()];
		// Fill the buffer with data off the serial bus
		int numRead = serialPort.readBytes(newData, newData.length);

		// Step through each character in the newData buffer
		for (int i = 0; i < newData.length; ++i)
		{
			// Check the value as character
			char c = (char)newData[i];

			// Values like 251 come across as '2', '5', '1', 13, 10 where 13 & 10 are the integer values 2 white space values
			// If we see a number character
			if(c >= '0' && c <= '9')
			{
				// put the character in the string
				dataBuffer[bufIndex] = c;
				bufIndex++; 
			}
			else
			{
				//TODO: If we had some non-newline characters (bufIndex>0) and now see a newline, convert the buffer to a value and reset
				// For now, it just prints the buffer
				if(bufIndex>0)
				{
					for(int j=0;j<bufIndex;j++)
					{
						System.out.print(dataBuffer[j]);
					}

					//System.out.println(", buffer size: " + (bufIndex-1) + "; last char: " + (int)c);
					System.out.println();
				}

				// set the buf index to point to the 1st char
				bufIndex = 0;
			}
		}
	}

	private void processDataReceived(byte[] newData)
	{ 
		for (int i = 0; i < newData.length; ++i)
			System.out.print((char)newData[i]);
		System.out.println("\n");
	}
  
	// CLose the serial port
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeDataListener();
			serialPort.closePort();
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

	// Constructor. Takes a string if there's a specific comm port to use
	public Serial(String com){
		//if(ncom>3 && ncom<=9)
			//PORT_NAMES[2] = "COM" + Integer.toString(ncom);
		PORT_NAME = com;
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