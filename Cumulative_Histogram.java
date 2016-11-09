import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import java.util.Arrays;

public class Cumulative_Histogram implements PlugInFilter {
	ImagePlus imp;
	int cHistHeight = 150;
	int cHistWidth = 256;

	int[] hist;
	int[] cHist;

	public int setup(String arg, ImagePlus imp) 
	{
		this.imp = imp;
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) 
	{
		hist = ip.getHistogram();
		drawHistogram();
		
		cHist = getCumulativeHistogram(hist);
		drawCHistogram();
	}

	
	//Draw greyscale marker bellow histogram
	private void drawGreyscaleLine(ImageProcessor imgp)
	{
		int height = imgp.getHeight();
		int width = imgp.getWidth();
		int yStart = (int) ( (height -50) + 50 * 0.25 );
		int ySize = (int) (50 * 0.5);
		
		for(int y = 0; y < ySize; y++)
			for(int w = 0; w < width; w++)
			{
				imgp.putPixel(w, yStart+ y, w);
			}
	}

	//Create and return an array containing Comulative Histogram arrays
	private int[] getCumulativeHistogram(int[] innHist)
	{
		int sum = 0;
		int[] reHist = new int[innHist.length];
		for(int i = 0; i< innHist.length; i++)
		{
			sum += innHist[i];
			reHist[i] = sum;
		}
		return reHist;
	}

	//Draws Comulative histog
	private void drawCHistogram()
	{
		ImageProcessor imgp= new ByteProcessor(cHistWidth, cHistHeight);
		imgp.setValue(255);
		imgp.fill();

		int maxHeight = 0;
		int yStart= imgp.getHeight()-50;
		int maxValue= cHist[cHist.length-1];

		for(int x = 0; x < cHist.length; x++)
		{
			maxHeight = Math.round(cHist[x] * yStart/ maxValue);
			for(int y = 0; y < maxHeight; y++)
			{
				imgp.putPixel(x, yStart- y, 0);
			}
		}
		drawGreyscaleLine(imgp);
		
		//Display comulative histogram image
		String cHist_title = "Comulative Histogram of " + imp.getTitle();
		ImagePlus cHist_img = new ImagePlus(cHist_title, imgp);
		cHist_img.show();
	}

	//Draws standard histogram
	private void drawHistogram()
	{
		ImageProcessor imgp= new ByteProcessor(cHistWidth, cHistHeight);
		imgp.setValue(255);
		imgp.fill();

		int maxHeight = 0;
		int yStart= imgp.getHeight()-50;
		int maxHistValue = findMax(hist);

		for(int x = 0; x<hist.length; x++)
		{
			maxHeight = hist[x] * yStart / maxHistValue;
			for(int y = 0; y < maxHeight; y++)
			{
				imgp.putPixel(x, yStart - y, 0);
			}
		}

		drawGreyscaleLine(imgp);
		//Display histogram image
		String hist_title = "Histogram of " + imp.getTitle();
		ImagePlus hist_img = new ImagePlus(hist_title, imgp);
		hist_img.show();
	}
	
	//find max value in histogram array
	private int findMax(int[] innArray)
	{
		int re = 0;
		for(int i = 0; i < innArray.length; i++)
			if(innArray[i] > re)
				re = innArray[i];
		return re;
	}
}
