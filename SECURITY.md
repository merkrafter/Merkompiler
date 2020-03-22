# Security Policy

Since this is only a study project, there are no really secured versions.
Therefore, you should not run this program on critical systems and never with root privileges.
I am, however, deeply interested in security topics and if someone finds a
way to exploit this program, I am thrilled to hear about it. In this case I
will fix it.

## Reporting a Vulnerability

In order to report a vulnerability, just open an issue for that.
You can do that even if you just have a *feeling* that something could
be exploitable. We will work on that. If it turns out to be a vulnerability, I will fix it and if not,
it could be added to the list below to keep track of it in case it becomes
a vulnerability in future versions by accident.

## Possible vectors

This is an incomplete list of things that might be dangerous.
These are not thought through to the end, but rather just ideas.
New items will be added to the list occasionally.

* The input file's name is echoed to the user by several parts of the program
 (e.g. if the file was not found, by the scanner and by the parser in the event of errors).
  This is not an immediate problem, but could become one if Merkompiler's output is processed
  by another application as a web browser or shell.