package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.CopyOnWriteArrayList;

import gnu.io.*;

public class PortHandler {
	
    private byte[] readBuffer = new byte[400];
    public static List<String> outcomes;
    
        
        public PortHandler(List<String> s,SimpleDateFormat datef) throws FileNotFoundException, UnsupportedEncodingException
        {
       outcomes = s;
       standardDate = datef;
        	
        }
    
  
	
	 private SerialPort serialPort;
	    private OutputStream outStream;
	    private InputStream inStream;
	    private SimpleDateFormat standardDate;
	 
	 
	    
	    public void connect(String portName) throws IOException {
	        try {
	            // Obtain a CommPortIdentifier object for the port you want to open
	            CommPortIdentifier portId =
	                    CommPortIdentifier.getPortIdentifier(portName);
	 
	            // Get the port's ownership
	            serialPort =
	                    (SerialPort) portId.open("Demo application", 5000);
	 
	            // Set the parameters of the connection.
	            int baudRate = 9600; // 57600bps
	            try {
	              // Set serial port to 57600bps-8N1..my favourite
	              serialPort.setSerialPortParams(
	                baudRate,
	                SerialPort.DATABITS_8,
	                SerialPort.STOPBITS_1,
	                SerialPort.PARITY_NONE);
	            } catch (UnsupportedCommOperationException ex) {
	              System.err.println(ex.getMessage());
	            }
	 
	            // Open the input and output streams for the connection. If they won't
	            // open, close the port before throwing an exception.
	            outStream = serialPort.getOutputStream();
	            inStream = serialPort.getInputStream();
	            
	            
	            
	        } catch (NoSuchPortException e) {
	            throw new IOException(e.getMessage());
	        } catch (PortInUseException e) {
	            throw new IOException(e.getMessage());
	        } catch (IOException e) {
	            serialPort.close();
	            throw e;
	        }
	    }
	
	
	
	    public InputStream getSerialInputStream() {
	        return inStream;
	    }
	 
	    /**
	     * Get the serial port output stream
	     * @return The serial port output stream
	     */
	    public OutputStream getSerialOutputStream() {
	        return outStream;
	    }

	    
	    private class SerialEventHandler implements SerialPortEventListener {
	        public void serialEvent(SerialPortEvent event) {
	            switch (event.getEventType()) {
	                case SerialPortEvent.DATA_AVAILABLE:
	                    readSerial();
	                    break;
	            }
	        }
	    }
	    
	    private class ReadThread implements Runnable {
	    	
	        private volatile boolean running = true;

	    	
	        public void run(){
	        	this.running=true;
	        	while(this.running) {
	               // readSerial();
	          try
	          {
	        	  	Thread.sleep(150);
	            	 readSerial();
	          }
	          catch (InterruptedException e)
	          {
	        	outcomes.retainAll(null);
	          }
	        
	          
	            }
	        }
	        
	    }
	    
	
	    private void setSerialEventHandler(SerialPort serialPort) {
	        try {
	            // Add the serial port event listener
	            serialPort.addEventListener(new SerialEventHandler());
	            serialPort.notifyOnDataAvailable(true);
	        } catch (TooManyListenersException ex) {
	            System.err.println(ex.getMessage());
	        }
	    }

	    private String readSerial() {
	        try {
	            int availableBytes = inStream.available();
	            if (availableBytes > 0) {
	                // Read the serial port
	                inStream.read(readBuffer, 0, availableBytes);
	     
	                // Print it out
	               String s = new String(readBuffer, 0, availableBytes);
	               outcomes.add(standardDate.format(new Date(System.currentTimeMillis())).toString() + "\n");
	               outcomes.add(s);


	                      return  s;
	            }
	        } catch (IOException e) {
	        }
			return null;
	    }
	
	    public Thread setSerialListener() {
	        return new Thread(new ReadThread());
	    }
	
	
	
	
	
   public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
       HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
       Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
       while (thePorts.hasMoreElements()) {
           CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
           switch (com.getPortType()) {
           case CommPortIdentifier.PORT_SERIAL:
               try {
                   CommPort thePort = com.open("CommUtil", 50);
                   thePort.close();
                   h.add(com);
               } catch (PortInUseException e) {
                   System.out.println("Port, "  + com.getName() + ", is in use.");
                   h.add(com);

               } catch (Exception e) {
                   System.err.println("Failed to open port " +  com.getName());
                   e.printStackTrace();
               }
           }
       }
       return h;
   }
	

    static void listPorts()
    {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        }        
    }
    
    
    static int getPortNumber()
    {
    	int o=0;
    	 java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
         while ( portEnum.hasMoreElements() ) 
         {
             CommPortIdentifier portIdentifier = portEnum.nextElement();
             System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
             o++;
         } 
         
        return o;
    }
    
    
    static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
	
};

