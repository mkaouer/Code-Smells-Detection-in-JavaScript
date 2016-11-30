#!/bin/sh

ltlfilt=../../src/bin/ltlfilt
ltl2tgba=../../src/bin/ltl2tgba
dstar2tgba=../../src/bin/dstar2tgba
timeout='timeout -sKILL 1h'
stats=--stats="%s, %e, %t, %a, %c, %d, %p, %r"
empty='-, -, -, -, -, -, -, -'

rm -f stats.mk stats.tmp

n=1
all=

while IFS=, read f type accmax accmin; do
  unset IFS

  case $type in
  *TCONG*)
    echo "$n.log:; ./stat.sh $n '$f' $type $accmax >\$@" >> stats.tmp
    all="$all $n.log"
    n=`expr $n + 1`
    echo "$n.log:; ./stat.sh $n '$f' DRA-CONG $accmax >\$@" >> stats.tmp
    all="$all $n.log"
    n=`expr $n + 1`
    ;;
  *)
    echo "$n.log:; ./stat.sh $n '$f' $type $accmax >\$@" >> stats.tmp
    all="$all $n.log"
    n=`expr $n + 1`
    ;;
  esac
done < info.ltl

cat > stats.mk <<EOF
ALL = $all
all.log: \$(ALL)
	cat \$(ALL) >\$@
EOF
cat stats.tmp >> stats.mk

echo "Now, run something like: make -j8 -f stats.mk"
