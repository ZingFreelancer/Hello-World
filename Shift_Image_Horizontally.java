import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Shift_Image_Horizontally implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) 
	{
		int w = ip.getWidth();
		int h = ip.getHeight();
		int end= w;
		int tmp = 0;

		//Repeat task until end
		for(int d= 0; d<end; d++) 
		{
			//Vertical loop
			for(int y = 0; y < h; y++) 
			{
				//hent ut bakerste pixel og lagre den i midlirtidig variabel
				tmp = ip.getPixel(w-1, y);
				//Horizontal loop with pixel replacement
				for(int x = w-1; x > 0; x--) 
				{
					//Flytt pixel et hakk mot høyre
					ip.putPixel(x, y, ip.getPixel(x-1, y) );
				}
				//Put den bakerste pixel fram og vis nåværende fremgang
				ip.putPixel(0, y, tmp);
				imp.updateAndDraw();
			}
		}			
	}

}
