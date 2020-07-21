package com.merkrafter.lexing

import java.util.*

/**
 * This tokenizer class takes a sequence of characters and splits it into Tokens.
 *
 * In case the underlying sequence has ended, infinitely many EOF tokens will be returned.
 * [hasNext] will still return false in that case.
 * Just keep in mind that it is therefore not possible to create a list of Tokens directly from this
 * CharTokenizer.
 *
 * @constructor Specifies the character sequence to work on
 *
 * @author merkrafter
 * @since v0.4.0
 */
class CharTokenizer(input: Sequence<Char>) : Iterator<Token> {

    // used instead of CharCategory.DECIMAL_DIGIT_NUMBER, because only ASCII should be recognized
    private val digits = '0'..'9'
    private val lowerAscii = 'a'..'z'
    private val upperAscii = 'A'..'Z'
    private val letters = lowerAscii + upperAscii
    private val whitespace = arrayOf(' ', '\n', '\t')
    private val specialChars = TokenType.values()
            .asSequence()
            .map { it.symbol }
            .filterNotNull()
            .map { it.first() }
            .toSet()

    /*
     * One could argue that passing an Iterator directly via the constructor would make more sense,
     * but as the user code will always start with a sequence, .iterator() has to be called at some
     * point and it is better to hide it here than in the algorithms.
     */
    private val inputIterator = input.iterator()

    /**
     * The last read character
     */
    private var ch: Char = ' '

    /**
     * Stores characters that were read from the [inputIterator] but could not be used immediately
     * as they do not comply with the current token type.
     */
    private val charQueue: Queue<Char> = LinkedList<Char>()

    override fun hasNext() = hasNextChar()

    /**
     * Returns the next Token based on this CharTokenizer's char sequence.
     * In case the underlying sequence has ended, an EOF token will be returned instead of throwing
     * an Exception.
     */
    override fun next(): Token =
            if (hasNextChar()) {
                ch = nextChar()
                when (ch) {
                    in letters -> tokenizeIdentifierOrKeyword()
                    in digits -> tokenizeNumber()
                    in specialChars -> tokenizeSpecialChars()
                    in whitespace -> {
                        tokenizeWhitespace(); next()
                    }
                    else -> OtherToken(ch.toString(), "", 0, 0)
                }

            } else {
                Token(TokenType.EOF, "", 0, 0)
            }

    /**
     * Reads characters from [inputIterator] until a character appears that is neither a digit or a
     * letter and returns an IdentToken or a KeywordToken.
     *
     * That non-digit and non-letter character may be EOF. This method assumes that [ch] contains a
     * character representing a letter already. The decision whether an IdentToken or a KeywordToken
     * is returned is made based on the [Keyword] enum and its members.
     */
    private fun tokenizeIdentifierOrKeyword(): Token {
        val ident = StringBuilder(ch.toString())
        while (hasNextChar()) {
            ch = nextChar()
            if (ch in letters || ch in digits) {
                ident.append(ch)
            } else {
                charQueue.add(ch)
                break
            }
        }

        val keyword = Keyword.values().firstOrNull { it.name == ident.toString().toUpperCase() }
        return if (keyword != null) {
            KeywordToken(keyword, "", 0, 0)
        } else {
            IdentToken(ident.toString(), "", 0, 0)
        }
    }


    /**
     * Reads characters from [inputIterator] until a non-digit character appears and returns a
     * NumberToken that represents that number.
     *
     * That non-digit character may be EOF. This method assumes that [ch] contains a character
     * representing a digit already.
     */
    private fun tokenizeNumber(): NumberToken {
        val num = StringBuilder(ch.toString())
        while (hasNextChar()) {
            ch = nextChar()
            if (ch in digits) {
                num.append(ch)
            } else {
                charQueue.add(ch)
                break
            }
        }
        return NumberToken(num.toString().toLong(), "", 0, 0)
    }

    /**
     * Interprets character from [ch] as a [TokenType] and returns a Token based on that.
     *
     * This method may read more characters from the underlying sequence to decide the [TokenType].
     */
    private fun tokenizeSpecialChars(): Token {
        val tokenType = when (ch) {
            '+' -> TokenType.PLUS
            '-' -> TokenType.MINUS
            '*' -> TokenType.TIMES
            '/' -> if (hasNextChar()) {
                ch = nextChar()
                when (ch) {
                    '/' -> {
                        // line comment
                        skipUntilNewline()
                        return next()
                    }
                    '*' -> {
                        /* block comment */
                        skipUntilBlockCommentEnd()
                        return next()
                    }
                    else -> {
                        charQueue.add(ch)
                        TokenType.DIVIDE
                    }
                }

            } else {
                TokenType.DIVIDE
            }
            '(' -> TokenType.L_PAREN
            ')' -> TokenType.R_PAREN
            '{' -> TokenType.L_BRACE
            '}' -> TokenType.R_BRACE
            '[' -> TokenType.L_SQ_BRACKET
            ']' -> TokenType.R_SQ_BRACKET
            ';' -> TokenType.SEMICOLON
            ',' -> TokenType.COMMA
            '=' -> if (hasNextChar()) {
                ch = nextChar()
                if (ch == '=') {
                    TokenType.EQUAL
                } else {
                    charQueue.add(ch)
                    TokenType.ASSIGN
                }
            } else {
                TokenType.ASSIGN
            }
            '<' -> if (hasNextChar()) {
                ch = nextChar()
                if (ch == '=') {
                    TokenType.LOWER_EQUAL
                } else {
                    charQueue.add(ch)
                    TokenType.LOWER
                }
            } else {
                TokenType.LOWER
            }
            '>' -> if (hasNextChar()) {
                ch = nextChar()
                if (ch == '=') {
                    TokenType.GREATER_EQUAL
                } else {
                    charQueue.add(ch)
                    TokenType.GREATER
                }
            } else {
                TokenType.GREATER
            }
            else -> {
                charQueue.add(ch)
                TokenType.OTHER
            }
        }
        return Token(tokenType, "", 0, 0)
    }

    /**
     * Reads characters from [inputIterator] until a non-whitespace character appears.
     */
    private fun tokenizeWhitespace() {
        while (hasNextChar()) {
            ch = nextChar()
            /*
             * Don't use !ch.isWhitespace() below here to be consistent with the when statement
             * in the [next] method.
             */
            if (ch !in whitespace) {
                charQueue.add(ch)
                break
            } // just consume; don't create token (yet)
        }
    }

    /**
     * Returns whether there are more characters to process.
     * These could be stored in the [charQueue] or in the [inputIterator]
     */
    private fun hasNextChar() = !charQueue.isEmpty() || inputIterator.hasNext()

    /**
     * Returns the next character to process.
     * Throws a [NoSuchElementException] iff [hasNextChar] returns false.
     */
    private fun nextChar(): Char = if (!charQueue.isEmpty()) {
        charQueue.remove()
    } else {
        inputIterator.next()
    }

    /**
     * Reads and skips characters until it encounters a '\n' character.
     * After this method, the cursor is placed in front of the first character of the next line.
     */
    private fun skipUntilNewline() {
        while (hasNextChar()) {
            ch = nextChar()
            if (ch == '\n') {
                break
            }
        }
    }

    /**
     * Reads and skips characters until it encounters a * and a / character.
     * After this method, the cursor is placed in front of the first character after the above
     * pattern.
     * This method assumes that a block comment started already.
     */
    private fun skipUntilBlockCommentEnd() {
        var lastCh = ch
        while (hasNextChar()) {
            ch = nextChar()
            if (lastCh == '*' && ch == '/') {
                break
            }
            lastCh = ch
        }
    }
}