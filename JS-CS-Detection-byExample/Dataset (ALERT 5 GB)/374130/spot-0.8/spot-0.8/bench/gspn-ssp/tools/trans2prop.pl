#!/usr/bin/perl -w

use strict 'vars';

die "Please specify model name as first argument to script.\n"
    unless 1 != $#ARGV;

my $modelname = $ARGV[0];
#print "Will work on model \"$modelname\"\n";
my $netf = "$modelname.net";
my $deff = "$modelname.def";
my $propf = "$modelname.tobs";
my $tmpf = "$modelname.nettmp";
open (NET, "< $netf") or die "cannot open $netf\n";
open (DEF, "< $deff") or die "cannot open $deff\n";
open (PROP, "> $propf") or die "cannot open $propf\n";
open (OUTNET,"> $tmpf") or die "cannot open $tmpf\n";


my %defs = ();
{
  local $/;
  my $contents = <DEF>;
  close DEF;
  while ($contents =~ /\((F\d+)\s[^\(]*\(\@f\n(.*)\n\)\)/gom)
    {
      $defs{$1} = $2;
    }
}

my $line = <NET>;
print OUTNET $line;
$line = <NET>;
print OUTNET $line;

$line = <NET>;
my @split = split (/\s+/, $line);

my $nump = $split[2];
my $numt = $split[4];

#print "$nump places, $numt transitions\n";

my $places ;
for (my $i = 0; $i < $nump; $i++)
  {
    $places .= <NET>;
  }
my %trans = ();
my $transline = <NET>;
for (my $i = 0; $i < $numt; $i++)
  {
    my $tname = (split (/ /,$transline))[0];
    $trans{$tname} .= $transline;
    $trans{$tname} .= $line while (defined ($line = <NET>) && $line =~ /^\s/);
#    print "analyzed transition $tname:\n$trans{$tname}\n";
    $transline = $line;
  }

my $numpt = grep /^PROP\_/, keys %trans;
$split[4] -= $numpt ;

print OUTNET (join (" ",@split));
print OUTNET "\n";
print OUTNET $places;
print PROP "$numpt\n";
my $props = '';
foreach my $key (keys %trans)
  {
    if ($key =~ /^PROP_/)
      {
        $props .= $trans{$key};
      }
    else
      {
        print OUTNET $trans{$key}
      }
  }

$props =~ s/^PROP_//gm;
while (my ($key, $val) = each %defs)
  {
    $props =~ s/$key\s*$/$val/gm;
  }

close OUTNET;
print PROP $props;
close PROP;
close NET;
