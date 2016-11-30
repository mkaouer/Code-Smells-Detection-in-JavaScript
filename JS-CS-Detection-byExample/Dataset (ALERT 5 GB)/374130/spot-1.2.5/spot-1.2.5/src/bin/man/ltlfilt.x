[NAME]
ltlfilt \- filter files or lists of LTL/PSL formulas
[DESCRIPTION]
.\" Add any additional description here
[BIBLIOGRAPHY]
If you would like to give a reference to this tool in an article,
we suggest you cite the following paper:
.TP
\(bu
Alexandre Duret-Lutz: Manipulating LTL formulas using Spot 1.0.
Proceedings of ATVA'13.  LNCS 8172.
.PP
The following papers describes algorithms used by ltlfilt:
.TP
\(bu
Kousha Etessami: A note on a question of Peled and Wilke regarding
stutter-invariant LTL. Information Processing Letters 75(6): 261-263
(2000).

Describes the transformation behind the \fB\-\-remove\-x\fR option.
.TP
\(bu
Christian Dax, Jochen Eisinger, Felix Klaedtke: Mechanizing the
Powerset Construction for Restricted Classes of
ω-Automata. Proceedings of ATVA'07.  LNCS 4762.

Describes the checks implemented by the \fB\-\-safety\fR,
\fB\-\-guarantee\fR, and \fB\-\-obligation\fR options.
.TP
\(bu
Ivana Černá, Radek Pelánek: Relating Hierarchy of Temporal Properties
to Model Checking.  Proceedings of MFCS'03.  LNCS 2747.

Describes the syntactic LTL classes matched by the
\fB\-\-syntactic\-safety\fR, \fB\-\-syntactic\-guarantee\fR,
\fB\-\-syntactic\-obligation\fR options,
\fB\-\-syntactic\-persistence\fR, and \fB\-\-syntactic\-recurrence\fR
options.
.TP
\(bu
Kousha Etessami, Gerard J. Holzmann: Optimizing Büchi
Automata. Proceedings of CONCUR'00.  LNCS 1877.

Describe the syntactic LTL classes matched by \fB\-\-eventual\fR, and
\fB\-\-universal\fR.
[SEE ALSO]
.BR randltl (1)
