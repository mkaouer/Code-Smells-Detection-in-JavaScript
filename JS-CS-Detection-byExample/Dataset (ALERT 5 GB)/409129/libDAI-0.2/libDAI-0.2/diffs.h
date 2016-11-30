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


#ifndef __DIFFS_H__
#define __DIFFS_H__


#include <vector>

using namespace std;


class Diffs : public vector<double> {
    private:
        size_t _maxsize;
        double _def;
        vector<double>::iterator _pos;
        vector<double>::iterator _maxpos;
    public:
        Diffs(long maxsize, double def) : vector<double>(), _maxsize(maxsize), _def(def) { 
            this->reserve(_maxsize); 
            _pos = begin(); 
            _maxpos = begin(); 
        };
        double max() { 
            if( size() < _maxsize )
                return _def;
            else
                return( *_maxpos ); 
        }
        void push(double x) {
            if( size() < _maxsize ) {
                push_back(x);
                _pos = end();
                _maxpos = max_element(begin(),end());
            }
            else {
                if( _pos == end() )
                    _pos = begin();
                if( _maxpos == _pos ) {
                    *_pos++ = x; 
                    _maxpos = max_element(begin(),end());
                } else {
                    if( x > *_maxpos )
                        _maxpos = _pos;
                    *_pos++ = x;
                }
            }
        }
};

#endif
