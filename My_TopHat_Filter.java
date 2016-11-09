import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class My_TopHat_Filter implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) 
	{
		whiteTopHat(ip);
		blackTopHat(ip);
	}

	private static void whiteTopHat(ImageProcessor i)
	{
		//Duplicate original image
		ImageProcessor ip = i.duplicate();
		//Apply open operation on copy by calling erode and dilate
		ip.erode();
		ip.dilate();
		//Display copy of processed image
		newImageWindow(ip.duplicate(), "Open operation");
		//Copy pixels from original image to duplicate
		ip.copyBits(i, 0,0, Blitter.SUBTRACT);
		//Display duplicate image modified by original image
		newImageWindow(ip, "Open + white top hat");
	}

	/**
    * Perform close operation followed by Black Top hat operation
    * @param ImageProcessor reference to image
    */
	private static void blackTopHat(ImageProcessor i)
	{
		//Duplicate original image
		ImageProcessor ip = i.duplicate();
		//Apply close operation on copy by calling dilate and erode
		ip.dilate();
		ip.erode();
		//Display image after close operation
		newImageWindow(ip, "Close operation");
		//Copy pixels from duplicate image to original
		i.copyBits(ip, 0,0, Blitter.SUBTRACT);
		//Display "original" image modified by duplicate image
		newImageWindow(i, "Close + black top hat");
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
}
