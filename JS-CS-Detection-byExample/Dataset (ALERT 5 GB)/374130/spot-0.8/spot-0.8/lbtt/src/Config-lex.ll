/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003, 2004, 2005
 *  Heikki Tauriainen <Heikki.Tauriainen@tkk.fi>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

%{
#include <config.h>
#include "Configuration.h"
#include "Config-parse.h"

extern int config_file_line_number;

%}

%option case-insensitive
%option never-interactive
%option noyywrap
%option nounput

%x ATTR EQ VAL

SQSTR [^\'\n]*
DQSTR ([^\"\\\n]|\\.)*
UQC   [^\'\"\\ \t\n]
UQSTR ({UQC}+|\\.)({UQC}*|\\.)*
OKVAL \'{SQSTR}\'|\"{DQSTR}\"|{UQSTR}

%%

<*>[ \t]*                   { /* Skip whitespace everywhere. */ }
<*>"#".*$                   { /* Skip comments everywhere. */ }

<INITIAL,ATTR>"\n"          { /* Skip newlines, but update the line number. */
                               config_file_line_number++;
                            }

"{"                         { BEGIN(ATTR); return CFG_LBRACE; }

algorithm|implementation|translator { return CFG_ALGORITHM; }
globaloptions               { return CFG_GLOBALOPTIONS; }
statespaceoptions           { return CFG_STATESPACEOPTIONS; }
formulaoptions              { return CFG_FORMULAOPTIONS; }

[^ \t\n]+                   { return CFG_UNKNOWN; }

<ATTR>enabled               { BEGIN(EQ); return CFG_ENABLED; }
<ATTR>name                  { BEGIN(EQ); return CFG_NAME; }
<ATTR>parameters            { BEGIN(EQ); return CFG_PARAMETERS; }
<ATTR>path                  { BEGIN(EQ); return CFG_PROGRAMPATH; }

<ATTR>comparisoncheck       { BEGIN(EQ); return CFG_COMPARISONTEST; }
<ATTR>comparisontest        { BEGIN(EQ); return CFG_COMPARISONTEST; }
<ATTR>consistencycheck      { BEGIN(EQ); return CFG_CONSISTENCYTEST; }
<ATTR>consistencytest       { BEGIN(EQ); return CFG_CONSISTENCYTEST; }
<ATTR>interactive           { BEGIN(EQ); return CFG_INTERACTIVE; }
<ATTR>intersectioncheck     { BEGIN(EQ); return CFG_INTERSECTIONTEST; }
<ATTR>intersectiontest      { BEGIN(EQ); return CFG_INTERSECTIONTEST; }
<ATTR>modelcheck            { BEGIN(EQ); return CFG_MODELCHECK; }
<ATTR>rounds                { BEGIN(EQ); return CFG_ROUNDS; }
<ATTR>translatortimeout     { BEGIN(EQ); return CFG_TRANSLATORTIMEOUT; }
<ATTR>verbosity             { BEGIN(EQ); return CFG_VERBOSITY; }

<ATTR>edgeprobability       { BEGIN(EQ); return CFG_EDGEPROBABILITY; }
<ATTR>propositions          { BEGIN(EQ); return CFG_PROPOSITIONS; }
<ATTR>size                  { BEGIN(EQ); return CFG_SIZE; }
<ATTR>truthprobability      { BEGIN(EQ); return CFG_TRUTHPROBABILITY; }
<ATTR>changeinterval        { BEGIN(EQ); return CFG_CHANGEINTERVAL; }
<ATTR>randomseed            { BEGIN(EQ); return CFG_RANDOMSEED; }

<ATTR>abbreviatedoperators  { BEGIN(EQ); return CFG_ABBREVIATEDOPERATORS; }
<ATTR>andpriority           { BEGIN(EQ); return CFG_ANDPRIORITY; }
<ATTR>beforepriority        { BEGIN(EQ); return CFG_BEFOREPRIORITY; }
<ATTR>defaultoperatorpriority {
                              BEGIN(EQ); return CFG_DEFAULTOPERATORPRIORITY;
                            }
<ATTR>equivalencepriority   { BEGIN(EQ); return CFG_EQUIVALENCEPRIORITY; }
<ATTR>falsepriority         { BEGIN(EQ); return CFG_FALSEPRIORITY; }
<ATTR>finallypriority       { BEGIN(EQ); return CFG_FINALLYPRIORITY; }
<ATTR>generatemode          { BEGIN(EQ); return CFG_GENERATEMODE; }
<ATTR>globallypriority      { BEGIN(EQ); return CFG_GLOBALLYPRIORITY; }
<ATTR>implicationpriority   { BEGIN(EQ); return CFG_IMPLICATIONPRIORITY; }
<ATTR>nextpriority          { BEGIN(EQ); return CFG_NEXTPRIORITY; }
<ATTR>notpriority           { BEGIN(EQ); return CFG_NOTPRIORITY; }
<ATTR>orpriority            { BEGIN(EQ); return CFG_ORPRIORITY; }
<ATTR>outputmode            { BEGIN(EQ); return CFG_OUTPUTMODE; }
<ATTR>propositionpriority   { BEGIN(EQ); return CFG_PROPOSITIONPRIORITY; }
<ATTR>releasepriority       { BEGIN(EQ); return CFG_RELEASEPRIORITY; }
<ATTR>strongreleasepriority { BEGIN(EQ); return CFG_STRONGRELEASEPRIORITY; }
<ATTR>truepriority          { BEGIN(EQ); return CFG_TRUEPRIORITY; }
<ATTR>untilpriority         { BEGIN(EQ); return CFG_UNTILPRIORITY; }
<ATTR>weakuntilpriority     { BEGIN(EQ); return CFG_WEAKUNTILPRIORITY; }
<ATTR>xorpriority           { BEGIN(EQ); return CFG_XORPRIORITY; }

<ATTR>"}"                   { BEGIN(INITIAL); return CFG_RBRACE; }

<ATTR>"="?[^= \t\n]*        { return CFG_UNKNOWN; }

<EQ>"="                     { BEGIN(VAL); return CFG_EQUALS; }

<EQ>.                       { return CFG_UNKNOWN; }

<VAL>\\|{OKVAL}+(\\)?       {
                              yylval.value = yytext;
                              BEGIN(ATTR);
                              return CFG_VALUE;
                            }

<VAL>{OKVAL}*(\'{SQSTR}|\"{DQSTR})(\\)? {
                              throw Configuration::ConfigurationException
                                      (config_file_line_number,
                                       "unmatched quotes");
                            }

<EQ,VAL>"\n"                { return CFG_UNKNOWN; }

%%
