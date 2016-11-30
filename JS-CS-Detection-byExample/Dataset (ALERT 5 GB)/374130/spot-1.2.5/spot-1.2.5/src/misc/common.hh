// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#ifndef SPOT_MISC_COMMON_HH
#  define SPOT_MISC_COMMON_HH

#ifdef __GNUC__
#define SPOT_DEPRECATED __attribute__ ((deprecated))
#elif defined(_MSC_VER)
#define SPOT_DEPRECATED __declspec(deprecated)
#else
#define SPOT_DEPRECATED func
#endif

#if defined _WIN32 || defined __CYGWIN__
  #define SPOT_HELPER_DLL_IMPORT __declspec(dllimport)
  #define SPOT_HELPER_DLL_EXPORT __declspec(dllexport)
  #define SPOT_HELPER_DLL_LOCAL
#else
  #if __GNUC__ >= 4
    #define SPOT_HELPER_DLL_IMPORT __attribute__ ((visibility ("default")))
    #define SPOT_HELPER_DLL_EXPORT __attribute__ ((visibility ("default")))
    #define SPOT_HELPER_DLL_LOCAL  __attribute__ ((visibility ("hidden")))
  #else
    #define SPOT_HELPER_DLL_IMPORT
    #define SPOT_HELPER_DLL_EXPORT
    #define SPOT_HELPER_DLL_LOCAL
  #endif
#endif

#ifdef SPOT_BUILD
  #define SPOT_DLL
#endif

// SPOT_API is used for the public API symbols. It either DLL imports
// or DLL exports (or does nothing for static build) SPOT_LOCAL is
// used for non-api symbols that may occur in header files.
#ifdef SPOT_DLL
  #ifdef SPOT_BUILD
    #define SPOT_API SPOT_HELPER_DLL_EXPORT
  #else
    #define SPOT_API SPOT_HELPER_DLL_IMPORT
  #endif
  #define SPOT_LOCAL SPOT_HELPER_DLL_LOCAL
#else
  #define SPOT_API
  #define SPOT_LOCAL
#endif
#define SPOT_API_VAR extern SPOT_API

#endif // SPOT_MISC_COMMON_HH
