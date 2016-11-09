import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class My_RGB_Contrast_Adjustment implements PlugInFilter 
{
	ImagePlus imp;
	private float targetPercent = 0.03f;
	private int pixelCutoff = 0;
	private ColorProcessor cp;
	//RGB ByteProcessor variable
	private ByteProcessor rgb_bp;
	//RGB Histogram array
	private int rgb_hist[];
	//RGB Low pixel value
	private int rgb_low;
	//RGB High pixel value
	private int rgb_high;
	//RGB a variable array
	private float[] rgb_a = new float[3];
	//RGB b variable array
	private float[] rgb_b = new float[3];

	public int setup(String arg, ImagePlus imp) 
	{
		this.imp = imp;
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) 
	{
		int WIDTH = ip.getWidth();
		int HEIGHT = ip.getHeight();
		//Create dialogue to get contrast adjustment %
		targetPercent = createDialog() / 100.0f;
		//Calculate pixel cutoff amount
		pixelCutoff = Math.round( (WIDTH * HEIGHT) * targetPercent);

		//Set color processor
		cp =  (ColorProcessor)ip;

		//Iterate over RGB channels and calculate correct a & b values for each channel
		for(int i = 0; i < 3; i++)
		{
			//Get one of RGB channels & store it in ByteProcessor
			rgb_bp = cp.getChannel(i + 1, null);
			//Get histogram from ByteProcessor
			rgb_hist = rgb_bp.getHistogram();
			//Calculate low pixel position using histogram & pixel cutoff
			rgb_low = getLowPercent(rgb_hist, pixelCutoff);
			//Calculate high pixel position using histogram & pixel cutoff
			rgb_high = getHeighPercent(rgb_hist, pixelCutoff);
			//Calculate a & b values and store them in array
			rgb_a[i] = 255.0f / (rgb_high - rgb_low);
			rgb_b[i] = -255.0f * rgb_low / (rgb_high - rgb_low);
		} 

		int[] RGB = new int[3];
		//change color of all pixels
		for(int x = 0; x < WIDTH; x++)
			for(int y = 0; y < HEIGHT; y++)
			{
				cp.getPixel(x, y, RGB);
				//for(int j = 0; j < 3; j++)
					//RGB[j] = clamp(Math.round(rgb_a[j] * RGB[j] + rgb_b[j] ), 0, 255);
				RGB[0] = clamp(Math.round(rgb_a[0] * RGB[0] + rgb_b[0] ), 0, 255);
				RGB[1] = clamp(Math.round(rgb_a[1] * RGB[1] + rgb_b[1] ), 0, 255);
				RGB[2] = clamp(Math.round(rgb_a[2] * RGB[2] + rgb_b[2] ), 0, 255);
				cp.putPixel(x, y, RGB);
			}
	}



   	/**
    * Clamps a value between desired minimum and maximum value
    * @param value to be clamped
    * @param minimum allowed value
    * @param maximum allowed value
    * @return clamped value
    */
	public static int clamp(int val, int min, int max) 
	{
		// val == Math.max(min, Math.min(max, val)) ? true : false;
    	return Math.max(min, Math.min(max, val));
	}


   	/**
    * Get low position in histogram array using pixelCutoff
    * @param histogram
    * @param cutoff
    * @return pixel value as position in histogram where cutoff match pixel amount
    */
	private static int getLowPercent(int[] inHistogram, int inCutoff)
	{
		int count = 0;
		for(int x = 0; x < inHistogram.length-1; x++)
		{
			//Sum pixels in array
			count = count + inHistogram[x];
			//Check if sum is larger than desired %
			if(count >= inCutoff)
				//Return x position
				return x;
		}
		return count;
	}

   	/**
    * Get high position in histogram array using pixelCutoff
    * @param histogram
    * @param cutoff
    * @return pixel value as position in histogram where cutoff match pixel amount
    */
	private static int getHeighPercent(int[] inHistogram, int inCutoff)
	{
		int count = 0;
		for(int x = inHistogram.length-1; x > 0; x--)
		{
			count = count + inHistogram[x];
			//heighPercent = pixelHeigh / pixelCount;
			if(count >= inCutoff)
				//Return X position
				return x;
		}
		return count;
	}

   	/**
    * Create input dialog window
    * @return new % value
    */
	private int createDialog() 
	{
		int prosent = 0;
		GenericDialog gd = new GenericDialog("Contrast justering i %");
		gd.addNumericField("Prosent (opp til 5): ", prosent, 0);
		gd.showDialog();
		if (gd.wasCanceled()) 
			return 3;
		else 
			return clamp((int) gd.getNextNumber(), 0, 5);
	}

   /**
    * Method to display a new window with image
    * @param ImageProcessor instance to display
    * @param Title of the window
    */
	private static void newImageWindow(ImageProcessor bp, String title)
	{
		ImagePlus img = new ImagePlus(title, bp);
		img.show();
	}

   /**
    * Easy method for debugging purpos only, write text to ImageJ log
    * @param Text to log
    */
	private static void print(String text)
	{ IJ.log(text); }
}