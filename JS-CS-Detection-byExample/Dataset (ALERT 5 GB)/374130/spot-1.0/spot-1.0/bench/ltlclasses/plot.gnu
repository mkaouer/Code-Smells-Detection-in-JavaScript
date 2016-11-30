set terminal postscript eps enhanced color
set ytics nomirror
set y2tics auto
set ylabel "states"
set y2label "ticks"
set key left top

set output 'results1.fm.eps'

plot 'results1.fm' using 1:($4+$5) '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Total Time" axes x1y2, \
     'results1.fm' using 1:4 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Translation Time" axes x1y2, \
     'results1.fm' using 1:2 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with lines title "States"

set output 'results2.fm.eps'

plot 'results2.fm' using 1:($4+$5) '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Total Time" axes x1y2, \
     'results2.fm' using 1:4 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Translation Time" axes x1y2, \
     'results2.fm' using 1:2 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with lines title "States"

set output 'results3.fm.eps'

plot 'results3.fm' using 1:($4+$5) '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Total Time" axes x1y2, \
     'results3.fm' using 1:4 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Translation Time" axes x1y2, \
     'results3.fm' using 1:2 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with lines title "States"

set output 'results4.fm.eps'

plot 'results4.fm' using 1:($4+$5) '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Total Time" axes x1y2, \
     'results4.fm' using 1:4 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Translation Time" axes x1y2, \
     'results4.fm' using 1:2 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with lines title "States"

set output 'results5.fm.eps'

plot 'results5.fm' using 1:($4+$5) '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Total Time" axes x1y2, \
     'results5.fm' using 1:4 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with filledcurve x1 title "Translation Time" axes x1y2, \
     'results5.fm' using 1:2 '%lf,%lf,%lf,%lf,%*lf,%*lf,%lf,%*lf,%*lf' \
        with lines title "States"

