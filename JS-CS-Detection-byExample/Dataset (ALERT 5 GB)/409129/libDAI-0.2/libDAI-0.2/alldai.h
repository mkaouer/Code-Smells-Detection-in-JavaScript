/*  Copyright (C) 2006  Joris Mooij  [j dot mooij at science dot ru dot nl]
    Radboud University Nijmegen, The Netherlands
    
    This file is part of libDAI.

    libDAI is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    libDAI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with libDAI; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/


#ifndef __ALLDAI_H__
#define __ALLDAI_H__


#include "daialg.h"
#include "bp.h"
#include "lc.h"
#include "hak.h"
#include "mf.h"
#include "jtree.h"
#include "treeep.h"
#include "mr.h"


/// newInfAlg constructs a new approximate inference algorithm named name for the
/// FactorGraph fg with optionts opts and returns a pointer to the new object.
/// The caller needs to delete it (maybe some sort of smart_ptr might be useful here).
InfAlg *newInfAlg( const string &name, const FactorGraph &fg, const Properties &opts );


/// AINames contains the names of all approximate inference algorithms
static const char* DAINames[] = {BP::Name, MF::Name, HAK::Name, LC::Name, TreeEP::Name, MR::Name, JTree::Name};


#endif
