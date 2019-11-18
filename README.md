# Merkompiler
`Merkompiler` is a JavaSST compiler written in Java as a study project.
It is not meant to be used in production, but rather as a template and inspiration for others
that need to solve similar tasks.

## Getting started
These instructions will help you compiling and running Merkompiler on your system.

### JavaSST description
JavaSST is a (pretty) small subset of Java's language features.
A complete description for it can be found in this project's wiki pages.

### Prerequisites
This program is developed under and tested with 
 - `Apache maven 3.6.0` (you'll only need that if you plan to test and build Merkompiler yourself)
 - `Java 1.8`

### Building
In order to compile this project yourself, you simply need to run the following commands.
If you just want to use it, skip to installing.
```bash
$ git clone https://github.com/merkrafter/Merkompiler.git
$ mvn package
```

### Installing
Merkompiler comes as a standalone jar, hence you don't need to do anything other than
building or downloading it from the github repository's releases tab.

## Running the tests
Assuming you downloaded the sources via git clone or equivalent, you only have to run:
```bash
$ mvn test
```

## Contributing
As this is a study project, direct contributing is not allowed.
You are still invited to open an issue if you find a bug or something similar.

## Versioning
This project uses [Semantic Versioning](https://semver.org/).

## License
This project is released under the [MIT license](LICENSE.md).
