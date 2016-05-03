package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;


import gnu.io.CommPortIdentifier;


public class Main {

	public static volatile Scanner userInput;
	public static volatile List<String> outcomes;
	public static PortHandler port;
	public static SimpleDateFormat dt;
	public static FileHandler writer;
	public static Thread reader;
	
	static TreeMap<Date,Float> finalResults;
	
	private static ChartFrame frame = null;
	private static JFrame menuFrame = null;
	
	
	private static JLabel label;
	private static JComboBox<String> portBox;
	
	private static JPanel menuPanel;
	private static JButton showChart;
	private static JButton saveAsJPG; 
	private static JButton cancelButton ;
	private static JButton newMeasurementButton;
	
	private static JFreeChart chart;
	private static XYDataset data;
	private static XYPlot plot;
	
	 private static void displayChart() {

	        

	            // create a default chart based on some sample data...
	            final String title = "Temperature measurements over time";
	            final String xAxisLabel = "Time";
	            final String yAxisLabel = "Temperature[Celcius]";

	             data = createXYDataset();

	             chart = ChartFactory.createXYStepChart(
	                title,
	                xAxisLabel, yAxisLabel,
	                data,
	                PlotOrientation.VERTICAL,
	                true,   // legend
	                true,   // tooltips
	                false   // urls
	            );

	            // then customise it a little...
	            chart.setBackgroundPaint(new Color(216, 216, 216));
	             plot = chart.getXYPlot();
	            plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));

	            
	            // and present it in a frame...
	            frame = new ChartFrame("Temperduino Chart", chart);
	            frame.pack();
	            RefineryUtilities.positionFrameRandomly(frame);
	            frame.setVisible(true);

	        
	        
	            frame.requestFocus();
	        

	    }

	

		  public static XYDataset createXYDataset() {

		         XYSeries series = new XYSeries("Temperature", false, true);
		         for (Entry<Date,Float> lol : finalResults.entrySet())
			    	{
			    	series.add(lol.getKey().getTime(),lol.getValue());
			   
			    	}
		         
		         XYSeriesCollection dataset = new XYSeriesCollection();
		         dataset.addSeries(series);
		         
		return dataset;
	}
	
	
	public static void startPortConnection() throws Exception 
	{
		
	    
	    int number = PortHandler.getPortNumber();
	    System.out.println(number);
		
		if(number==0)throw new Exception("No serial ports detected.");
			
			
		if (number==1)
		{
		       Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();

			CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
			try
			{
				port.connect(com.getName());
			}
			catch(Exception e)
			{
				System.out.println("Failed to open the only port available");
				throw e;
			}
			
		}
			
			
			
			
		if(number>1){
			System.out.println("More than one port available.");
			System.out.println("Selecting default port" + (String) portBox.getSelectedItem());
			try
			{
				port.connect((String) portBox.getSelectedItem());
			}
			catch(Exception e)
			{
				System.out.println("Failed to open the port.");
				throw e;
			}
			
		}
			
		
		
		
		
		
	}
	
	public static void main(String[] args) throws Exception {
	
		

		Scanner userInput = new Scanner(System.in);
		dt = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
		
		finalResults = new TreeMap<Date,Float>();
		
		outcomes  = Collections.synchronizedList(new ArrayList<String>());
		
		port = new PortHandler(outcomes,dt);
		 writer = new FileHandler("outcomes.txt", finalResults,dt);
		
		
		PortHandler.listPorts();
		HashSet<CommPortIdentifier> h = PortHandler.getAvailableSerialPorts();
		for (CommPortIdentifier i : h)System.out.println(i.getName());
		
		//port.connect("COM4")
		
		//tu bylo to
		   try {
				startPortConnection();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

      		reader = port.setSerialListener();
      		
      		reader.start();
		initializeGUI();
		
	}
		
	
	public static void initializeGUI()
	{
		
		
		
		//1. Create the frame.
	       menuFrame = new JFrame("Temperduino v0.1");

	       //2. Optional: What happens when the frame closes?
	       menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	       //3. Create components and put them in the frame.
	       //...create emptyLabel...
	       menuPanel = new JPanel();
	       menuPanel.setLayout(new GridLayout(6,1));
	       
	       
	       label = new JLabel("Select the communication port:");
	       
	       portBox = new JComboBox<String>();	       
	       //inserting available ports into the combo box
	       for ( CommPortIdentifier com : PortHandler.getAvailableSerialPorts() )
	       {
	    	   portBox.addItem( com.getName());
	       }
	       
	  
	       
	       cancelButton = new JButton("Exit");
	       newMeasurementButton = new JButton("Start new measurement");
	       
	       showChart = new JButton("Show chart");
	       
	       saveAsJPG = new JButton("Save chart as jpg");
	       
	       
	       saveAsJPG.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) {
	        	   
	        	   if(frame!=null)
	        	   {
	        		   
	        			   try {
							ChartUtilities.saveChartAsJPEG(new File("chart.jpg"),chart , 500, 300);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	        		   
	        	   }
	           }          
	        });
	       
	       
	       
	       
	       
	       showChart.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) {
	        	   
	        	   
	        	   Iterator<String> it = outcomes.iterator();
	       	    float val =0;
	       	    
	       	    while(it.hasNext()){
	       	    	
	       	    	
	       	    	
	       	    	String s = it.next();
	       	    	System.out.println(s);
	       			//safeList.add(s);
	       	    	Date valDate = null;
					try {
						valDate = dt.parse(s);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	       	    	
	       	    	if (it.hasNext())
	       	    	{
	       	    		s = it.next();
	       	    		val = Float.valueOf(s);
	       	    	}
	       	    	
	       	    	finalResults.put(valDate, val);
	       	    	
	       	    }
	       	    
	       	    for (Entry<Date,Float> lol : finalResults.entrySet())
	       	    	{
	       	    	Date key = lol.getKey();
	       	    	
	       	    	float value = lol.getValue();

	       	    System.out.printf("%s : %s\n", key, value);
	       	    
	       	    	}
	       	    
	       		try {
					writer.endWrite();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	   
	        	   
	               displayChart();

	           }          
	        });
	       
	       cancelButton.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) {
	        	   
	        	   
	        	 System.exit(0);
	           }          
	        });
	       
	       
	       
	       newMeasurementButton.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent e) {
	        	 //  frame.setVisible(false);
	        	
	       		
	        	   if(!reader.isAlive())reader.start();
	        	   else
	        	   {
	        	   outcomes.removeAll(outcomes);
	        	   finalResults.clear();
	        	   System.out.println();
	        	   System.out.println(outcomes.size());
	        	   }
	           }          
	        });
	       
	       //4. Size the frame.
	       menuPanel.add(label);
	       menuPanel.add(portBox);
	       menuPanel.add(newMeasurementButton);
	       menuPanel.add(showChart);
	       menuPanel.add(saveAsJPG);
	       menuPanel.add(cancelButton);
	       
	       menuFrame.add(menuPanel);
	       
	       menuFrame.pack();
	       //5. Show it.
	       menuFrame.setVisible(true);
		
	}
	
	
	
	
	
	
}
		
		
		

