import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Mirror_Image implements PlugInFilter {

	ImagePlus imp;
	ImagePlus newImp;
	ImageProcessor newIp;
	int h = 100;
	int w = 100;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) 
	{
		h = ip.getHeight();
		w = ip.getWidth();

		newIp= new ByteProcessor(h, w);  
		String title = "Mirrored Image";  
		newImp= new ImagePlus(title, newIp);  
		newImp.show(); 
		mirror(ip);
	}

	private void mirror(ImageProcessor  ip)
	{
		for(int x = 0; x < w; x++)
		{
			for(int y = 0; y < h; y++)
			{
				int pixel = ip.getPixel(x, y);
				newIp.putPixel(h-y, w-x, pixel);
			}
			newImp.updateAndDraw();
		}
	}
}
