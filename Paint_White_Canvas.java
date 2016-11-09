import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class Paint_White_Canvas implements PlugInFilter {
	ImagePlus imp;
	int cSize = 5;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) 
	{
		int w = ip.getWidth();
		int h = ip.getHeight();
		drawCanvas(0, w, 0, cSize, ip);
		drawCanvas(0, w, h-cSize, h, ip);
		drawCanvas(0, cSize, 0, h, ip);
		drawCanvas(w-cSize, w, 0, h, ip);
	}

	private void drawCanvas(int xStart, int xEnd, int yStart, int yEnd, ImageProcessor ip) {
		for(int x=xStart; x<xEnd; x++)
			for(int y= yStart; y<yEnd; y++)
				ip.putPixel(x, y, 255);

		//for( ;  xStart<xEnd; xStart++)
			//for( ; yStart<yEnd; yStart++)
			//ip.putPixel(xStart, yStart, 255);
	}
}
