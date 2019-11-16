package com.merkrafter.lexing;

import java.util.Iterator;
import java.util.Optional;

/****
 * This class can be used to tokenize an iterator of characters.
 * All possible types of tokens can be found in TokenType enum.
 *
 * To use this class, call processToken() and access the sym and id/num field afterwards.
 *
 * @author merkrafter
 ***************************************************************/
public class Scanner {
    // ATTRIBUTES
    //==============================================================
    /**
     * This is the character input stream that this Scanner tokenizes.
     */
    private final Iterator<Character> in;
    /**
     * This field stores the kind of the character that was read last.
     */
    private Token sym;
    /**
     * This field stores the character that was read last.
     */
    private char ch;
    /**
     * This field stores the name of the last identifier that this scanner found.
     */
    private String id;
    /**
     * This field stores the name of the last number that this scanner found.
     */
    private String num;
    /**
     * This field stores the current filename.
     */
    private String filename;
    /**
     * This field stores the line inside the current file.
     */
    private long line;
    /**
     * This field stores the position inside the current line.
     */
    private int position;

    // FIXME remove; only temporary
    private Token currToken;

    /**
     * This field stores characters that were found during a looking-forward action,
     * but can not be processed yet.
     */
    private Optional<Character> charBuffer;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new Scanner that is ready to tokenize the given character iterator.
     ***************************************************************/
    public Scanner(final Iterator<Character> in) {
        this.in = in;
        id = "";
        num = "";
        charBuffer = Optional.empty();
    }

    // GETTER
    //==============================================================
    public Token getSym() {
        return sym;
    }

    public String getId() {
        return id;
    }

    public String getNum() {
        return num;
    }

    // SETTER
    //==============================================================

    /**
     * Sets the current filename for this Scanner.
     * This method simply sets a String and does not check whether it is
     * an actual file name, the file exists or something similar.
     *
     * @param filename a String representing a file name
     */
    public void setFilename(final String filename) {
        this.filename = filename;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Reads the next symbol from the character iterator and sets the sym field accordingly.
     * It also sets the id and num fields if appropriate.
     * After sym is TokenType.EOF, this Scanner is done processing the iterator.
     */
    public void processToken() {
        if (charBuffer.isPresent()) {
            ch = charBuffer.get();
            charBuffer = Optional.empty();
        }
        while (ch <= ' ') {
            // This `true` argument is necessary since `loadNextCharSuccessfully` sets `ch` to 0 in case there is no
            // next character to read from `in`. So, this method call must set EOF in order to terminate the algorithm.
            if (!this.loadNextCharSuccessfully(true)) {
                return;
            }
        }
        switch (ch) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '7':
            case '8':
            case '9':
                sym = new Token(TokenType.NUMBER, filename, line, position);
                num = "";
                do {
                    num += ch;
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } while (ch >= '0' && ch <= '9');
                break;
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                sym = new Token(TokenType.IDENT, filename, line, position);
                id = "";
                do {
                    id += ch;
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } while (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9');
                break;
            case '(':
                sym = new Token(TokenType.L_PAREN, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case ')':
                sym = new Token(TokenType.R_PAREN, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '{':
                sym = new Token(TokenType.L_BRACE, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '}':
                sym = new Token(TokenType.R_BRACE, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '[':
                sym = new Token(TokenType.L_SQ_BRACKET, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case ']':
                sym = new Token(TokenType.R_SQ_BRACKET, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '+':
                sym = new Token(TokenType.PLUS, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '-':
                sym = new Token(TokenType.MINUS, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '*':
                sym = new Token(TokenType.TIMES, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '/':
                sym = new Token(TokenType.DIVIDE, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                if (ch == '*') {//this actually is a comment
                    char lastCh;
                    // skip comment ...
                    do {
                        lastCh = ch;
                        if (!this.loadNextCharSuccessfully()) {
                            return;
                        }
                    } while (!(lastCh == '*' && ch == '/'));
                    // ... then read next symbol
                    loadNextCharSuccessfully();
                    processToken();
                } else {
                    charBuffer = Optional.of(ch);
                }
                break;
            case '=':
                sym = new Token(TokenType.ASSIGN, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                if (ch == '=') {
                    sym = new Token(TokenType.EQUAL, filename, line, position);
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } else {
                    charBuffer = Optional.of(ch);
                }
                break;
            case '<':
                sym = new Token(TokenType.LOWER, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                if (ch == '=') {
                    sym = new Token(TokenType.LOWER_EQUAL, filename, line, position);
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } else {
                    charBuffer = Optional.of(ch);
                }
                break;
            case '>':
                sym = new Token(TokenType.GREATER, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                if (ch == '=') {
                    sym = new Token(TokenType.GREATER_EQUAL, filename, line, position);
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } else {
                    charBuffer = Optional.of(ch);
                }
                break;
            case ';':
                sym = new Token(TokenType.SEMICOLON, filename, line, position);
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            default:
                sym = new Token(TokenType.OTHER, filename, line, position);
                this.loadNextCharSuccessfully();
        }
    }

    // private methods
    //--------------------------------------------------------------

    /**
     * Tries to read the next character on `in` and store it in `ch`.
     * In case this is successful, `true` is returned.
     * Otherwise, `ch` is set to 0, `sym` is optionally set to `TokenType.EOF` and `false` is returned.
     *
     * @param setEOF whether this method sets `sym` to `TokenType.EOF` if there are no more characters
     * @return whether the next character could be loaded successfully
     */
    private boolean loadNextCharSuccessfully(boolean setEOF) {
        if (in.hasNext()) {
            ch = in.next();
            return true;
        } else {
            ch = (char) 0;
            if (setEOF) {
                sym = new Token(TokenType.EOF, filename, line, position);
            }
            return false;
        }
    }

    /**
     * Tries to read the next character on `in` and store it in `ch`.
     * In case this is successful, `true` is returned.
     * Otherwise, `ch` is set to 0 and `false` is returned.
     *
     * @return whether the next character could be loaded successfully
     */
    private boolean loadNextCharSuccessfully() {
        return loadNextCharSuccessfully(false);
    }
}
