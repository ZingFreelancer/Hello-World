import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class My_Contrast_Adjustment implements PlugInFilter 
{
	ImagePlus imp;
	private int[] histogram;
	private int width = 512;
	private int height = 512;
	private int pixeLow = 0;
	private int pixelHigh = 0;
	private int pixelCount = 0;
	private float targetPercent = 0.03f;
	private int pixelPercent = 0;
	private float lowPercent = 0f;
	private float heighPercent = 0f;

	public int setup(String arg, ImagePlus imp) 
	{
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) 
	{
		histogram = ip.getHistogram();
		width = ip.getWidth();
		height = ip.getHeight();
		//Calculate total pixel count
		pixelCount = width * height;
		//Create dialog and calculate % in decimal
		targetPercent = createDialog() / 100.0f;
		//Calculate how many pixels in current image equals to our target %
		pixelPercent = Math.round(pixelCount * targetPercent);

		//find low and high values in histogram
		getLowPercent();
		getHeighPercent();
		
		//find a & b
		float a = 255.0f / (pixelHigh - pixeLow);
		float b = -255.0f * pixeLow / (pixelHigh - pixeLow);

		//change color of all pixels
		for(int w = 0; w < width; w++)
		{
			for(int h = 0; h < height; h++)
			{
				ip.putPixel(w, h, Math.round(a * ip.getPixel(w, h) + b) );
			}
		}
	}

	//Get matching low position in histogram array
	private static int getLowPercent(int[] innHistogram, int innPixelPercent)
	{
		int out = 0;
		for(int x = 0; x < innHistogram.length-1; x++)
		{
			//Sum pixels in array
			out = out + innHistogram[x];
			//Check if sum is larger than desired %
			if(out >= innPixelPercent)
			{
				pixeLow = x;
				return;
			}
		}
	}

	//Get matching high position
	private void getHeighPercent()
	{
		for(int x = histogram.length-1; x > 0; x--)
		{
			pixelHigh = pixelHigh + histogram[x];
			//heighPercent = pixelHeigh / pixelCount;
			if(pixelHigh >= pixelPercent)
			{
				pixelHigh = x;
				return;
			}
		}
	}

	//creates dialog
	private int createDialog() 
	{
		int prosent = 0;
		GenericDialog gd = new GenericDialog("Metning i prosent");
		gd.addNumericField("Prosent (opp til 5): ", prosent, 0);
		gd.showDialog();
		if (gd.wasCanceled()) return 3;
		prosent = (int) gd.getNextNumber();
		if(prosent > 5)
			prosent = 5;
		return prosent;
	}
}
