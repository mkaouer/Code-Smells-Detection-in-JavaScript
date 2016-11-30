[NAME]
spot-x \- Common fine-tuning options.

[SYNOPSIS]
.B \-\-extra-options STRING
.br
.B \-x STRING

[DESCRIPTION]
.\" Add any additional description here

[ENVIRONMENT VARIABLES]

.TP
\fBSPOT_SATLOG\fR
If set to a filename, the SAT-based minimization routines will append
statistics about each iteration to the named file.  Each line lists
the following comma-separated values: requested number of states,
number of reachable states in the output, number of edges in the
output, number of transitions in the output, number of variables in
the SAT problem, number of clauses in the SAT problem, user time for
encoding the SAT problem, system time for encoding the SAT problem,
user time for solving the SAT problem, system time for solving the SAT
problem.

.TP
\fBSPOT_SATSOLVER\fR If set, this variable should indicate how to call
a SAT\-solver.  This is used by the sat\-minimize option described
above.  The default value is \f(CW"glucose -verb=0 -model %I >%O"\fR,
it is correct for glucose version 3.0 (for older versions, remove the
\fCW(-model\fR option).  The escape sequences \f(CW%I\fR and
\f(CW%O\fR respectively denote the names of the input and output
files.  These temporary files are created in the directory specified
by \fBSPOT_TMPDIR\fR or \fBTMPDIR\fR (see below).  The SAT-solver
should follow the convention of the SAT Competition for its input and
output format.

.TP
\fBSPOT_TMPDIR\fR, \fBTMPDIR\fR
These variables control in which directory temporary files (e.g.,
those who contain the input and output when interfacing with
translators) are created.  \fBTMPDIR\fR is only read if
\fBSPOT_TMPDIR\fR does not exist.  If none of these environment
variables exist, or if their value is empty, files are created in the
current directory.

.TP
\fBSPOT_TMPKEEP\fR
When this variable is defined, temporary files are not removed.
This is mostly useful for debugging.

[BIBLIOGRAPHY]
.TP
1.
Christian Dax, Jochen Eisinger, Felix Klaedtke: Mechanizing the
Powerset Construction for Restricted Classes of
ω-Automata. Proceedings of ATVA'07.  LNCS 4762.

Describes the WDBA-minimization algorithm implemented in Spot.  The
algorithm used for the tba-det options is also a generalization (to
TBA instead of BA) of what they describe in sections 3.2 and 3.3.

.TP
2.
Tomáš Babiak, Thomas Badie, Alexandre Duret-Lutz, Mojmír Křetínský,
Jan Strejček: Compositional Approach to Suspension and Other
Improvements to LTL Translation.  Proceedings of SPIN'13.  LNCS 7976.

Describes the compositional suspension, the simulation-based
reductions, and the SCC-based simplifications.

.TP
3.
Rüdiger Ehlers: Minimising Deterministic Büchi Automata Precisely using
SAT Solving.  Proceedings of SAT'10.  LNCS 6175.

Our SAT-based minimization procedures are generalizations of this
paper to deal with TBA or TGBA.

[SEE ALSO]
.BR ltl2tgba (1)
.BR ltl2tgta (1)
.BR dstar2tgba (1)
