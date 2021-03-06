package imagelab;
import imagelab.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.util.ArrayList;
import java.util.Vector;
import java.io.*;

/**
    ImageLab is a platform for image filter development.  ImageLab
    begins by building a menu of all available filters (those .class files
    that implement the ImageFilter interface @see ImageFilter).
    @author Aaron Gordon
    @version 0.2 October 21, 2004
*/
public class ImageLab {

    JFrame frame;               // the apps main frame
    Vector<Object> images;      // holds all open images (imgProvider objects)
    String filterDir;           // the directory where filters live - unused right now
    Vector<Object> filters;     // holds filter objects
    ImgProvider impro;          // the current image provider
    ImgProvider lastImpro;      // the impro that last had a filter applied to it
    ImageLab theLab;            // holds a copy of this

    public static boolean debug = false; // when true, prints debug information to console

    public static void main(String [] arg) {
        ImageLab ilab = new ImageLab();
    }

    public ImageLab() {

        //filterDir = "filters";        //default, build in a way for user to change
        images = new Vector<Object>();
        theLab  = this;
        filters = new Vector<Object>(10);
        frame   = new JFrame();
        Container cpane = frame.getContentPane();

        File parDir = new File(".");            //set to current directory
        String imdir = ".";

        try {
            imdir = parDir.getCanonicalPath() +  "/images";
            System.out.print(ImageLab.debug ? "\n\nParent directory:  \n" + imdir : "");
        } catch (Exception excer) {
            System.out.println(excer.getMessage());
        }

        JMenuBar menubar = buildMenus();
        frame.setJMenuBar(menubar);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(700, 100, 200, 150);
        frame.setVisible(true);
    }

    private JMenuBar buildMenus() {

        JMenuBar mbar = new JMenuBar();
        JMenu file = new JMenu("File");

        mbar.add(file);
            JMenuItem open = new JMenuItem("open", 'o');
            file.add(open);
            open.addActionListener(makeOpenListener());
            JMenuItem save = new JMenuItem("save", 's');
            file.add(save);
            save.addActionListener(makeSaveListener());
            JMenuItem quit = new JMenuItem("quit", 'q');
            file.add(quit);
            quit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            
        JMenu photomosaicMenu = new JMenu("Photomosaic");
        JMenuItem item1 = new JMenuItem("Create Color DataBase");
        JMenuItem item2 = new JMenuItem("Create an Image database");
        mbar.add(photomosaicMenu);
        photomosaicMenu.add(item1);
        photomosaicMenu.add(item2);
        MyActionListener bob = new MyActionListener();
        item1.addActionListener(bob);
        item2.addActionListener(bob);

        JMenu filter = new JMenu("Filter");
        mbar.add(filter);
                /* find filters and build corresponding menu items
                 * look at each .class file in filterDir
                 * do the classForName stuff and enter into filter menu */
                System.out.print(ImageLab.debug ? "**********finding filters***********\n": "");
                FileSystemView fsv = FileSystemView.getFileSystemView();
                //File [] fil = fsv.getFiles(new File("."),true);
                File [] fil = fsv.getFiles(new File("filters"),true);
                System.out.print(ImageLab.debug ? "Found " + fil.length + " possible filters\n" : "");
                String clName = " ";        //holds name of class

                for (int k=0; k<fil.length; k++) {

                    if (fil[k].getName().endsWith("class")) {
                        Class cl;
                        Object obj;
                        try {
                            clName = fil[k].getName();
                            int spot = clName.lastIndexOf(".");
                            //clName = clName.substring(0,spot);
                            clName = "filters." + clName.substring(0,spot);
                            System.out.print(ImageLab.debug ? "Trying: " + clName + "\n": "");
                            cl = Class.forName(clName);
                            System.out.print(ImageLab.debug ? "Class for name is: " + cl + "\n": "");
                            Class [] interfaces = cl.getInterfaces();
                            System.out.print(ImageLab.debug ? "Number of interfaces is " + interfaces.length + "\n" : "");

                            boolean isFilter = false;

                            for (int j=0; j<interfaces.length; j++) {
                                System.out.print(ImageLab.debug ? "------->>>>>>>>>>" + interfaces[j].getName() + "\n" : "");
                                isFilter |= interfaces[j].getName().equals("imagelab.ImageFilter");
                            }

                            if (isFilter) {
                                obj = cl.newInstance();
                                System.out.print(ImageLab.debug ? "This is the one: " + fil[k].getName() + "\n" : "");
                                if (obj != null) filters.add(obj);
                                JMenuItem jmi = new JMenuItem(((ImageFilter)obj).getMenuLabel());
                                filter.add(jmi);
                                jmi.addActionListener(makeActionListener((ImageFilter)(filters.lastElement())));
                            }
                        } catch (Exception bigEx) {
                            System.err.println("Error in ImageProvider: " + k);
                            System.err.println(">>> " + bigEx);
                        } // try-catch
                    } // if
                } // for k

        return mbar;
    } // buildMenus
    
    private class MyActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Create Color DataBase")){
				FileDialog fd = new FileDialog(new Frame(), "Create a Color database", FileDialog.SAVE);
				fd.setVisible(true);
				FileWriter file;
				int numCol = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of colors: "));
				try {
					file = new FileWriter(fd.getDirectory() + fd.getFile());
					for(int i = 0; i < numCol; i++){
						for(int j = 0; j < 3; j++){
							file.write((int)(Math.random() * 256) + " ");
						}
						file.write("\r\n");
					}
					file.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else if(e.getActionCommand().equals("Create an Image database")){
				FileDialog fd = new FileDialog(new Frame(), "Create an Image database", FileDialog.SAVE);
				fd.setVisible(true);
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Select a folder of images");
				fc.setApproveButtonText("Ok");
				fc.showOpenDialog(null);
				// The next line will contain the folder path the user selected
				File imageFolder = new File(fc.getSelectedFile().getPath());
				ArrayList <File> files = findImageFiles(imageFolder);
				
				try {
					FileWriter file = new FileWriter(fd.getDirectory() + fd.getFile());
					for(int i = 0; i < files.size(); i++){
						ImgProvider img = new ImgProvider(files.get(i).getPath());
						img.readinImage();

						file.write((int)(average(img.getRed())) + " ");
						file.write((int)(average(img.getGreen())) + " ");
						file.write((int)(average(img.getBlue())) + " ");
						file.write(files.get(i).getPath());
						file.write("\r\n");
						
					}
					
					file.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
					
					
				
			}
		}
    	
    }
    
    private ArrayList<File> findImageFiles(File directory) {
		ArrayList<File> files = new ArrayList<File>();
	if (!directory.exists()) {
			return files;
		}
	File[] allFiles = directory.listFiles();
		for (File file : allFiles) {
			if (file.getName().toLowerCase().endsWith(".jpg") ||
				file.getName().toLowerCase().endsWith(".png") ||
				file.getName().toLowerCase().endsWith(".bmp") ||
				file.getName().toLowerCase().endsWith(".gif")) {
					files.add(file);
			}
		}
		return files;
	}
    
    private int average(short[][] list){
    	double average = 0;
    	for(int i = 0; i < list.length; i++){
    		for(int j = 0; j < list[i].length; j++){
    			average += list[i][j];
    		}
    	}
    	return (int) (average / list.length / list[0].length);
    }

    /** Builds a dedicated actionListener for the specific ImageFilter passed in. */
    public ActionListener makeActionListener (ImageFilter imf) {

        final ImageFilter theFilter = imf;

        return new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            if (impro == null) return;
            System.out.print(ImageLab.debug ? "Using impro number " + impro.getid() + "\n": "");
            theFilter.filter(impro);
            lastImpro = impro;
            impro = theFilter.getImgProvider();
            impro.setLab(theLab);
            images.add(impro);
        }};
    }

    // creates action listener for opening a file using a FileDialog
    public ActionListener makeOpenListener() {

        return new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                ImgProvider improvider; // an imgProvider to hold the image
                FileDialog fd;
                fd = new FileDialog(frame, "Pick an image", FileDialog.LOAD);
                fd.setVisible(true);
                String theFile = fd.getFile();
                String theDir = fd.getDirectory();
                if (fd.getFile() == null)
                    return;
                System.out.print(ImageLab.debug ? "The file's name is " + theDir + theFile + "\n" : "");
                improvider = new ImgProvider(theDir + theFile);
                improvider.setLab(theLab);
                //short [][] img = impro.getImage();
                System.out.print(ImageLab.debug ? "ImageLab:makeOpenListener - before showImage\n" : "");
                improvider.showImage(theDir + theFile);
                System.out.print(ImageLab.debug ? "ImageLab:makeOpenListener - after showImage" : "");
                images.add(improvider);
                impro = improvider; // current image provider is set

            } // actionPerformed
        }; // new ActionListener
    } // makeOpenListener

    // creates action listener for saving a file using a FileDialog
    public ActionListener makeSaveListener() {

        return new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                FileDialog fd;
                fd = new FileDialog(frame, "Choose a file name. The image format will be determined by the extension (jpeg, gif, png).", FileDialog.SAVE);
                fd.setVisible(true);
                String theDir = fd.getDirectory();
                String theFile = fd.getFile();

                if (fd.getFile() == null)
                    return;

                File fil = new File(theDir + theFile);

                try {
                    impro.save(fil);
                } catch (IOException ioex) {
                    System.err.println(ioex.getMessage());
                    System.err.println("Attempt to save image in " + theDir + theFile + " FAILED");
                }
            } // actionPerformed
        }; // new ActionListener
    } // makeOpenListener

    /** Marks an image as the one in focus.
    @param ip The ImgProvider responible for the image
    */
    public void setActive(ImgProvider ip) {
        impro = ip;
        System.out.print(ImageLab.debug ? "Setting impro to " + impro.getid() + "\n" : "");
    }
    
    public void setPreviousImageActive() {
        if (lastImpro != null) {
            setActive(lastImpro);
        } else if (images.indexOf(impro) > 0) {
            setActive((ImgProvider)images.get(images.indexOf(impro) - 1));
        }
    }
    
    public void removeImageProvider(ImgProvider ip) {
        if (ip == lastImpro) {
            lastImpro = null;
        }
        if (ip == impro) {
            setPreviousImageActive();
        }
        images.remove(ip);
    }
    
    
}







