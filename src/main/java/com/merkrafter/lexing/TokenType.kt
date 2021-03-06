package com.merkrafter.lexing

/****
 * This enum lists all types of terminals that can be encountered in JavaSST files.
 *
 * @author merkrafter
 ***************************************************************/
enum class TokenType(val symbol: String?) {
  KEYWORD(null),
  IDENT(null),
  NUMBER(null),
  PLUS("+"),
  MINUS("-"),
  TIMES("*"),
  DIVIDE("/"),
  ASSIGN("="),
  L_PAREN("("),
  R_PAREN(")"),
  L_BRACE("{"),
  R_BRACE("}"),
  L_SQ_BRACKET("["),
  R_SQ_BRACKET("]"),
  COMMA(","),
  SEMICOLON(";"),
  EQUAL("=="),
  LOWER_EQUAL("<="),
  LOWER("<"),
  GREATER_EQUAL(">="),
  GREATER(">"),
  EOF(null),
  OTHER(null)
}
