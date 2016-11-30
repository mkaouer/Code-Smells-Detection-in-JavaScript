#!/usr/bin/env perl

# Copyright (C) 2011 Laboratoire de Recherche et D�veloppement de
# l'Epita.
# Copyright (C) 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
# d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
# et Marie Curie.
#
# This file is part of Spot, a model checking library.
#
# Spot is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# Spot is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
# License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Spot; see the file COPYING.  If not, write to the Free
# Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
# 02111-1307, USA.

use warnings;

my $line = 0;
my $tool = 0;
my ($a, $b, $acc, $time);

sub sep($)
{
    $n = shift;
    $n =~ s/(\d{1,3}?)(?=(\d{3})+$)/$1\\,/g;
    return $n;
}

format STDOUT3 =
@<<<<<<<<<<<<<<<<<<<<< & @>>>>>> & @>>>>>> & @>>>>>>>>> & @>>>>>>>>>>> \\ % @>>
$tool, sep($a), sep($b), sep($2), sep($3), sep($1)
.

format STDOUT2 =
||<:>@>>||@<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<||<)>@>>>>>||<)>@>>>>>||<)>@>>>||<)>@#####.##||<)>@>>>>>>>>||<)>@>>>>>>>>||<)>@>>||
$num, $tool, $a, $b, $acc, $time, $2, $3, $1
.

format STDOUT =
@>>: @<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
$num, $tool
       @>>>>> / @>>>>> / @>>> / @#####.##   @>>>>>>>> / @>>>>>>>>   (@>>)
$a, $b, $acc, $time, $2, $3, $1
.

$~ = STDOUT2 if (exists $ENV{'WIKIOUTPUT'});
$~ = STDOUT3 if (exists $ENV{'LATEXOUTPUT'});

my %impl;

while (<>)
{
    if (/^\s{4}(\d+):\s`(.+)'\s*(?:\(disabled\))?\s*$/)
    {
	$impl{$1} = $2 unless exists $impl{$1};
    }
    if (/All formulae\s*\|\s*([^|]*?)\s*\|\s*([^|]*?)\s*\|$/)
    {
        $acc = $1;
	$time = $2 || 0;
    }
    next unless /All formulae\s+\|\s*(.*?)\s*\|\s*(.*?)\s*\|\s*(.*?)\s*\|/;
    if ($line % 2)
    {
	$num = $line >> 1;
        $tool = $impl{$num};
	write;
    }
    else
    {
	($a, $b) = ($2, $3);
    }
    ++$line;
}
