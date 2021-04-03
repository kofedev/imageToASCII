//
// Simple console "converter" from image to ASCII view
// Gregory Skomorovsky, readln.me
// ver 0.2
// command line format: java -jar PicView.jar filename [dimension] [i]
// dimension - is a width of ASCII output
// i - is an inversion option
//

package me.readln.etc;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.util.*;

public class Main {

    private static void clear (float[][] array, int wi, int he) {
        for (int i = 0; i < he; i++)
            for (int j = 0; j < wi; j++) {
                array[i][j] = 0.0f;
            }
    }

    public static void main(String[] args) {

        String filename;
        int asciiWidthDefault;
        boolean inversion = false;

        try {
            filename = args[0];
        } catch (Exception e) {
            System.out.println("Please, input filename in command line!");
            return;
        }

        try {
            int asciiWidthByUser = Integer.parseInt(args[1]);
            if (asciiWidthByUser <= 0) {
                System.out.println("Dimensions should be > 0");
                return;
            }
            asciiWidthDefault = asciiWidthByUser;
        } catch (Exception e) {
            asciiWidthDefault = 50; // width of ASCII terminal (in characters) by default
        }

        try {
            String inversionUserDecision = args[2];
            if (inversionUserDecision.equals("i")) inversion = true;
        } catch (Exception e) {

        }


        // ASCII symbols for picture "pixels"

        Map<Integer, Character> asciiGreyscale = new TreeMap<>();

        asciiGreyscale.put(0,   ' ');
        asciiGreyscale.put(1,   '.');
        asciiGreyscale.put(2,   '\'');
        asciiGreyscale.put(3,   '^');
        asciiGreyscale.put(4,   ',');
        asciiGreyscale.put(5,   ':');
        asciiGreyscale.put(6,   ';');
        asciiGreyscale.put(7,   '±');
        asciiGreyscale.put(8,   '*');
        asciiGreyscale.put(9,   '1');
        asciiGreyscale.put(10,  '7');
        asciiGreyscale.put(11,  '2');
        asciiGreyscale.put(12,  '3');
        asciiGreyscale.put(13,  '%');
        asciiGreyscale.put(14,  '#');
        asciiGreyscale.put(15,  '&');
        asciiGreyscale.put(16,  '$');
        asciiGreyscale.put(17,  '0');
        asciiGreyscale.put(18,  '@');
        asciiGreyscale.put(19,  '8');

        File file;

        try {
            file = new File(filename);
        } catch (Exception e) {
            System.out.println("Can't read input file...");
            return;
        }

        BufferedImage image = null;

        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Can't read input file...");
            e.printStackTrace();
            return;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        Integer asciiWidth  = asciiWidthDefault;
        Integer asciiHeight = (asciiWidthDefault * height) / width;

        float[][] pic = new float[height][width];
        float[][] picInAsciiSize = new float[asciiHeight][asciiWidth];

        // clear

        clear(pic, width, height);
        clear(picInAsciiSize, asciiWidth, asciiHeight);

        // get grayscale

        final float BRIGHT_ADDITION = 0.5f;

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                Color color = new Color(image.getRGB(j, i));
                pic[i][j] = ((float)( color.getRed() * 0.2126f + color.getGreen() * 0.7152f + color.getBlue() * 0.0722f)) + BRIGHT_ADDITION;

            }

        int stepOnWidth = width / asciiWidth;
        int stepOnHeight = height / asciiHeight;

        // approximate

        int h = 0;
        for ( int i = 0; i < height; ) {

            int w = 0;
            for (int j = 0; j < width; ) {

                float value = 0;
                int counter = 0;
                for (int k = 0; k < stepOnHeight; k++)
                    for (int m = 0; m < stepOnWidth; m++) {

                        value += pic[i + k][j + m];
                        counter++;
                    }

                picInAsciiSize[h][w] = value / counter;

                j += stepOnWidth;

                if (j >= width) break;
                w++;
                if (w >= asciiWidth) break;
            }
            i+=stepOnHeight;
            if (i >= height) break;
            h++;
            if (h >= asciiHeight) break;
        }

        // display

        float step = 1.0f / asciiGreyscale.size();

        for (int i = 0; i < asciiHeight; i++) {

            System.out.println();

            for (int j = 0; j < asciiWidth; j++) {

                Character character = ' ';

                double calculatedGrayscale = Math.pow((picInAsciiSize[i][j] / 256 ), 3);

                for (int q = 0; q < asciiGreyscale.size(); q++) {

                    if ( (double)(step * q) < calculatedGrayscale && calculatedGrayscale <= (double)(step*(q+1))) {
                        character = inversion ? asciiGreyscale.get(asciiGreyscale.size()-(q+1)) : asciiGreyscale.get(q);
                        break;
                    }
                }

                System.out.print(character + " ");

            }
        }

    }

}
