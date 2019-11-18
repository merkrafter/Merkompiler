package com.merkrafter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/****
 * This class represents an character iterator over a file.
 * It currently only supports the default file encoding.
 *
 * @author merkrafter
 ***************************************************************/
public class Input implements Iterator<Character> {
    // CONSTANTS
    //==============================================================
    /**
     * This is the value returned by the BufferedReader if the EOF is reached.
     */
    private static final int ENDOFFILE = -1;
    /**
     * This is the error value for this class that is used if an IOException occurred, for instance.
     */
    private static final int ERROR = -2;

    // ATTRIBUTES
    //==============================================================
    /**
     * This reader is used to read the file that is passed via the constructor.
     */
    private final BufferedReader reader;
    /**
     * This field stores the last character that was read from the file.
     * As a BufferedReader's read() method returns an integer, this field uses that type as well.
     */
    private int lastRead;


    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new Input instance using a name of a file to read from.
     * @param filename The path to the file that should be iterated over.
     * @throws FileNotFoundException if the path does not point to an existing file
     ***************************************************************/
    Input(final String filename) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(filename));
        next(); // read a first character
    }

    /**
     * Checks whether EOF is reached already or whether there was an error.
     *
     * @return whether there is a next character to read
     */
    @Override
    public boolean hasNext() {
        return lastRead != ENDOFFILE && lastRead != ERROR;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Returns the character that was read most recently. Also loads the next character in the file.
     *
     * @return latest character in the file
     */
    @Override
    public Character next() {
        int returnValue = lastRead;

        try {
            lastRead = reader.read();
        } catch (IOException e) {
            lastRead = ERROR;
        }
        return (char) returnValue;
    }
}
