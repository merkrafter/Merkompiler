package com.merkrafter.parsing;

import com.merkrafter.lexing.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/****
 * This class serves as a test data provider for ParserTest.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/

public class ParserTestDataProvider {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * This method generates a stream of TokenWrappers that are valid assignments EXCEPT they're
     * lacking the ending semicolon.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> assignmentsWithoutSemicolon() {
        return Stream.of(
                // non-parameterized TokenWrappers
                Stream.of(
                        // direct assignment of a number to an identifier
                        // actual number value does not matter
                        new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                          .add(tokenFrom(TokenType.ASSIGN))
                                          .add(tokenFrom(TokenType.NUMBER)),

                        // direct assignment of an identifier to an identifier
                        new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                          .add(tokenFrom(TokenType.ASSIGN))
                                          .add(tokenFrom(TokenType.IDENT))),
                // parameterized TokenWrappers
                Stream.of(TokenType.PLUS, TokenType.MINUS, TokenType.TIMES, TokenType.DIVIDE)
                      .map(binOp -> new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(TokenType.ASSIGN))
                                                      .add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(binOp))
                                                      .add(tokenFrom(TokenType.NUMBER))))
                     // merge all the above (outer) streams
                     .flatMap(i -> i);
    }

    /**
     * This method returns the same TokenWrappers as
     * the {@link #assignmentsWithoutSemicolon() assignmentsWithoutSemicolon} method does but with
     * semicolons appended. It therefore returns only valid assignments.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> assignments() {
        return assignmentsWithoutSemicolon().map(tokenWrapper -> tokenWrapper.add(tokenFrom(
                TokenType.SEMICOLON)));
    }

    /**
     * This method generates a stream of TokenWrappers that are valid procedure calls.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> procedureCalls() {
        return internProcedureCalls().map(tokenWrapper -> tokenWrapper.add(tokenFrom(TokenType.SEMICOLON)));
    }

    /**
     * This method generates a stream of TokenWrappers that are valid intern procedure calls.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> internProcedureCalls() {
        return Stream.of(
                // a call of an intern procedure without arguments
                new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(TokenType.R_PAREN)),

                // a call of an intern procedure with a single identifier as its argument
                new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.R_PAREN)),

                // a call of an intern procedure with a single number as its argument
                new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(TokenType.NUMBER))
                                  .add(tokenFrom(TokenType.R_PAREN)),

                // a call of an intern procedure with two identifiers as arguments
                new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.COMMA))
                                  .add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.R_PAREN)),

                // a call of an intern procedure with an expression like a*b/2 as argument
                new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.PLUS))
                                  .add(tokenFrom(TokenType.IDENT))
                                  .add(tokenFrom(TokenType.DIVIDE))
                                  .add(tokenFrom(TokenType.NUMBER))
                                  .add(tokenFrom(TokenType.R_PAREN)));
    }

    /**
     * This method generates a stream of TokenWrappers that are valid if constructs.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> ifConstructs() {
        return Stream.of(
                // if constructs with all comparison operators between an ident and a number
                // the if and else bodies are simple assignments
                Stream.of(TokenType.LOWER_EQUAL,
                          TokenType.LOWER,
                          TokenType.EQUAL,
                          TokenType.GREATER,
                          TokenType.GREATER_EQUAL)
                      .map(cmpOp -> new TokenWrapper().add(tokenFrom(Keyword.IF))
                                                      .add(tokenFrom(TokenType.L_PAREN))
                                                      .add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(cmpOp))
                                                      .add(tokenFrom(TokenType.NUMBER))
                                                      .add(tokenFrom(TokenType.R_PAREN))

                                                      .add(tokenFrom(TokenType.L_BRACE))
                                                      .add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(TokenType.ASSIGN))
                                                      .add(tokenFrom(TokenType.NUMBER))
                                                      .add(tokenFrom(TokenType.SEMICOLON))
                                                      .add(tokenFrom(TokenType.R_BRACE))

                                                      .add(tokenFrom(Keyword.ELSE))
                                                      .add(tokenFrom(TokenType.L_BRACE))
                                                      .add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(TokenType.ASSIGN))
                                                      .add(tokenFrom(TokenType.NUMBER))
                                                      .add(tokenFrom(TokenType.SEMICOLON))
                                                      .add(tokenFrom(TokenType.R_BRACE))))
                     // merge all the above (outer) streams
                     .flatMap(i -> i);
    }

    /**
     * This method generates a stream of TokenWrappers that are valid while loops.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> whileLoops() {
        return Stream.of(
                // while loops with all comparison operators between an ident and a number
                // the body is a simple assignment
                Stream.of(TokenType.LOWER_EQUAL,
                          TokenType.LOWER,
                          TokenType.EQUAL,
                          TokenType.GREATER,
                          TokenType.GREATER_EQUAL)
                      .map(cmpOp -> new TokenWrapper().add(tokenFrom(Keyword.WHILE))
                                                      .add(tokenFrom(TokenType.L_PAREN))
                                                      .add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(cmpOp))
                                                      .add(tokenFrom(TokenType.NUMBER))
                                                      .add(tokenFrom(TokenType.R_PAREN))

                                                      .add(tokenFrom(TokenType.L_BRACE))
                                                      .add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(TokenType.ASSIGN))
                                                      .add(tokenFrom(TokenType.NUMBER))
                                                      .add(tokenFrom(TokenType.SEMICOLON))
                                                      .add(tokenFrom(TokenType.R_BRACE))))
                     // merge all the above (outer) streams
                     .flatMap(i -> i);
    }

    /**
     * This method generates a stream of TokenWrappers that are valid simple expressions.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> simpleExpressions() {
        return Stream.of(
                /*
                These TokenWrappers are independent from any operators
                 */
                Stream.of(
                        // an identifier with a single letter
                        new TokenWrapper().add(tokenFrom("a")),

                        // an identifier with two letters
                        new TokenWrapper().add(tokenFrom("ab")),

                        // a single number
                        new TokenWrapper().add(tokenFrom(5)),

                        // complex expression with multiplication, addition and subtraction
                        // " a*a + b*b - c*c
                        new TokenWrapper().add(tokenFrom("a"))
                                          .add(tokenFrom(TokenType.TIMES))
                                          .add(tokenFrom("a"))
                                          .add(tokenFrom(TokenType.PLUS))
                                          .add(tokenFrom("b"))
                                          .add(tokenFrom(TokenType.TIMES))
                                          .add(tokenFrom("b"))
                                          .add(tokenFrom(TokenType.MINUS))
                                          .add(tokenFrom("c"))
                                          .add(tokenFrom(TokenType.TIMES))
                                          .add(tokenFrom("c"))),
                /*
                These TokenWrappers are multiplied by using the 4 elementary arithmetic operations
                 */
                Stream.of(TokenType.PLUS, TokenType.MINUS, TokenType.TIMES, TokenType.DIVIDE)
                      .flatMap(operator -> Stream.of(
                              // simple offset of an ident and a number against each other
                              // with the ident being the first argument
                              new TokenWrapper().add(tokenFrom("a"))
                                                .add(tokenFrom(operator))
                                                .add(tokenFrom(5)),

                              // simple offset of two idents against each other
                              new TokenWrapper().add(tokenFrom("a"))
                                                .add(tokenFrom(operator))
                                                .add(tokenFrom("b")),

                              // simple offset of a number and an ident against each other
                              // with the ident being the second argument
                              new TokenWrapper().add(tokenFrom(3))
                                                .add(tokenFrom(operator))
                                                .add(tokenFrom("b")),

                              // simple offset of two numbers against each other
                              new TokenWrapper().add(tokenFrom(3))
                                                .add(tokenFrom(operator))
                                                .add(tokenFrom(5)),

                              // chain of 4 idents and 3 operators
                              new TokenWrapper().add(tokenFrom("a"))
                                                .add(tokenFrom(operator))
                                                .add(tokenFrom("b"))
                                                .add(tokenFrom(operator))
                                                .add(tokenFrom("c"))
                                                .add(tokenFrom(operator))
                                                .add(tokenFrom("d")))))

                     // merge all the above (outer) streams
                     .flatMap(i -> i);
    }

    /**
     * This method generates a stream of TokenWrappers that are valid expressions.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> expressions() {
        return Stream.of(
                /*
                Every simple expression is an expression as well
                 */
                simpleExpressions(),

                /*
                Comparisons of all simple expressions with an identifier
                 */
                Stream.of(TokenType.LOWER,
                          TokenType.LOWER_EQUAL,
                          TokenType.EQUAL,
                          TokenType.GREATER_EQUAL,
                          TokenType.GREATER).flatMap(

                        cmpOp ->
                                // simple expression first, then comparison operator, then ident
                                simpleExpressions().map(

                                        tokenWrapper -> tokenWrapper.add(tokenFrom(cmpOp))
                                                                    .add(tokenFrom("a"))))

        ).flatMap(i -> i);
    }

    /**
     * Creates a new Token from a TokenType by setting file name, line and position number to some
     * default values in order to make increase the readability of test cases.
     *
     * @return a basic Token with the given TokenType set
     */
    static Token tokenFrom(final TokenType type) {
        return new Token(type, null, 1, 1);
    }

    /**
     * Creates a new Token from a Keyword by setting file name, line and position number to some
     * default values in order to make increase the readability of test cases.
     *
     * @return a KeywordToken with the given Keyword set
     */
    static Token tokenFrom(final Keyword keyword) {
        return new KeywordToken(keyword, null, 1, 1);
    }

    /**
     * Creates a new Token from an identifier string by setting file name, line and position number
     * to some default values in order to make increase the readability of test cases.
     *
     * @return an IdentToken with the given identifier set
     */
    static Token tokenFrom(final String identifier) {
        return new IdentToken(identifier, null, 1, 1);
    }

    /**
     * Creates a new Token from a number string by setting file name, line and position number to
     * some default values in order to make increase the readability of test cases.
     *
     * @return a NumberToken with the given number set
     */
    static Token tokenFrom(final long number) {
        return new NumberToken(number, null, 1, 1);
    }

    /**
     * This class serves as a wrapper around a list of tokens as directly passing around lists/arrays
     * of tokens from the provider to the test methods does not work, as they're merged into one big
     * stream of Tokens.
     */
    static class TokenWrapper {
        private List<Token> tokenList;

        TokenWrapper() {
            tokenList = new LinkedList<>();
        }

        /**
         * Adds the given token at the end of the token list and returns this TokenWrapper instance.
         *
         * @param token a token to append to this token wrapper
         * @return itself in order to allow chaining
         */
        TokenWrapper add(final Token token) {
            tokenList.add(token);
            return this;
        }

        /**
         * @return the stored tokens as an array
         */
        Token[] getTokens() {
            return tokenList.toArray(new Token[0]);
        }

        /**
         * @return the string representation of the underlying token list
         */
        @Override
        public String toString() {
            return tokenList.toString();
        }
    }
}
