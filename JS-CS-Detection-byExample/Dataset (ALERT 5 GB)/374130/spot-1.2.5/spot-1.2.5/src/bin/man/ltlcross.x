[NAME]
ltlcross \- cross-compare LTL/PSL translators to Büchi automata
[DESCRIPTION]
.\" Add any additional description here
[EXAMPLES]
The following commands compare never claims produced by ltl2tgba(1)
and spin(1) and 100 random formulas, using a timeout of 2 minutes.  A
trace of the execution of the two tools, including any potential issue
detected, is reported on standard error, while statistics are
written to \f(CWresults.json\fR.

.nf
% randltl \-n100 \-\-tree\-size=20..30 a b c | \e
ltlcross \-T120 'ltl2tgba \-s %f >%N' 'spin \-f %s >%N' \-\-json=results.json
.fi
.LP

The next command compares lbt, ltl3ba, and ltl2tgba(1) on a set of
formulas saved in file \f(CWinput.ltl\fR.  Statistics are again writen
as CSV into \f(CWresults.csv\fR.  Note the use of \f(CW%L\fR to
indicate that the formula passed to lbt should be written into a file
in LBT's format, and \f(CW%T\fR to read the output in LBTT's format
(which is a superset of the format output by LBT).

.nf
% ltlcross \-F input.ltl \-\-csv=results.csv \e
           'lbt <%L >%T' \e
           'ltl3ba \-f %s >%N' \e
           'ltl2tgba \-\-lbtt %f >%T'
.fi
.LP

Rabin or Streett automata output by ltl2dstar can be read from a
file specified with \f(CW%D\fR.  For instance:

.nf
% ltlcross \-F input.ltl \e
  'ltl2dstar \-\-ltl2nba=spin:path/ltl2tgba@\-s %L %D' \e
  'ltl2dstar \-\-automata=streett \-\-ltl2nba=spin:path/ltl2tgba@\-s %L %D' \e
.fi
.LP

However because Spot only supports Büchi acceptance, these Rabin and
Streett automata are immediately converted to TGBA before further
processing by ltlcross.  This is still interesting to search for bugs
in translators to Rabin or Streett automata, but the statistics might
not be very relevant.

If you use ltlcross in an automated testsuite just to check for
potential problems, avoid the \fB\-\-csv\fR and \fB\-\-json\fR
options: ltlcross is faster when it does not have to compute these
statistics.

[ENVIRONMENT VARIABLES]

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


[OUTPUT DATA]

The following columns are output in the CSV or JSON files.

.TP 7
\fBformula\fR
The formula translated.

.TP
\fBtool\fR
The tool used to translate this formula.  This is either the value of the
full \fICOMMANDFMT\fR string specified on the command-line, or,
if \fICOMMANDFMT\fR has the form \f(CW{\fISHORTNAME\fR\f(CW}\fR\FiCMD\fR,
the value of \fISHORTNAME\fR.

.TP
\fBexit_status\fR, \fBexit_code\fR
Information about how the execution of the translator went.  If the
option \fB\-\-omit\-missing\fR is given, these two columns are omitted
and only the lines corresponding to successful translation are output.
Otherwise, \fBexit_status\fR is a string that can take the following
values:

.RS
.TP
\f(CW"ok"\fR
The translator ran succesfully (this does not imply that the produced
automaton is correct) and ltlcross could parse the resulting
automaton.  In this case \fBexit_code\fR is always 0.

.TP
\f(CW"timeout"\fR
The translator ran for more than the number of seconds
specified with the \fB\-\-timeout\fR option.  In this
case \fBexit_code\fR is always -1.

.TP
\f(CW"exit code"\fR
The translator terminated with a non-zero exit code.
\fBexit_code\fR contains that value.

.TP
\f(CW"signal"\fR
The translator terminated with a signal.
\fBexit_code\fR contains that signal's number.

.TP
\f(CW"parse error"\fR
The translator terminated normally, but ltlcross could not
parse its output.  In this case \fBexit_code\fR is always -1.

.TP
\f(CW"no output"\fR
The translator terminated normally, but without creating the specified
output file.  In this case \fBexit_code\fR is always -1.
.RE

.TP
\fBtime\fR
A floating point number giving the run time of the translator in seconds.
This is reported for all executions, even failling ones.

.PP
Unless the \fB\-\-omit\-missing\fR option is used, data for all the
following columns might be missing.

.TP
\fBin_type\fR, \fBin_states\fR, \fBin_edges\fR, \fBin_transitions\fR, \fBin_acc\fR , \fBin_scc\fR
These columns are only output if \f(CW%D\fR appears in any command
specification, i.e., if any of the tools output some Streett or Rabin
automata.  In this case \fBin_type\fR contains a string that is either
\f(CWDRA\fR (Deterministic Rabin Automaton) or \f(CWDSA\fR
(Deterministic Streett Automaton).  The other columns respectively
give the number of states, edges, transitions, acceptance pairs, and
strongly connected components in that automaton.

.TP
\fBstates\fR, \fBedges\fR, \fBtransitions\fR, \fBacc\fR
The number of states, edges, transitions, and acceptance sets in the
translated automaton.  Column \fBedges\fR counts the number of edges
(labeled by Boolean formulas) in the automaton seen as a graph, while
\fBtransitions\fR counts the number of assignment-labeled transitions
that might have been merged into a formula-labeled edge.  For instance
an edge labeled by \f(CWtrue\fR will be counted as 2^3=8 transitions if
the automaton mention 3 atomic propositions.

If the translator produced a Streett or Rabin automaton, these columns
contains the size of a TGBA (or BA) produced by ltlcross from that
Streett or Rabin automaton.  Check \fBin_states\fR, \fBin_edges\fR,
\fBin_transitions\fR, and \fBin_acc\fR for statistics about the actual
input automaton.

.TP
\fBscc\fR, \fBnonacc_scc\fR, \fBterminal_scc\fR, \fBweak_scc\fR, \fBstrong_scc\fR
The number of strongly connected components in the automaton.  The
\fBscc\fR column gives the total number, while the other columns only
count the SCCs that are non-accepting (a.k.a. transiant), terminal
(recognizes and accepts all words), weak (do not recognize all words,
but accepts all recognized words), or strong (accept some words, but
reject some recognized words).

.TP
\fBnondet_states\fR, \fBnondet_aut\fR
The number of nondeterministic states, and a Boolean indicating whether the
automaton is nondeterministic.

.TP
\fBterminal_aut\fR, \fBweak_aut\fR, \fBstrong_aut\fR
Three Boolean used to indicate whether the automaton is terminal (no
weak nor strong SCCs), weak (some weak SCCs but no strong SCCs), or strong
(some strong SCCs).

.TP
\fBproduct_states\fR, \fBproduct_transitions\fR, \fBproduct_scc\fR
Size of the product between the translated automaton and a randomly
generated state-space.  For a given formula, the same state-space is
of course used the result of each translator.  When the
\fB\-\-products\fR=\fIN\fR option is used, these values are averaged
over the \fIN\fR products performed.

[SEE ALSO]
.BR randltl (1),
.BR genltl (1),
.BR ltlfilt (1),
.BR ltl2tgba (1)

[BIBLIOGRAPHY]
If you would like to give a reference to this tool in an article,
we suggest you cite the following paper:
.TP
\(bu
Alexandre Duret-Lutz: Manipulating LTL formulas using Spot 1.0.
Proceedings of ATVA'13.  LNCS 8172.
.PP
ltlcross is a Spot-based reimplementation of a tool called LBTT.  LBTT
was developped by Heikki Tauriainen at the Helsinki University of
Technology.  The main motivation for the reimplementation was to
support PSL, and output more statistics about the translations.

The sanity checks performed on the result of each translator (by
either LBTT or ltlcross) are described in the following paper:
.TP
\(bu
H. Tauriainen and K. Heljanko: Testing LTL formula translation into
Büchi automata.  Int. J. on Software Tools for Technology Transfer.
Volume 4, number 1, October 2002.
.PP
LBTT did not implement Test 2 described in this paper.  ltlcross
implements a slight variation: when an automaton produced by some
translator is deterministic, its complement is built and used for
additional cross-comparisons with other tools.  If the translation P1
of the positive formula and the translation N1 of the negative formula
both yield deterministic automata (this may only happen for obligation
properties) then the emptiness check of Comp(P1)*Comp(N1) is
equivalent to Test 2 of Tauriainen and Heljanko.  If only one
automaton is deterministic, say P1, it can still be used to check we
can be used to check the result of another translators, for instance
checking the emptiness of Comp(P1)*P2.

Our implementation will detect and reports problems (like
inconsistencies between two translations) but unlike LBTT it does not
offer an interactive mode to investigate such problems.


