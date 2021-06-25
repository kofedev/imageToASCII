// Simple console "converter" from image to ASCII view
// Gregory readln.me
// ver 0.4
// command line format: java -jar PicView.jar filename dimension i
// dimension - is a width of ASCII output
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
    static Integer asciiWidth;
    static Integer asciiHeight;
    static double[][] picInAsciiSize;

    final static String MESSAGE_THERE_IS_NO_FILENAME_IN_COMMANDLINE = "Please, input filename in command line!";
    final static String MESSAGE_DIMENSIONS_SHOULD_BE_UNDER_ZERO     = "Dimensions should be > 0";

    static double[][] giveMeArray(int h, int w) {
        return new double[h][w];
    }

    static void readArguments (String[] args) {

        try {
            filename = args[0];
        } catch (Exception e) {
            System.out.println(MESSAGE_THERE_IS_NO_FILENAME_IN_COMMANDLINE);
        }

        try {
            int asciiWidthByUser = Integer.parseInt(args[1]);
            if (asciiWidthByUser <= 0) {
                System.out.println(MESSAGE_DIMENSIONS_SHOULD_BE_UNDER_ZERO);
            }
            asciiWidthDefault = asciiWidthByUser;
        } catch (Exception e) { }

        try {
            String inversionUserDecision = args[2];
            if (inversionUserDecision.equals("i")) inversion = true;
        } catch (Exception e) { }

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
                    (color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722);
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
