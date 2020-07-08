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

    override fun hasNext(): Boolean = inputIterator.hasNext()

    /**
     * Returns the next Token based on this CharTokenizer's char sequence.
     * In case the underlying sequence has ended, an EOF token will be returned instead of throwing
     * an Exception.
     */
    override fun next(): Token =
            if (inputIterator.hasNext()) {
                ch = inputIterator.next()
                when (ch) {
                    in letters -> tokenizeIdentifierOrKeyword()
                    in digits -> tokenizeNumber()
                    in specialChars -> tokenizeSpecialChars()
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
        while (inputIterator.hasNext()) {
            ch = inputIterator.next()
            if (ch in letters || ch in digits) {
                ident.append(ch)
            } else {
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
        while (inputIterator.hasNext()) {
            ch = inputIterator.next()
            if (ch in digits) {
                num.append(ch)
            } else {
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
            '/' -> TokenType.DIVIDE
            '(' -> TokenType.L_PAREN
            ')' -> TokenType.R_PAREN
            '{' -> TokenType.L_BRACE
            '}' -> TokenType.R_BRACE
            '[' -> TokenType.L_SQ_BRACKET
            ']' -> TokenType.R_SQ_BRACKET
            ';' -> TokenType.SEMICOLON
            ',' -> TokenType.COMMA
            '=' -> if (nextFromInput() == '=') TokenType.EQUAL else TokenType.ASSIGN
            '<' -> if (nextFromInput() == '=') TokenType.LOWER_EQUAL else TokenType.LOWER
            '>' -> if (nextFromInput() == '=') TokenType.GREATER_EQUAL else TokenType.GREATER
            else -> TokenType.OTHER
        }
        return Token(tokenType, "", 0, 0)
    }

    private fun nextFromInput(): Char? = if (inputIterator.hasNext()) inputIterator.next() else null
}