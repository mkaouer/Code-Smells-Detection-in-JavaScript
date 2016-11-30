#! /usr/bin/perl -w

# Copyright (C) 2012 Laboratoire de Recherche et Développement de
# l'Epita.
#
# This file is part of Spot, a model checking library.
#
# Spot is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 3 of the License, or
# (at your option) any later version.
#
# Spot is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
# License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.


# This script reads the output of the files "*.log" given in arguments
# and output it in a csv format on stdout.
# If you give the option `-p' as the first argument of this program,
# you'll get only the positive formulae, while `-n' gives the negative
# ones. If you give several of them, you'll get only the last you asked for.
#
# As an example: "./lbtt2csv.pl -n -n -p -n -p foo.log" will output only
# the positive formulae.


use strict;

our $no_positive = undef;
our $no_negative = undef;

# Get a block and return the number of states, transition of the
# automaton and of the product, and the time too.
sub work_on_formula_in_algo($)
{
  my ($content) = @_;

  my ($states) = ($$content =~ /number of states:\s+(\d+?)\n/ms);
  my ($tr) = ($$content =~ /number of transitions:\s+(\d+)\n/ms);
  my ($acc_set) = ($$content =~ /acceptance sets:\s+(\d+)\n/ms);
  my ($det) = ($$content =~ /is deterministic:\s+(\w+)\n/ms);
  my ($nb_det) = ($$content =~ /nondeterminism index:\s+(\d+)\n/ms);
  my ($time) = ($$content =~ /time:\s+(\d+\.\d+)/ms);
  $$content = $'; #'

  my ($product_st) = ($$content =~ /number of states:\s+(\d+)/ms);
  my ($product_tr) = ($$content =~ /number of transitions:\s+(\d+)\n/ms);

  $$content =~ /Negated formula:\n/ms;
  $$content = $'; #'

  return ($states, $tr, $acc_set, $det,
          $nb_det, $time, $product_st, $product_tr);
}


# Take a string which starts with the line " XX: `Algo Name'" and
# contains all algorithm block. Then it prints all the information
# wanted in its block, for the positive and negated formula.
sub work_on_algorithm($$)
{
  my ($content, $formula) = @_;

  my ($nb) = ($content =~ /(\d+?):[^\n]+/ms);
  $content = $'; #'

  # Get the number of state and transition of the automaton and the
  # product and the computation time.
  my ($states, $tr, $acc_set, $det, $nb_det, $time, $product_st, $product_tr)
      = work_on_formula_in_algo(\$content);

  print "\"$formula\",$nb,$states,$tr,$acc_set,$det,$nb_det,"
      . "$time,$product_st,$product_tr\n" unless defined $no_positive;

  # Now let's repeat with the negated formula.
  ($states, $tr, $acc_set, $det, $nb_det, $time, $product_st, $product_tr)
      = work_on_formula_in_algo(\$content);

  print "\"! $formula\",$nb,$states,$tr,$acc_set,$det,$nb_det,"
      . "$time,$product_st,$product_tr\n" unless defined $no_negative;
}

# Take a string which is a concatenation of the lines of a round.
# In fact, we received from "LTL formula..." to "...Model".
# So we have to clean a little before working.
sub work_on_whole_round($)
{
  my ($round) = @_;

  $round =~ s/      parse tree.*?\n//ms;

  $round =~ /.*?\n\s+formula:\s+(.*?)\n/ms;
  my $formula = $1;
  $round = $'; #'



  $round =~ s{\n\n(.*?)(?=\n\n)}<
  work_on_algorithm ($1, $formula);
  >gse;
}

while ($ARGV[0] eq '-n' or $ARGV[0] eq '-p')
{
  $no_negative = undef;
  $no_positive = undef;
  $no_negative = "true" if $ARGV[0] eq '-p';
  $no_positive = "true" if $ARGV[0] eq '-n';
  shift;
}

my $round_re = qr/LTL formula:.*?Model/sm;

local $/ = undef;
while (my $content = <>)
{
  # The content starting with "Round 1" and ending before the
  # "Statistics after round ...".
  $content =~ /Round/m;
  $content = "Round" . $'; #'
      $content =~ /Statistics after round /m;
  $content = $`;

  $content =~ s<$round_re><
      work_on_whole_round($&);
  >gse;
}
