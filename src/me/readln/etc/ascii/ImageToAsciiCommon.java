package me.readln.etc.ascii;

public interface ImageToAsciiCommon {

    /**
     * Symbols which are used as "pixels" for visualization the picture in ASCII form
     */
    final static char[] ASCII_PIXELS =
            {' ', '.', ',', '\'', '^', ':', ';', '±', '+', '=',
            '*', 'a', 'c', 'b', '1', '/', '7', '?', '2', '3', '4',
            '5', '6', 'A', 'C', 'D', '%', '#', '&', '§', '$', '0',
            'Q', '8', '@'};

    /**
     * Constants for RGB conversion
     */
    final static double RED_RATIO       = 0.2126;
    final static double GREEN_RATIO     = 0.7152;
    final static double BLUE_RATIO      = 0.0722;
    static final double BRIGHT_ADDITION = 0.5;

    /**
     * Welcome message before ASCII-version of the picture
     */
    final static String PREFIX_FOR_THE_CONVERTED_IMAGE = "Picture was converted to ASCII:\n\n";

}
