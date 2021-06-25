// Simple console "converter" from image to ASCII view
// Gregory readln.me
// ver 0.4a
// command line format: java -jar PicView.jar filename [-dN] [-i]
// where N is the dimension (width of ASCII output)
// i - is a picture inversion option

package me.readln.etc;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {

    final static char[] ASCII_PIXELS = {' ', '.', ',', '\'', '^', ':', ';', '±', '+', '=',
                                        '*', 'a', 'c', 'b', '1', '/', '7', '?', '2', '3', '4',
                                        '5', '6', 'A', 'C', 'D', '%', '#', '&', '§', '$', '0',
                                        'Q', '8', '@'};

    static String filename;
    static boolean inversion = false;
    static int asciiWidthDefault = 50;
    static File file;
    static BufferedImage image = null;
    static final double BRIGHT_ADDITION = 0.5;

    static int width;
    static int height;
    static int asciiWidth;
    static int asciiHeight;
    static double[][] picInAsciiSize;

    final static String MESSAGE_THERE_IS_NO_FILENAME_IN_COMMANDLINE = "Please, input filename in command line!";
    final static String MESSAGE_DIMENSIONS_SHOULD_BE_UNDER_ZERO     = "Dimensions should be > 0";

    final static double RED_RATIO   = 0.2126;
    final static double GREEN_RATIO = 0.7152;
    final static double BLUE_RATIO  = 0.0722;

    static double[][] giveMeArray(int h, int w) {
        return new double[h][w];
    }

    static void readArguments (String[] args) {

        // get filename

        try {
            filename = args[0];
        } catch (Exception e) {
            System.out.println(MESSAGE_THERE_IS_NO_FILENAME_IN_COMMANDLINE);
        }

        // get other arguments

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
                asciiWidthDefault = asciiWidthByUser;
            }
        }

        if (arg1.charAt(1) == 'i' || arg2.charAt(1) == 'i') inversion = true;

    }


    static void linkFile() throws IIOException {
        file = new File(filename);
    }


    static void printRow(double step, int indexCurrentCol) {
        for (int j = 0; j < asciiWidth; j++) {
            char character = ' ';
            double calculatedGrayscale = Math.pow((picInAsciiSize[indexCurrentCol][j] / 256 ), 3);
            for (int q = 0; q < ASCII_PIXELS.length; q++) {

                if ( (step * q) < calculatedGrayscale && calculatedGrayscale <= (step*(q+1))) {
                    character = inversion ? ASCII_PIXELS[ASCII_PIXELS.length-(q+1)] : ASCII_PIXELS[q];
                    break;
                }
            }
            System.out.print(character + " ");
        }
    }

    static void printASCIIpicture() {
        double step = 1.0 / ASCII_PIXELS.length;
        for (int i = 0; i < asciiHeight; i++) {
            System.out.println();
            printRow(step, i);
        }
    }


    static void getGrayscaleValueFromPixelsInImageRowAndWriteToPic (int indexCurrentCol,
                                                                    BufferedImage imgSource,
                                                                    double[][] picture,
                                                                    int width) {
        for(int j = 0; j < width; j++) {
            Color color = new Color(imgSource.getRGB(j, indexCurrentCol));
            picture[indexCurrentCol][j] = BRIGHT_ADDITION +
                    (color.getRed() * RED_RATIO + color.getGreen() * GREEN_RATIO + color.getBlue() * BLUE_RATIO);
        }
    }


    public static void main(String[] args) {

        readArguments(args);

        try {
            linkFile();
        } catch (IIOException e) {
            e.printStackTrace();
        }

        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (image != null) {

            width = image.getWidth();
            height = image.getHeight();

            asciiWidth = Math.min(width, asciiWidthDefault);
            asciiHeight = (asciiWidth * height) / width;

            BufferedImage resizedImage = new BufferedImage(asciiWidth, asciiHeight, image.getType());
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(image, 0, 0, asciiWidth, asciiHeight, null);
            g2d.dispose();

            picInAsciiSize = giveMeArray(asciiHeight, asciiWidth);

            for (int i = 0; i < asciiHeight; i++)
                getGrayscaleValueFromPixelsInImageRowAndWriteToPic(i, resizedImage, picInAsciiSize, asciiWidth);

            printASCIIpicture();

            System.out.println();

        }

    }

}
