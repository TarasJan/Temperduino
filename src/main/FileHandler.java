package main;


import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultKeyedValuesDataset;

public class FileHandler extends DefaultCategoryDataset {

	String file;
	TreeMap<Date,Float>array;
	SimpleDateFormat dt;
	
	public FileHandler(String file,TreeMap<Date,Float> outcomes,SimpleDateFormat datef) throws FileNotFoundException  {
	
		this.file = file;
		this.array = outcomes;
		 this.dt = datef;

		    
		
	}

	public void endWrite() throws IOException
	{
		FileWriter fw=null;
		try {
			fw = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		   System.out.println("FileHandler");
for (Entry<Date,Float> record : array.entrySet()) 
{
	    try  {
	    	System.out.print(record.getKey() + " " + record.getValue());
	    	fw.write(dt.format(record.getKey()) + " ");
	    	fw.write(record.getValue().toString()+String.format("%n"));
	   //   array.remove(record);
	    } catch (IOException x) {
	      System.err.println(x);
	    }
	}
	
	fw.close();
	
	}

}
		
	
