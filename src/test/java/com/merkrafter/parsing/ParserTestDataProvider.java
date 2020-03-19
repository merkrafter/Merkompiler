package com.merkrafter.parsing;

import com.merkrafter.lexing.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/****
 * This class serves as a test data provider for ParserTest.
 * All non-private methods of this class are static and return a stream
 * of TokenWrappers (essentially lists of Tokens) that satisfy certain syntax criteria.
 * <p>
 * The methods are organized hierarchically which means that {@link #statements()} joins the tokens
 * from {@link #assignments()}, {@link #returnStatements()} and some other, for instance.
 * <p>
 * This file also defines some static methods that allow the fast creation of
 * tokens without the need to specify the filename, line and position numbers
 * as they are not relevant for the syntax analysis tests.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/

class ParserTestDataProvider {
    // CONSTANTS
    //==============================================================
    private static final String FUNC = "func";
    private static final String VAR = "var";

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * This method generates a stream of TokenWrappers that are valid statements.
     * They are a union of assignments, procedure calls, if, while and return statements.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> statements() {
        return Stream.of(assignments(),
                         procedureCalls(),
                         ifConstructs(),
                         whileLoops(),
                         returnStatements()).flatMap(i -> i);
    }

    /**
     * This method generates a stream of TokenWrappers that are valid assignments EXCEPT they're
     * lacking the ending semicolon.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> assignmentsWithoutSemicolon() {
        return simpleExpressions().map(
                // add all simple expressions at the end of "a = "
                expression -> new TokenWrapper().add(tokenFrom("a"))
                                                .add(tokenFrom(TokenType.ASSIGN))
                                                .add(expression));
    }

    /**
     * This method generates a stream of TokenWrappers that are valid assignments.
     * This method returns the same TokenWrappers as
     * the {@link #assignmentsWithoutSemicolon() assignmentsWithoutSemicolon} method does, but with
     * semicolons appended.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> assignments() {
        return assignmentsWithoutSemicolon().map(tokenWrapper -> tokenWrapper.add(tokenFrom(
                TokenType.SEMICOLON)));
    }

    /**
     * This method generates a stream of TokenWrappers that are valid procedure calls.
     * This method returns the same TokenWrappers as the {@link #internProcedureCalls()}} method
     * does, but with semicolons appended.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> procedureCalls() {
        return internProcedureCalls().map(tokenWrapper -> tokenWrapper.add(tokenFrom(TokenType.SEMICOLON)));
    }

    /**
     * This method generates a stream of TokenWrappers that are valid intern procedure calls.
     * These include calls with empty argument lists, one and two element arguments lists, and
     * expressions as arguments.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> internProcedureCalls() {
        return Stream.of(
                // a call of an intern procedure without arguments
                new TokenWrapper().add(tokenFrom(FUNC))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(TokenType.R_PAREN)),

                // a call of an intern procedure with a single identifier as its argument
                new TokenWrapper().add(tokenFrom(FUNC))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(VAR))
                                  .add(tokenFrom(TokenType.R_PAREN)),

                // a call of an intern procedure with a single number as its argument
                new TokenWrapper().add(tokenFrom(FUNC))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(TokenType.NUMBER))
                                  .add(tokenFrom(TokenType.R_PAREN)),

                // a call of an intern procedure with two identifiers as arguments
                new TokenWrapper().add(tokenFrom(FUNC))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(VAR))
                                  .add(tokenFrom(TokenType.COMMA))
                                  .add(tokenFrom(VAR))
                                  .add(tokenFrom(TokenType.R_PAREN)),

                // a call of an intern procedure with an expression like a*b/2 as argument
                new TokenWrapper().add(tokenFrom(FUNC))
                                  .add(tokenFrom(TokenType.L_PAREN))
                                  .add(tokenFrom(VAR))
                                  .add(tokenFrom(TokenType.PLUS))
                                  .add(tokenFrom(VAR))
                                  .add(tokenFrom(TokenType.DIVIDE))
                                  .add(tokenFrom(TokenType.NUMBER))
                                  .add(tokenFrom(TokenType.R_PAREN)));
    }

    /**
     * This method generates a stream of TokenWrappers that are valid if constructs.
     * In particular, the if and else bodies are single assignments, while the comparison is being
     * made between an identifier and a number.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> ifConstructs() {
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
     * In particular, the body is a single assignment, while the comparison is being
     * made between an identifier and a number.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> whileLoops() {
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
     * This method generates a stream of TokenWrappers that are valid return statements.
     * These include a single return statement without return value as well as returning
     * {@link #simpleExpressions()}.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> returnStatements() {
        return Stream.of(
                /*
                unparameterized data
                 */
                Stream.of(
                        // return keyword without an value to return
                        new TokenWrapper().add(tokenFrom(Keyword.RETURN))
                                          .add(tokenFrom(TokenType.SEMICOLON))),
                /*
                parameterized data
                 */
                simpleExpressions().map(
                        // return keyword with simple expressions as return values
                        tokenWrapper -> new TokenWrapper().add(tokenFrom(Keyword.RETURN))
                                                          .add(tokenWrapper)
                                                          .add(tokenFrom(TokenType.SEMICOLON))))
                     // merge all the above (outer) streams
                     .flatMap(i -> i);
    }

    /**
     * This method generates a stream of TokenWrappers that are valid simple expressions.
     * These include pretty basic expressions as single identifiers and numbers, as well as more
     * complex expressions that include multiple operators and procedure calls.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> simpleExpressions() {
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

                        // complex expression including intern procedure calls
                        // 2*fib(n-1) + fib(n-2)
                        new TokenWrapper().add(tokenFrom(2))
                                          .add(tokenFrom(TokenType.TIMES))
                                          .add(tokenFrom("fib"))
                                          .add(tokenFrom(TokenType.L_PAREN))
                                          .add(tokenFrom("n"))
                                          .add(tokenFrom(TokenType.MINUS))
                                          .add(tokenFrom(1))
                                          .add(tokenFrom(TokenType.R_PAREN))
                                          .add(tokenFrom(TokenType.PLUS))
                                          .add(tokenFrom("fib"))
                                          .add(tokenFrom(TokenType.L_PAREN))
                                          .add(tokenFrom("n"))
                                          .add(tokenFrom(TokenType.MINUS))
                                          .add(tokenFrom(2))
                                          .add(tokenFrom(TokenType.R_PAREN)),

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
     * These include {@link #simpleExpressions()} as well as comparisons between those and idents.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    static Stream<TokenWrapper> expressions() {
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


    // support methods
    //--------------------------------------------------------------

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


    // inner classes
    //--------------------------------------------------------------

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
         * Adds all tokens of the given token wrapper at the end of this wrapper's token list and
         * returns this TokenWrapper instance.
         *
         * @param tokenWrapper a TokenWrapper to append at the end of this wrapper
         * @return itself in order to allow chaining
         */
        TokenWrapper add(final TokenWrapper tokenWrapper) {
            tokenList.addAll(tokenWrapper.tokenList);
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
