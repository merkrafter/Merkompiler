# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Creation of a Control Flow Graph; support for --graphical
- New tokenizer implementation to comply with Iterator interface

### Changed
- Parser now works on an Iterator instead of concrete Scanner implementation

### Removed
- Previous Scanner implementation

## [0.4.0] - 2020-03-22
### Added
- Error messages for semantics errors
  - type system violations
  - missing return statements
  - multiple declarations of variables or procedures
  - assignments to constant fields
- CLI argument: --grapical

### Changed
- Error messages to make them more helpful

### Fixes
- Tokenizer could not recognize digit 6

## [0.3.0] - internal only

## [0.2.0] - 2020-01-07
### Added
- Ability to parse tokens with supporting the full grammar
- CLI argument: --skip-after
- Ability for tokenizer to recognize commas


## [0.1.0] - 2019-11-18
### Added
- Ability to tokenize input files with respect to JavaSST language specification
- CLI arguments: --help, --verbose, --output
