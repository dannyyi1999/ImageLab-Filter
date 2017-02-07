package filters;
import imagelab.*;

public class ConvolutionBW implements ImageFilter {

	// Attribute to store the modified image
	ImgProvider filteredImage;

	public void filter (ImgProvider ip) {

		// Grab the pixel information and put it into a 2D array
		short[][] im = ip.getBWImage();
		short[][] mask = {{-1, -1, 0},{-1, 0, 1},{0, 1, 1}};

		// Make variables for image height and width
		int height = im.length;
		int width  = im[0].length;

		// Create a new array to store the modified image
		short[][] newImage = new short[height][width];

		// Loop through the original image and store the modified
		// version in the newImage array
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				double sum = 0;

				for (int i = 0; i < mask.length; i++) {
				   for (int j = 0; j < mask.length; j++) {
					   if(row + i - 1 < 0 || col + j - 1 < 0 || row + i > height || col + j > width){
						   continue;
					   }
				       sum += im[row+i - 1][col+j - 1]*mask[i][j];
				      
				   }
				}
				sum += 127;
				
				if(sum > 255){
					sum = 255;
				}else if(sum < 0){
					sum = 0;
				}
				newImage[row][col] = (short) (sum);
			}
		}

		// Create a new ImgProvider and set the filtered image to our new image
		filteredImage = new ImgProvider();
		filteredImage.setBWImage(newImage);

		// Show the new image in a new window with title "Flipped Horizontally"
		filteredImage.showPix("convolutionized");
	}

	public ImgProvider getImgProvider() {
		return filteredImage;
	}

	// This is what users see in the Filter menu
	public String getMenuLabel() {
		return "Convolution (BW)";
	}

}
