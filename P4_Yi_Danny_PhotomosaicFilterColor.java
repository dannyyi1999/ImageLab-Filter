package filters;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import imagelab.*;

public class P4_Yi_Danny_PhotomosaicFilterColor implements ImageFilter {

	ImgProvider filteredImage;

	@SuppressWarnings("resource")
	public void filter (ImgProvider ip) {
		short[][] oR = ip.getRed(); // get a 2D array of all original red values
		short[][] oG = ip.getGreen(); // get a 2D array of all original green values
		short[][] oB = ip.getBlue(); // get a 2D array of all original blue values
		short[][] oA = ip.getAlpha(); // get a 2D array of all original alpha values (transparency)
		
		/*  Here is what is in each of the 2D arrays
			{ // c0  c1   c2
				{35, 209, 153}, //row 0
				{45, 210, 103}, // row 1
				{55, 200, 173}, // row 2
				{75, 255, 123} // row 3
			}
			if the above information were stored in short[][] a,
			then a would have 4 rows and 3 columns and the following would be true:
			a[0] would be the array {35, 209, 153}
			a[0][2] would be the value of the item in row 0 and column 2 which is 153
			a[3][1] = a[2][0] would change the value of a[3][1] from 255 to 55 because a[2][0] is 55
		*/
		int height = oR.length; // height is the number of rows which is the length of the outer array
		int width = oR[0].length; // width is the number of columns which is the length of each inner array

		Scanner in;
		ArrayList <ColorInfo> database = new ArrayList <ColorInfo>();
		
		FileDialog fd = new FileDialog(new Frame(), "Select a Color database", FileDialog.LOAD);
		fd.setVisible(true);
		try {
			in = new Scanner(new File(fd.getDirectory() + fd.getFile()));
			while(in.hasNext()){
				database.add(new ColorInfo(Integer.parseInt(in.next()), Integer.parseInt(in.next()), Integer.parseInt(in.next())));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int diameter = Integer.parseInt(JOptionPane.showInputDialog("Please enter the pixel diameter ranging from 1 - 40"));
		while(diameter > 40 || diameter < 0){
			diameter = Integer.parseInt(JOptionPane.showInputDialog("I'm sorry, that is not in the range. Please choose from 1 - 40"));
		}
		
		int mosWidth = width / diameter * 40;
		int mosHeight = height / diameter * 40;
		
		
		short[][] nR = new short[mosHeight][mosWidth]; // declare a new array of red values of size height rows x width columns
		short[][] nG = new short[mosHeight][mosWidth]; // declare a new array of green values of size height rows x width columns
		short[][] nB = new short[mosHeight][mosWidth]; // declare a new array of blue values of size height rows x width columns
		short[][] nA = new short[mosHeight][mosWidth]; // declare a new array of alpha values of size height rows x width columns
		
		int aveRed = 0;
		int aveGreen = 0;
		int aveBlue = 0;
		
		for (int row = 0; row < height / diameter; row++) { // loop through all the rows
		    for (int col = 0; col < width / diameter; col++) { // for each row, loop through all the columns
		    	// This simply sets each color for each pixel to the original color at that pixel
		    	// so the filtered image will be the same as the original
		    	for(int i = 0; i < diameter; i++){
		    		for(int j = 0; j < diameter; j++){
		    			aveRed += oR[row * diameter + i][col * diameter + j];
		    			aveGreen += oR[row * diameter + i][col * diameter + j];
		    			aveBlue += oR[row * diameter + i][col * diameter + j];
		    		}
		    	}
		    	aveRed /= diameter;
		    	aveGreen /= diameter;
		    	aveBlue /= diameter;
		    	int distance = (int) Math.sqrt(Math.pow(aveRed - database.get(0).getRed(), 2) +
		    			Math.pow(aveGreen - database.get(0).getGreen(), 2) + 
		    			Math.pow(aveBlue - database.get(0).getBlue(), 2));
		    	int small = 0;
		    	for(int x = 1; x < database.size(); x++){
		    		if(distance > (int) Math.sqrt(Math.pow(aveRed - database.get(x).getRed(), 2) +
		    			Math.pow(aveGreen - database.get(x).getGreen(), 2) + 
		    			Math.pow(aveBlue - database.get(x).getBlue(), 2))){
		    			distance = (int) Math.sqrt(Math.pow(aveRed - database.get(x).getRed(), 2) +
				    			Math.pow(aveGreen - database.get(x).getGreen(), 2) + 
				    			Math.pow(aveBlue - database.get(x).getBlue(), 2));
		    			small = x;
		    		}
		    	}
		    	
		    	for(int i = 0; i < 40; i++){
		    		for(int a = 0; a < 40; a++){
		    			nR[row * 40 + i][col * 40 + a] = (short) database.get(small).getRed();
		    			nG[row * 40 + i][col * 40 + a] = (short) database.get(small).getGreen();
		    			nB[row * 40 + i][col * 40 + a] = (short) database.get(small).getBlue();
		    			nA[row * 40 + i][col * 40 + a] = (short) database.get(small).getRed();
		    		}
		    	}
		    }
		}
		
		filteredImage = new ImgProvider(); // initialize a new ImgProvider object
		filteredImage.setColors(nR, nG, nB, nA); // create the image from the arrays of new color values
		filteredImage.showPix("Color Template"); // display the image on screen with the given title
	}

	public ImgProvider getImgProvider() {
		return filteredImage;
	}

	public String getMenuLabel() {
		return "Color Template"; // this will be the name of the filter in the filters menu
	}
}

class ColorInfo {
	private int red;
	private int green;
	private int blue;
	
	public ColorInfo(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int getRed(){
		return red;
	}
	
	public void setRed(int num){
		red = num;
	}
	
	public int getGreen(){
		return green;
	}
	
	public void setGreen(int num){
		green = num;
	}
	
	public int getBlue(){
		return blue;
	}
	
	public void setBlue(int num){
		blue = num;
	}
	
}
