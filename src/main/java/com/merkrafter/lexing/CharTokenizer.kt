package com.merkrafter.lexing

/**
 * This tokenizer class takes a sequence of characters and splits it into Tokens.
 *
 * In case the underlying sequence has ended, infinitely many EOF tokens will be returned.
 * [hasNext] will still return [false] in that case.
 * Just keep in mind that it is therefore not possible to create a list of Tokens directly from this
 * CharTokenizer.
 *
 * @constructor Specifies the character sequence to work on
 *
 * @author merkrafter
 * @since v0.4.0
 */
class CharTokenizer(private val input: Sequence<Char>) : Iterator<Token> {

    /*
     * One could argue that passing an Iterator directly via the constructor would make more sense,
     * but as the user code will always start with a sequence, .iterator() has to be called at some
     * point and it is better to hide it here than in the algorithms.
     */
    private val inputIterator = input.iterator()

    override fun hasNext(): Boolean = inputIterator.hasNext()

    /**
     * Returns the next Token based on this CharTokenizer's char sequence.
     * In case the underlying sequence has ended, an EOF token will be returned instead of throwing
     * an Exception.
     */
    override fun next(): Token =
            if (inputIterator.hasNext()) {
                OtherToken(inputIterator.next().toString(), "", 0, 0)
            } else {
                Token(TokenType.EOF, "", 0, 0)
            }
}