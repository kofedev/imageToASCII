// Simple console "converter" from image to ASCII view
// Gregory readln.me
// ver 0.3
// command line format: java -jar PicView.jar filename [dimension] [i]
// dimension - is a width of ASCII output
// i - is a picture inversion option

package me.readln.etc;

import java.awt.image.BufferedImage;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Color;

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
    static double[][] pic;
    static double[][] picInAsciiSize;

    static int newPixelSize;
    static int picArrayForApproximateHeight;
    static int picArrayForApproximateWidth;

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

    static void getGrayscaleValueFromPixelsInImageRowAndWriteToPic (int indexCurrentCol) {
        for(int j = 0; j < width; j++) {
            Color color = new Color(image.getRGB(j, indexCurrentCol));
            pic[indexCurrentCol][j] = BRIGHT_ADDITION +
                                (color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722);
        }
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

    static double[][] giveMeSubArray (double[][] source, int rowCoordSource, int colCoordSource, int subArraySize) {

        double[][] subArray = giveMeArray(subArraySize, subArraySize);
        int subArrayRowIndex;
        int subArrayColIndex;
        subArrayRowIndex = 0;
        for (int row = rowCoordSource; row < rowCoordSource + subArraySize; row++) {
            subArrayColIndex = 0;
            for (int col = colCoordSource; col < colCoordSource + subArraySize; col++) {
                subArray[subArrayRowIndex][subArrayColIndex] = source[row][col];
                subArrayColIndex++;
            }
            subArrayRowIndex++;
        }
        return subArray;
    }

    static double getAverageFromArray (double[][] pixel) {

        double accumulator = 0;
        for (int row = 0; row < pixel.length; row++)
            for (int col = 0; col < pixel[0].length; col++)
                accumulator += pixel[row][col];

        return accumulator / (pixel.length * pixel[0].length);

    }

    static void approximate () {

        int picInAsciiSizeRowIndex;
        int picInAsciiSizeColIndex;

        picInAsciiSizeRowIndex = 0;
        for (int row = 0; row < picArrayForApproximateHeight; row += newPixelSize) {
            picInAsciiSizeColIndex = 0;

            for (int col = 0; col < picArrayForApproximateWidth; col += newPixelSize) {

                double[][] pixel = giveMeSubArray(pic, row, col, newPixelSize);

                picInAsciiSize[picInAsciiSizeRowIndex][picInAsciiSizeColIndex] =
                        getAverageFromArray(pixel);

                if (picInAsciiSizeColIndex < asciiWidth-1) picInAsciiSizeColIndex++;

            }

            if (picInAsciiSizeRowIndex < asciiHeight-1) picInAsciiSizeRowIndex++;
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

            // basic calculation

            width = image.getWidth();
            height = image.getHeight();

            asciiWidth = Math.min(width, asciiWidthDefault);
            asciiHeight = (asciiWidth * height) / width;
            newPixelSize = Math.min(width, height) / Math.min(asciiWidth, asciiHeight);

            picArrayForApproximateHeight =
                ( (height / newPixelSize) + ( (height % newPixelSize > 0) ? 1 : 0 ) )
                        * newPixelSize;

            picArrayForApproximateWidth =
                ( (width / newPixelSize) + ( (width % newPixelSize > 0) ? 1 : 0 ) )
                        * newPixelSize;

            // get working arrays

            pic = giveMeArray(picArrayForApproximateHeight, picArrayForApproximateWidth);

            picInAsciiSize = giveMeArray(asciiHeight, asciiWidth);

            // get each pixels grayscale value from the image
            for (int i = 0; i < height; i++)
                getGrayscaleValueFromPixelsInImageRowAndWriteToPic(i);

            // approximate from original size to ASCII-picture size
            approximate();

            // final printing
            printASCIIpicture();

            System.out.println();

        }

    }

}
