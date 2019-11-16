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
    private TokenType sym;
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
    public TokenType getSym() {
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
                sym = TokenType.NUMBER;
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
                sym = TokenType.IDENT;
                id = "";
                do {
                    id += ch;
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } while (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9');
                break;
            case '(':
                sym = TokenType.L_PAREN;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case ')':
                sym = TokenType.R_PAREN;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '{':
                sym = TokenType.L_BRACE;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '}':
                sym = TokenType.R_BRACE;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '[':
                sym = TokenType.L_SQ_BRACKET;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case ']':
                sym = TokenType.R_SQ_BRACKET;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '+':
                sym = TokenType.PLUS;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '-':
                sym = TokenType.MINUS;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '*':
                sym = TokenType.TIMES;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            case '/':
                sym = TokenType.DIVIDE;
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
                sym = TokenType.ASSIGN;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                if (ch == '=') {
                    sym = TokenType.EQUAL;
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } else {
                    charBuffer = Optional.of(ch);
                }
                break;
            case '<':
                sym = TokenType.LOWER;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                if (ch == '=') {
                    sym = TokenType.LOWER_EQUAL;
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } else {
                    charBuffer = Optional.of(ch);
                }
                break;
            case '>':
                sym = TokenType.GREATER;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                if (ch == '=') {
                    sym = TokenType.GREATER_EQUAL;
                    if (!this.loadNextCharSuccessfully()) {
                        return;
                    }
                } else {
                    charBuffer = Optional.of(ch);
                }
                break;
            case ';':
                sym = TokenType.SEMICOLON;
                if (!this.loadNextCharSuccessfully()) {
                    return;
                }
                break;
            default:
                sym = TokenType.OTHER;
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
                sym = TokenType.EOF;
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
