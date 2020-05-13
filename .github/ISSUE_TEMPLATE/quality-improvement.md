---
name: Quality improvement
about: Improve code quality with regard to e.g. efficiency or maintainability
title: ''
labels: refactoring
assignees: ''

---

**What property can be improved?**
E.g. efficiency, maintainability

**How can that property be improved?**
By using an AstNodeFactory to create nodes instead of the nodes' constructors in the Parser class it is possible to instantiate different specializations of nodes, e.g. paintable ones.

**Additional information**
Possibly add some time measurements, relevant links, ...
