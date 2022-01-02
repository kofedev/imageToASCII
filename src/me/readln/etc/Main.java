package me.readln.etc;

// Simple console "converter" from image to ASCII view
// Gregory Kofe readln.me
// ver 0.5a
// Project: Console Études
// command line format: java -jar PicView.jar filename [-dN] [-i]
// where N is the dimension (width of ASCII output)
// i - is a picture inversion option

import me.readln.etc.ascii.ImageToAscii;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    final static String MESSAGE_THERE_IS_NO_FILENAME_IN_COMMANDLINE
            = "Please, input filename in command line!";
    final static String MESSAGE_DIMENSIONS_SHOULD_BE_UNDER_ZERO
            = "Dimensions should be > 0";

    static String filename;
    static boolean inversion = false;
    static int asciiPictureWidth = 50; // width (in symbols) of the converted picture
    static File file;
    static BufferedImage image = null;

    public static void main(String[] args) {

        // read arguments

        /// get filename
        try {
            filename = args[0];
        } catch (Exception e) {
            System.out.println(MESSAGE_THERE_IS_NO_FILENAME_IN_COMMANDLINE);
        }

        /// get other arguments
        String arg1 = "  ";
        String arg2 = "  ";
        if (args.length == 2 || args.length == 3) arg1 = args[1];
        if (args.length == 3) arg2 = args[2];
        if (arg1.charAt(1) == 'd' || arg2.charAt(1) == 'd') {
            String dimension = (arg1.charAt(1) == 'd') ? arg1.substring(2) : arg2.substring(2);
            int asciiWidthByUser = Integer.parseInt(dimension);
            if (asciiWidthByUser <= 0) {
                System.out.println(MESSAGE_DIMENSIONS_SHOULD_BE_UNDER_ZERO);
            } else {
                asciiPictureWidth = asciiWidthByUser;
            }
        }

        if (arg1.charAt(1) == 'i' || arg2.charAt(1) == 'i') inversion = true;

        // try to connect with the file
        file = new File(filename);

        // try to get an image from the file
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // conversion initialization
        ImageToAscii imageToAscii = new ImageToAscii(image, asciiPictureWidth, inversion);

        // print picture to console
        System.out.println(imageToAscii.getStringWithConvertedPicture());

    }

}
