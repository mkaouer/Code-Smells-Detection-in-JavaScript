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


#include "mex.h"
#include "../factor.h"


/* Convert vector<Factor> structure to a cell vector of CPTAB-like structs */
mxArray *Factors2mx(const std::vector<Factor> &Ps);

/* Convert cell vector of CPTAB-like structs to vector<Factor> */
std::vector<Factor> mx2Factors(const mxArray *psi, long verbose);

/* Convert CPTAB-like struct to Factor */
Factor mx2Factor(const mxArray *psi);
