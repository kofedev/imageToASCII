package me.readln.etc.ascii;
/*
 * Class ImageToAscii converts picture (BufferedImage) to ASCII string
 * Gregory Kofe readln.me
 * Beta version
 * Project: Console Études
*/

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageToAscii implements ImageToAsciiCommon {

    private BufferedImage image;
    private static double[][] picInAsciiSize;
    private int asciiWidth;
    private int asciiHeight;
    private boolean inversion;
    private int width;
    private int height;
    private int asciiPictureWidth;
    private String output = PREFIX_FOR_THE_CONVERTED_IMAGE; // big string with the converted image

    /**
     * Constructor takes necessarily parameters and initializes internal variables
     * @param image - is the image to convert (BufferedImage)
     * @param asciiPictureWidth - is the width of the converted ASCII "picture"
     * @param inversion - is the flag of picture's inversion ("black" / "white")
     */
    public ImageToAscii(BufferedImage image, int asciiPictureWidth, boolean inversion) {
        this.image = image;
        this.asciiPictureWidth = asciiPictureWidth;
        this.inversion = inversion;
    }

    /**
     * This is a main method of the API
     * @return "big" string that contains converted picture
     */
    public String getStringWithConvertedPicture () {
        convertPictureToAscii();
        printAsciiPicture();
        return output;
    }

    private void convertPictureToAscii () {
        if (image != null) {

            width = image.getWidth();
            height = image.getHeight();
            asciiWidth = Math.min(width, asciiPictureWidth);
            asciiHeight = (asciiWidth * height) / width;

            BufferedImage resizedImage = new BufferedImage(asciiWidth, asciiHeight, image.getType());
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(image, 0, 0, asciiWidth, asciiHeight, null);
            g2d.dispose();

            picInAsciiSize = giveMeArray(asciiHeight, asciiWidth);

            for (int i = 0; i < asciiHeight; i++)
                getGrayscaleValueFromPixelsInImageRowAndWriteToPic(i, resizedImage, picInAsciiSize, asciiWidth);

        } else {
            //@ToDo exception !
        }
    }

    private void addToOutputString (String addition) {
        output = output + addition;
    }

    private void printRow(double step, int indexCurrentCol) {
        for (int j = 0; j < asciiWidth; j++) {
            char character = ' ';
            double calculatedGrayscale = Math.pow((picInAsciiSize[indexCurrentCol][j] / 256 ), 3);
            for (int q = 0; q < ASCII_PIXELS.length; q++) {

                if ( (step * q) < calculatedGrayscale && calculatedGrayscale <= (step*(q+1))) {
                    character = inversion ? ASCII_PIXELS[ASCII_PIXELS.length-(q+1)] : ASCII_PIXELS[q];
                    break;
                }
            }
            addToOutputString(character + " ");
        }
    }

    private void printAsciiPicture() {
        double step = 1.0 / ASCII_PIXELS.length;
        for (int i = 0; i < asciiHeight; i++) {
            addToOutputString("\n");
            printRow(step, i);
        }
    }

    private double[][] giveMeArray(int h, int w) {
        return new double[h][w];
    }

    private void getGrayscaleValueFromPixelsInImageRowAndWriteToPic (int indexCurrentCol,
                                                                    BufferedImage imgSource,
                                                                    double[][] picture,
                                                                    int width) {
        for(int j = 0; j < width; j++) {
            Color color = new Color(imgSource.getRGB(j, indexCurrentCol));
            picture[indexCurrentCol][j] = BRIGHT_ADDITION +
                    (color.getRed() * RED_RATIO + color.getGreen() * GREEN_RATIO + color.getBlue() * BLUE_RATIO);
        }
    }

}
