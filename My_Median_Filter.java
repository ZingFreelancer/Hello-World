import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import java.util.Arrays;

public class My_Median_Filter implements PlugInFilter {
	ImagePlus imp;
	private int FILTERSIZE = 65;
	private int weightMatrix[][] = {
		{0,0,1,0,0},
		{0,1,2,1,0},
		{1,2,3,2,1},
		{0,1,2,1,0},
		{0,0,1,0,0}
	};

	public int setup(String arg, ImagePlus imp) 
	{
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) 
	{
		//Calculate filter size by calling getFilterSize method
		FILTERSIZE = getFilterSize(weightMatrix);
		//Call method and replace ImageProcessor with new image
		ip = doFilter(ip, weightMatrix, FILTERSIZE);
	}

   /**
    * Process image with weighted median filter technique
    * @param original image processor
    * @param Weight Matrix
    * @param filter size to be used for median filter
    * @return ImageProcessor
    */
	private static ImageProcessor doFilter(ImageProcessor imgp, int[][] wm, int fSize)
	{
		//Duplicate original image in order to keep original pixel values
		ImageProcessor copy = imgp.duplicate();
		int WIDTH = imgp.getWidth();
		int HEIGHT = imgp.getHeight();
		//Create a median filter based on fSize
		int[] filter = new int[fSize];
		//Calculate X size of weight matrix
		int HALFXMATRIX = (int) wm.length/2;
		//Calculate Y size of weight matrix
		int HALFYMATRIX = (int) wm[0].length/2;
		//Calculate filter half size for future use
		int FILTERHALFSIZE = (int)fSize/2;

		for(int y = HALFYMATRIX; y <= HEIGHT - HALFYMATRIX; y++)
			for(int x = HALFXMATRIX; x <= WIDTH - HALFXMATRIX; x++)
			{
				int fIndex = 0;
				for(int fy = -HALFYMATRIX; fy <= HALFYMATRIX; fy++)
					for(int fx = -HALFXMATRIX; fx <= HALFXMATRIX; fx++)
					{
						for(int i = 0; i < wm[HALFXMATRIX+fx][HALFYMATRIX+fy]; i++)
						{
							filter[fIndex] = copy.getPixel(x+fx, y+fy);
							fIndex++;
						}
					}
				Arrays.sort(filter);
				imgp.putPixel(x, y, filter[FILTERHALFSIZE]);
			}
		return imgp;
	}

   /**
    * Calculate filter size by summing all values
    * in weight matrix
    * @param weight matrix to be used for the filter
    * @return filter size as integer
    */
	private static int getFilterSize(int[][] matrix)
	{
		int sum = 0;
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[0].length; j++)
				sum += matrix[i][j];
		return sum;
	}

	//Print method for debugging
	private void print(String out) 
	{ IJ.log(out); }

}
