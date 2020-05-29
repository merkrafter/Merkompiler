package com.merkrafter.lexing

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
                    in digits -> tokenizeNumber()
                    else -> OtherToken(ch.toString(), "", 0, 0)
                }

            } else {
                Token(TokenType.EOF, "", 0, 0)
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
}