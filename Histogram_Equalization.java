import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import java.lang.Object;
import java.util.Random;
import java.util.Arrays;

public class Histogram_Equalization implements PlugInFilter {
	ImagePlus imp;
	private static final int K = 256;
	private static final int cHistHeight = 100;
	private static final int cHistWidth = 256;
	private static int MEAN = 127;
	private static int DEVIATION = 50;
	//Default width & height to K
	private static int WIDTH = K;
	private static int HEIGHT = K;
	private static final int GUASSMAX = 1000;

	private int[] imgHist;
	private int[] imgCHist;
	private int[] refHistogram;
	private int[] refCHistogram;
	private int[] inversedRefCHistogram;
	

	public int setup(String arg, ImagePlus imp) 
	{
		this.imp = imp;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) 
	{
		//init variables
		WIDTH = ip.getWidth();
		HEIGHT = ip.getHeight();
		refHistogram = new int[K];
		refCHistogram = new int[K];
		inversedRefCHistogram = new int[K];
		imgHist = ip.getHistogram();

		//run methods
		makeRefHistogram();
		//Make Cumulative histograms
		refCHistogram = makeCumulativeHistogram(refHistogram);
		imgCHist = makeCumulativeHistogram(imgHist);

		//Normalize cumulative histogram
		refCHistogram = normilize(refCHistogram);
		imgCHist = normilize(imgCHist);

		//Print normilized histogram
		drawCHistogram(refCHistogram, "CHist");
		//Inverse normalized cumulative histogram
		inverseRefCHistogram();
		//Draw inversed normalized cumulative histogram
		drawCHistogram(inversedRefCHistogram, "Inversed CHist");
		//Print image cumulative histogram
		drawCHistogram(imgCHist, "Image Before");
		/*equalize images based on normalized cumulative image histogram
				and normalized cumulative reference histogram
		*/
		equalize(ip, imgCHist, inversedRefCHistogram);

		//Sample and display histogram of result
		imgHist = ip.getHistogram();
		imgCHist = makeCumulativeHistogram(imgHist);
		imgCHist = normilize(imgCHist);
		drawCHistogram(imgCHist, "Image After");
	}

	//Create a reference histogram based on gaussian distribution
	private void makeRefHistogram()
	{
		double bottom = 2*Math.pow(DEVIATION, 2);
		for(int x = 0; x < K; x++)
		{
			double top = Math.pow(x-MEAN, 2);
			double gauss = Math.pow(Math.E, -(top/bottom));
			refHistogram[x] = (int)Math.round(gauss * GUASSMAX);
		}
	}

	//Equalize image based on inversed cumulative reference histogram
	private void equalize(ImageProcessor imgp, int[] hist, int[] ref)
	{
		for(int y = 0; y < HEIGHT; y++)
			for(int x = 0; x < WIDTH; x++)
			{
				int p1 = imgp.getPixel(x, y);
				int p2 = ref[hist[p1]];
				imgp.putPixel(x, y, p2);
			}
	}

	//Create a cumulative histogram based on gassian reference histogram
	private int[] makeCumulativeHistogram(int[] hist)
	{
		int sum = 0;
		for(int i = 0; i < hist.length; i++)
		{
			sum += hist[i];
			hist[i] = sum;
		}
		return hist;
	}

	//Clamp gaussian cumulative histogram
	private int[] normilize(int[] hist)
	{
		for(int i = 0; i < hist.length; i++)
		{
			int v = Math.round(hist[i] * 255.0f / hist[K-1]);
			hist[i] = v;
		}
		return hist;
	}

	//inverse gaussian cumulative histogram
	private void inverseRefCHistogram()
	{
		for(int i = 0; i < refCHistogram.length; i++)
		{
			int v = refCHistogram[i];
			inversedRefCHistogram[v] = i;
		}
		//fill out the gaps
		for(int j = 1; j < refCHistogram.length; j++)
			if(inversedRefCHistogram[j] == 0)
				inversedRefCHistogram[j] = inversedRefCHistogram[j-1];
	}

	//Visualize Cumulative histogram method
	private void drawCHistogram(int[] cHist, String title)
	{
		ImageProcessor imgp = new ByteProcessor(cHistWidth, cHistHeight);
		imgp.setValue(255);
		imgp.fill();

		int maxHeight = 0;
		int yStart= imgp.getHeight();
		float maxValue= cHist[cHist.length-1];

		for(int x = 0; x < cHist.length; x++)
		{
			maxHeight = Math.round(cHist[x] * yStart / maxValue);
			for(int y = 0; y < maxHeight; y++)
			{
				imgp.putPixel(x, yStart - y, 0);
			}
		}
		
		//Display comulative histogram image
		String cHist_title = title;
		ImagePlus cHist_img = new ImagePlus(cHist_title, imgp);
		cHist_img.show();
	}

	//Log function to output information to imagej log
	private void print(String text)
	{ IJ.log(text); }

}
