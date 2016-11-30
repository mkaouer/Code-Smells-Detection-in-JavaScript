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


/*=================================================================*
 *                                                                 * 
 * This is a MEX-file for MATLAB.                                  *
 *                                                                 * 
 *   [psi] = dai_readfg(filename);                                 *
 *                                                                 * 
 *=================================================================*/


#include <iostream>
#include "mex.h"
#include "matlab.h"
#include "factorgraph.h"


using namespace std;


/* Input Arguments */

#define FILENAME_IN     prhs[0]
#define NR_IN           1


/* Output Arguments */

#define PSI_OUT         plhs[0]
#define NR_OUT          1


void mexFunction( int nlhs, mxArray *plhs[], int nrhs, const mxArray*prhs[] )
{ 
    char *filename;

    
    // Check for proper number of arguments
    if ((nrhs != NR_IN) || (nlhs != NR_OUT)) { 
        mexErrMsgTxt("Usage: [psi] = dai_readfg(filename);\n\n"
        "\n"
        "INPUT:  filename   = filename of a .fg file\n"
        "\n"
        "OUTPUT: psi        = linear cell array containing the factors\n"
        "                     (psi{i} is a structure with a Member field\n"
        "                     and a P field, like a CPTAB).\n");
    } 
    
    // Get input parameters
    size_t buflen;
    buflen = mxGetN( FILENAME_IN ) + 1;
    filename = (char *)mxCalloc( buflen, sizeof(char) );
    mxGetString( FILENAME_IN, filename, buflen );
    

    // Read factorgraph
    FactorGraph fg;
    if( fg.ReadFromFile( filename ) ) {
        mexErrMsgTxt("dai_readfg: error reading file\n");
    }


    // Save factors
    vector<Factor> psi;
    for( size_t I = 0; I < fg.nrFactors(); I++ )
        psi.push_back(fg.factor(I));
    

    // Hand over results to MATLAB
    PSI_OUT = Factors2mx(psi);

    
    return;
}
