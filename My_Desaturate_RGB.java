import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class My_Desaturate_RGB implements PlugInFilter 
{
	ImagePlus imp;
	static double sCol = 0.3;

	public int setup(String arg, ImagePlus imp) 
	{
		this.imp = imp;
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) 
	{
		sCol = createDialog();
		for(int y = 0; y < ip.getHeight(); y++)
		{
			for(int x = 0; x < ip.getWidth(); x++)
			{
				//Get int packed color pixel
				int c = ip.get(x, y);

				//Extract RGB components from color pixel
				int r = (c & 0xff0000) >> 16;
				int g = (c & 0x00ff00) >> 8;
				int b = (c & 0x0000ff);

				//compute equivalent gray value
				double gray = 0.299*r + 0.587*g + 0.114*b;

				//Linerly interpolate (yyy) <-> (rgb)
				r = (int) (gray + sCol * (r - gray));
				g = (int) (gray + sCol * (g - gray));
				b = (int) (gray + sCol * (b - gray));

				//Reassemble color pixel
				c = ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff;
				ip.set(x, y, c);
			}
		}
	}

   	/**
    * Create a simple dialogue window
    * @return new desaturation value as double
    */
	private static double createDialog() 
	{
		//Return value
		double re = 0;
		//Dialogue object
		GenericDialog gd = new GenericDialog("Desaturation");
		gd.addNumericField("Desaturation factor (0 ti 1): ", re, 2);
		gd.showDialog();
		//If dialogue is cancelled, return re with its default value
		if (gd.wasCanceled()) return re;
		//Get numericfield value
		re = gd.getNextNumber();
		//Clamp the value between 0 and 1
		re = clamp(re, 0, 1);
		//return new value
		return re;
	}

   	/**
    * Clamps a value between desired minimum and maximum value
    * @param value to be clamped
    * @param minimum allowed value
    * @param maximum allowed value
    * @return clamped value
    */
	public static double clamp(double val, double min, double max) 
	{
    	return Math.max(min, Math.min(max, val));
	}

}
