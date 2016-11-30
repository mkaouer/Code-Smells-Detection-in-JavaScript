/*  Copyright 2006  Joris Mooij
    jorism@jorismooij.nl
    
    This file is part of AI.

    AI is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    AI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AI; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/


#include <iostream>
#include "alldai.h"


using namespace std;


int main( int argc, char *argv[] ) {
    if ( argc != 2 ) {
        cout << "Usage: " << argv[0] << " <filename.fg>" << endl << endl;
        cout << "Reads factor graph <filename.fg> and runs" << endl;
        cout << "Belief Propagation and JunctionTree on it." << endl << endl;
        return 1;
    } else {
        FactorGraph fg;

        if( fg.ReadFromFile(argv[1]) ) {
            cout << "Error reading " << argv[1] << endl;
            return 2;
        } else {
            size_t  maxiter = 1000;
            double  tol = 1e-9;
            size_t  verb = 1;

            Properties opts;
            opts.Set("maxiter",maxiter);
            opts.Set("tol",tol);
            opts.Set("verbose",verb);

            JTree jt( fg, opts("updates",string("HUGIN")) );
            jt.init();
            jt.run();

            cout << "Exact single node marginals:" << endl;
            for( size_t i = 0; i < fg.nrVars(); i++ )
                cout << jt.belief(fg.var(i)) << endl;

            BP bp(fg, opts("updates",string("SEQMAX")));
            bp.init();
            bp.run();

            cout << "Exact single node marginals:" << endl;
            for( size_t i = 0; i < fg.nrVars(); i++ )
                cout << bp.belief(fg.var(i)) << endl;
        }
    }

    return 0;
}
