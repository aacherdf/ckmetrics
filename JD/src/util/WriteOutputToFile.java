package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteOutputToFile {
	
	public void writeOutput(String outputData, String fileName)
	{
		String userDesktopLocation = System.getProperty("user.home") + "/Desktop";
		File outputFile = new File(userDesktopLocation, fileName);
		FileWriter fr = null;
		
		try
		{
			fr= new FileWriter(outputFile);
			fr.write(outputData);
			
		}catch(IOException ie)
		{
			ie.printStackTrace();
		}
		finally
		{
			try
			{
				fr.close();
			}
			catch(IOException ie)
			{
				ie.printStackTrace();
			}
		}
	}

}
