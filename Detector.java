/*Author: Brett Henry and GMU CS Department
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.AbstractCollection;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import javax.swing.JPanel;

public class Detector extends JPanel {
	/**
		 * Calculates the difference between two colors, and returns it as an interger value between 0-100, 0 being no difference, 100 being the difference between black and white
		 * @param c1 and c2
		 * @return int
		 */
	public static int getDifference(Color c1, Color c2) {
		
		double b1 = c1.getBlue();
		double b2 = c2.getBlue();
		double r1 = c1.getRed();
		double r2 = c2.getRed();
		double g1 = c1.getGreen();
		double g2 = c2.getGreen();
		double bdiff = Math.abs(b1-b2);
		double rdiff = Math.abs(r1-r2);
		double gdiff = Math.abs(g1-g2);
		
		int diff = (int)Math.floor(((Math.pow(bdiff,2)+Math.pow(rdiff,2)+Math.pow(gdiff, 2))/195075.0)*100.0);
		return diff; 
	}
	
	/**
	 * Checks the difference between the color of each pixel in the given image, and sets the pixel to black if the difference is < the ok 		 * distance given (int okDist), white otherwise
	 * @param BufferedImage image, Color c, int okDist
	 */
	public static void thresh(BufferedImage image, Color c, int okDist) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		for(int x = 0; x<width;x++)
		{
			for(int y = 0; y<height; y++)
			{
				Color pixel = new Color(image.getRGB(x, y));
				int diff = getDifference(c,pixel);
				if(diff<= okDist)
					image.setRGB(x, y, Color.BLACK.getRGB());
				else
					image.setRGB(x, y, Color.WHITE.getRGB());
			}
		}
	}
	
	/**
	 * Using the given image, disjoint set, and pixelId, returns the root of the above set, and the set to the left of the given pixelId
	 * returns null if either set does not exist.
	 * @param BufferedImage image, DisJointSet<Pixel> ds, int PixelId
	 * @returns Pair<Integer, Integer> where Pair<set to the left, set above>
	 */
	public static Pair<Integer,Integer> getNeighborSets(BufferedImage image, DisjointSets<Pixel> ds, int pixelId) {
		//TODO: Your code here...
		Pair<Integer,Integer> neighTbhors;
		
		if(pixelId%image.getWidth() == 0 && pixelId-image.getWidth() <0)
			neighTbhors = new Pair<Integer,Integer>(null,null);
		else if(pixelId%image.getWidth() == 0)
			neighTbhors = new Pair<Integer,Integer>(null,ds.find(pixelId-image.getWidth()));
		else if(pixelId-image.getWidth() <0)
			neighTbhors = new Pair<Integer,Integer>(ds.find(pixelId-1),null);
		else
			neighTbhors = new Pair<Integer,Integer>(ds.find(pixelId-1),ds.find(pixelId-image.getWidth())); 
		return neighTbhors; //neighTbhors....lol
	}
	
	/**
	 * calls thresh on the image passed to the constructor, using blobColor and okDist, then puts each individual pixel into theDisjointSet	 	  * . Moves through the image unioning black pixels and white pixels that are next to eachother. 
	 */
	public void detect() {
		thresh(img,blobColor,okDist);
		
		System.out.print("Working");
		ArrayList<Pixel> pixels = new ArrayList<Pixel>();
		for(int x = 0; x<(img.getWidth()*img.getHeight());x++)
		{
			pixels.add(getPixel(img,x));
		}
		ds = new DisjointSets<Pixel>(pixels);
		
		for(int t = 0; t<(img.getWidth() *img.getHeight());t++)
		{
			if(t%100000 == 0)
				System.out.print(".");
			int diff = 0;
			Pixel curr = pixels.get(t);
			Color cpix = getColor(img,curr);
			Pair<Integer,Integer> neighbhors = getNeighborSets(img,ds,t);
			if(neighbhors.a != null)
			{
				Color above = getColor(img,pixels.get(neighbhors.a));
				diff = getDifference(cpix,above);
				if(cpix.getRGB() == above.getRGB())
				{
					ds.union(ds.find(t), neighbhors.a);
					
				}
			}
			if(neighbhors.b != null)
			{
				if(neighbhors.a!= null && ds.find(neighbhors.b) != ds.find(neighbhors.a))
				{
					Color left = getColor(img,pixels.get(neighbhors.b));
					diff = getDifference(cpix,left);
					if(cpix.getRGB() == left.getRGB())
					{
						ds.union(ds.find(t), neighbhors.b);
					
					}
				}
			}
			
				
			
		}
		System.out.println("Done!");
		
	}

	/**
	 * Adds each uniques root to a set, then sorts the set. Then, recolors the k largest blobs to colors from getSeqColor(). Then outputs to 	  * the console the size of each k largest blob. Also draws a box around the largest blob and writes an image for each.
	 * @throws ILlegalArgumentException for k < 0
	 */
	public void outputResults(String outputFileName, String outputECFileName, int k) {
		if(k<1) {
			throw new IllegalArgumentException(new String("! Error: k should be greater than 0, current k="+k));
		}
		
		TreeSet<Integer> roots = new TreeSet<Integer>();
		for(int x = 0; x< (img.getHeight()*img.getWidth()) ; x++)
		{
			roots.add(ds.find(x));
		}
		
		ArrayList<Set<Pixel>> sets = new ArrayList<Set<Pixel>>();
		for(Integer root:roots)
		{
			if(getColor(img,getPixel(img,root)).getRGB() == Color.BLACK.getRGB())
				sets.add(ds.get(root));
		}
		Collections.sort(sets,new compareSets());
		
		if(sets.size() == 0)
		{
			System.out.println("No blobs found :'(");
			return;
		}
		int count = 0;
		int max = 0;
		if(sets.size() < k)
		{
			max = sets.size();
			System.out.println(sets.size()+"/"+sets.size());
			
		}
		else
		{		
			max = k;
			System.out.println(k+"/"+sets.size());
		}		
		for(int x =0; x<k; x++)
		{
			if(x>=sets.size())
				break;
			Set<Pixel> blob = sets.get(x);
			
			for(Pixel p:blob)
			{
				Color c = getSeqColor(x,max);
				img.setRGB(p.a, p.b, c.getRGB());
			}
			
		}
		
		for(int x = 0; x<k;x++)
		{
			if(x>=sets.size())
				break;
			System.out.println("Blob #"+(x+1)+" is "+sets.get(x).size()+" pixels");
		}
		
		try {
			File ouptut = new File(outputFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to "+outputFileName);
		}
		catch (Exception e) {
			System.err.println("! Error: Failed to save image to "+outputFileName);
		}
		reloadImage();
		Set<Pixel> largeBlob = sets.get(0);
		int minX = img.getWidth();
		int minY = img.getHeight();
		int maxX = 0;
		int maxY = 0;
		for(Pixel p:largeBlob)
		{
			if(p.a<minX)
				minX = p.a;
			if(p.b<minY)
				minY = p.b;
			if(p.a>maxX)
				maxX =p.a;
			if(p.b>maxY)
				maxY=p.b;
		}
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		g2d.drawRect(minX-1, minY-1, (maxX-minX)+3, (maxY-minY)+3);
		g2d.dispose();
		
		try {
			File ouptut = new File(outputECFileName);
			ImageIO.write(this.img, "png", ouptut);
			System.err.println("- Saved result to "+outputECFileName);
		}
		catch (Exception e) {
			System.err.println("! Error: Failed to save image to "+outputECFileName);
		}
		
	}
	/**
	 * Helper class. Comparator for Collections.sort() 
	 */
	private class compareSets implements Comparator<Set>{
		public int compare(Set a, Set b)
		{
			if(a.size()<b.size())
				return 1;
			else if(a.size() == b.size())
				return 0;
			else
				return -1;
		}
	}
	//main method just for your testing
	//edit as much as you want
	public static void main(String[] args) {
		//Color red = Color.DARK_GRAY;
		//Color black = Color.WHITE;
		//System.out.println(getDifference(black,red));
		/*File in = new File("04_TinyRGB.png");
		BufferedImage i = null;
		try {
			i = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR READING FILE");
		}
		thresh(i,Color.RED,10);
		File ouptut = new File("out.png");
		try {
			ImageIO.write(i, "png", ouptut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Detector test = new Detector("04_Circles.png",Color.BLUE,3);
		test.detect();
		test.outputResults("output.png", "output6.png", 10);
		/*ArrayList<Pixel> arrt = new ArrayList<Pixel>();
		for(int x = 0; x<10; x++)
		{
			for(int y = 0; y<10;y++)
			{
				arrt.add(new Pixel(x,y));
			}
			
		}
		DisjointSets<Pixel> dstest = new DisjointSets<Pixel>(arrt);
		dstest.union(0, 1);
		dstest.union(0, 2);
		dstest.union(0, 3);
		dstest.union(4, 0);
		dstest.union(10, 11);
		dstest.union(12, 13);
		dstest.union(10, 12);
		Pair<Integer, Integer> pt;
		pt = getNeighborSets(i,dstest,14);
		System.out.println(pt.a + " "+pt.b);
		/*
		
		//Some stuff to get you started...
		
		File imageFile = new File("../input/04_Circles.png");
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(imageFile);
		}
		catch(IOException e) {
			System.err.println("! Error: Failed to read "+imageFile+", error msg: "+e);
			return;
		}
		
		Pixel p = getPixel(img, 110); //100x100 pixel image, pixel id 110
		System.out.println(p.a); //x = 10
		System.out.println(p.b); //y = 1
		System.out.println(getId(img, p)); //gets the id back (110)
		System.out.println(getId(img, p.a, p.b)); //gets the id back (110)
		
		*/
	}

	//-----------------------------------------------------------------------
	//
	// Todo: Read and provide comments, but do not change the following code
	//
	//-----------------------------------------------------------------------

	//Data
	public BufferedImage img;        //this is the 2D array of RGB pixels
	private Color blobColor;         //the color of the blob we are detecting
	private String imgFileName;      //input image file name
	private DisjointSets<Pixel> ds;  //the disjoint set
	private int okDist;              //the distance between blobColor and the pixel which "still counts" as the color

	/**
	 * Constructor sets the apporpriate global variables. (imgfile, blobColor, okDist), to the given values
	 * @param String imgfile - path to image/image filename, Color blobColor - the color of the blob you want to search for, int okDist - acceptable distance between color.
	 */
	public Detector(String imgfile, Color blobColor, int okDist) {
		this.imgFileName=imgfile;
		this.blobColor = blobColor;
		this.okDist = okDist;
		
		reloadImage();
	}

	/**
	 * Helper method reads image from imgFileName
	 */
	public void reloadImage() {
		File imageFile = new File(this.imgFileName);
		
		try {
			this.img = ImageIO.read(imageFile);
		}
		catch(IOException e) {
			System.err.println("! Error: Failed to read "+this.imgFileName+", error msg: "+e);
			return;
		}
	}

	/**
	 * Helper JPanel function
	 * @param Graphics g
	 */
	public void paint(Graphics g) {
		g.drawImage(this.img, 0, 0,this);
	}

	//private classes below

	//Convenient helper class representing a pair of things
	private static class Pair<A,B> {
		A a;
		B b;
		public Pair(A a, B b) {
			this.a=a;
			this.b=b;
		}
	}

	//A pixel is a set of locations a (aka. x, distance from the left) and b (aka. y, distance from the top)
	private static class Pixel extends Pair<Integer, Integer> {
		public Pixel(int x, int y) {
			super(x,y);
		}
	}

	/**
	 * Helper method converts a pixel in an image into a pixelId.
	 * @returns int pixelId
	 * @param BufferedImage image, Pixel p
	 */
	private static int getId(BufferedImage image, Pixel p) {
		return getId(image, p.a, p.b);
	}

	
	/**
	 * Converts a pixelId back into a pixel
	 * @param int id, BufferedImage image
	 * @returns Pixel
	 * @throws ArrayIndexOutOfBounds for......You should be able to figure this one out on your own
	 */
	private static Pixel getPixel(BufferedImage image, int id) {
		int y = id/image.getWidth();
		int x = id-(image.getWidth()*y);

		if(y<0 || y>=image.getHeight() || x<0 || x>=image.getWidth())
			throw new ArrayIndexOutOfBoundsException();

		return new Pixel(x,y);
	}

	/**
	 * Gets the id of a given pixel
	 * @returns int pixelID
	 * @param BufferedImage image, int x, int y
	 */
	private static int getId(BufferedImage image, int x, int y) {
		return (image.getWidth()*y)+x;
	}

	/**
	 * Gets and returns the color of a given Pixel. 
	 * @param BufferedImage image, Pixel p
	 * @returns Color c
	 */
	private static Color getColor(BufferedImage image, Pixel p) {
		return new Color(image.getRGB(p.a, p.b));
	}
	
	/**
	 * Gets the color from a sequence of colors, given int i, and int max
	 * @param int i - the ith blob in the number of blobs, int max - the max number of blobs to get colors for
	 */
	private Color getSeqColor(int i, int max) {
		if(i < 0) i = 0;
		if(i >= max) i = max-1;
		
		int r = (int)(((max-i+1)/(double)(max+1)) * blobColor.getRed());
		int g = (int)(((max-i+1)/(double)(max+1)) * blobColor.getGreen());
		int b = (int)(((max-i+1)/(double)(max+1)) * blobColor.getBlue());
		
		if(r == 0 && g == 0 && b == 0) {
			r = g = b = 10;
		}
		else if(r == 255 && g == 255 && b == 255) {
			r = g = b = 245;
		}
		
		return new Color(r, g, b);
	}
}
