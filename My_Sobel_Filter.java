import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class My_Sobel_Filter implements PlugInFilter 
{
	ImagePlus imp;
	private String img_title;
	//X matrix filter
	private static final int[][] Y_MATRIX = {
		{-1, -2, -1},
		{0, 0, 0},
		{1, 2, 1}
	};
	//Y matrix filter
	private static final int[][] X_MATRIX = {
		{-1, 0, 1},
		{-2, 0, 2},
		{-1, 0, 1}
	};

	public int setup(String arg, ImagePlus imp) 
	{
		this.imp = imp;
		img_title = imp.getTitle();
		return DOES_8G;
	}

	public void run(ImageProcessor ip) 
	{
		processMatrix(ip, img_title, X_MATRIX, Y_MATRIX);
	}

   /**
    * Process image with X and Y matrix
    * @param original image processor
    * @param original image title
    * @param X filter matrix
    * @param Y filter matrix
    */
	private static void processMatrix(ImageProcessor imgp, String title, int[][] XMatrix, int[][] YMatrix)
	{
		int width = imgp.getWidth();
		int height = imgp.getHeight();
		//Create image processor objcet to write intensity pixel values to
		ImageProcessor intensityImg = new ByteProcessor(width, height);
		//Create image processor object to write orientation pixel values to
		ImageProcessor orientationImg = new ByteProcessor(width, height);

		//Iterate through image starting from 1 and ending at length-2
		for(int y = 1; y <= height -2; y++)
			for(int x = 1; x <= width -2; x++)
			{
				/*
					Apply filter to every pixel in the image &
					convolve every pixel with filter
				*/
				int convolveX = 0;
				int convolveY = 0;
				for(int fy = -1; fy <= 1; fy++)
					for(int fx = -1; fx <= 1; fx++)
					{
						convolveX += XMatrix[fx+1][fy+1] * imgp.getPixel(x+fx, y+fy);
						convolveY += YMatrix[fx+1][fy+1] * imgp.getPixel(x+fx, y+fy);
					}
					//Calculate intensity using G = squareRoot(x^2 + y^2)
					float intensity = (float)Math.sqrt( (Math.pow(convolveX,2)+Math.pow(convolveY,2)));
					//Calculate orientation using atan2(y / x) and scale result to fit between 0 and 255
					float orientation = (float)((Math.atan2(convolveY, convolveX)+Math.PI)/(Math.PI*2)*255.0f);
					
					//Write new pixel values to their respective image processor variables
					intensityImg.putPixel(x, y, Math.round(intensity));
					orientationImg.putPixel(x, y, Math.round(orientation));
			}
		//Call newImgeWindow to display results
		newImageWindow(intensityImg, "Intensity of "+title);
		newImageWindow(orientationImg, "Orientation of "+title);
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
