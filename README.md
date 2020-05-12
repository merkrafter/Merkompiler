# Merkompiler
`Merkompiler` is a JavaSST compiler written in Java (and pieces of Kotlin) as a study project.
It is not meant to be used in production, but rather as a template and inspiration for others
that need to solve similar tasks.
I also don't claim that this is the best or even a good implementation of a compiler.

Please note that Merkompiler is not able to output `.class` files.
At this point, it can tokenize input files, parse them into an abstract syntax tree, and make it visible.

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

### Running
Merkompiler can be run with:
```bash
java -jar path/to/Merkompiler.jar
```

An overview over all available options of your version can be obtained with:
```bash
java -jar path/to/Merkompiler.jar --help
```

The `--graphical` argument outputs a `.dot` file that can be translated to a `.png` file via the command
```bash
dot -Tpng path/to/file.dot > output.png
```

## Running the tests
Assuming you downloaded the sources via git clone or equivalent, you only have to run:
```bash
$ mvn test
```

## Built with
- Technologies
  - [Apache Maven](https://maven.apache.org/) by Apache Software Foundation
  - [Kotlin](https://github.com/JetBrains/kotlin) by JetBrains s.r.o and respective authors and developers
- Libraries
  - [openJDK](https://openjdk.java.net/projects/jdk8/)
  - [argparse4j](https://github.com/argparse4j/argparse4j) by Tatsuhiro Tsujikawa under the [MIT license](https://github.com/argparse4j/argparse4j/blob/master/LICENSE.txt)

## Tests built with
- [junit5](https://github.com/junit-team/junit5)
- [mockito](https://github.com/mockito/mockito) by Mockito contributors under the [MIT license](https://github.com/mockito/mockito/blob/release/3.x/LICENSE)

## Contributing
See [Contributing](CONTRIBUTING.md) file.

## Versioning
This project uses [Semantic Versioning](https://semver.org/).

## License
This project is released under the [MIT license](LICENSE.md).

## Acknowledgements
This README was created from this [template](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2).
