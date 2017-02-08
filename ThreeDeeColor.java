package filters;
import javax.swing.JOptionPane;

import imagelab.*;

public class ThreeDeeColor implements ImageFilter {

	ImgProvider filteredImage;

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
		int offset = Integer.parseInt(JOptionPane.showInputDialog("Enter the amount of offset: "));
		int height = oR.length; // height is the number of rows which is the length of the outer array
		int width = oR[0].length; // width is the number of columns which is the length of each inner array
		
		short[][] nR = new short[height][width]; // declare a new array of red values of size height rows x width columns
		short[][] nG = new short[height][width]; // declare a new array of green values of size height rows x width columns
		short[][] nB = new short[height][width]; // declare a new array of blue values of size height rows x width columns
		short[][] nA = new short[height][width]; // declare a new array of alpha values of size height rows x width columns
		
		for (int row = 0; row < height; row++) { // loop through all the rows
		    for (int col = 0; col < width; col++) { // for each row, loop through all the columns
		    	if(col + offset < width){
			        nR[row][col] = (short) (oR[row][col + offset]);
		    	}else {
		    		nR[row][col] = oR[row][col];
		    	}
		        nG[row][col] = oG[row][col];
		        if(col - offset >= 0){
			        nB[row][col] = (short) (oB[row][col - offset]);
		        }else {
		        	nB[row][col] = oB[row][col];
		        }
		        nA[row][col] = oA[row][col];
		        
		    }
		}
		
		filteredImage = new ImgProvider(); // initialize a new ImgProvider object
		filteredImage.setColors(nR, nG, nB, nA); // create the image from the arrays of new color values
		filteredImage.showPix("Three D Color"); // display the image on screen with the given title
	}

	public ImgProvider getImgProvider() {
		return filteredImage;
	}

	public String getMenuLabel() {
		return "3D Color"; // this will be the name of the filter in the filters menu
	}
}
        
