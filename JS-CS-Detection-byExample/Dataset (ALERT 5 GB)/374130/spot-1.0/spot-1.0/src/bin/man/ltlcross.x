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

The next command compare lbt, ltl3ba, and ltl2tgba(1) on a set of
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

If you use ltlcross in an automated testsuite just to check for
potential problems, avoid the \fB\-\-csv\fR and \fB\-\-json\fR
options: ltlcross is faster when it does not have to compute these
statistics.

[SEE ALSO]
.BR randltl (1),
.BR genltl (1),
.BR ltlfilt (1),
.BR ltl2tgba (1)

[BIBLIOGRAPHY]
ltlcross is a Spot-based reimplementation of a tool called LBTT.  LBTT
was developped by Heikki Tauriainen at the Helsinki University of
Technology.  The main motivation for the reimplementation was to
support PSL, and output more statistics about the translations.

The sanity checks performed on the result of each translator (by
either LBTT or ltlcross) are described in the following paper.  Our
implementation will detect and reports problems (like inconsistencies
between two translations) but unlike LBTT it does not offer an
interactive mode to investigate such problems.

.TP
th02
H. Tauriainen and K. Heljanko: Testing LTL formula translation into
Büchi automata.  Int. J. on Software Tools for Technology Transfer.
Volume 4, number 1, October 2002.
