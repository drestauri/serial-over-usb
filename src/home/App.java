package home;

import home.utils.Serial;

public class App {
	
	public static void main(String[] args) throws Exception
	{
		if (args.length != 1)
		{
			System.out.println("usage: java -jar serial-over-usb.jar <COMM_PORT>\n");
			System.out.println("== NOTE ==");
			System.out.println(" On raspberry pi, use 'ls /dev/*tty*' to list ports with");
			System.out.println(" your device plugged and then not to identify which name changes");
			System.exit(-1); // Comment this out during dev
		}
		
		// Switch these out during dev
		Serial serial = new Serial(args[0]);
		//Serial serial = new Serial("COM3");
	}
}
