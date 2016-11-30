/*  This file is part of libDAI - http://www.libdai.org/
 *
 *  libDAI is licensed under the terms of the GNU General Public License version
 *  2, or (at your option) any later version. libDAI is distributed without any
 *  warranty. See the file COPYING for more details.
 *
 *  Copyright (C) 2010  Joris Mooij  [joris dot mooij at libdai dot org]
 */


/// \file
/// \brief Defines class DecMAP, which constructs a MAP state by decimation


#ifndef __defined_libdai_decmap_h
#define __defined_libdai_decmap_h


#include <dai/daialg.h>


namespace dai {


/// Approximate inference algorithm DecMAP, which constructs a MAP state by decimation
/** Decimation involves repeating the following two steps until no free variables remain:
 *  - run an approximate inference algorithm,
 *  - clamp the factor with the lowest entropy to its most probable state
 */
class DecMAP : public DAIAlgFG {
    private:
        /// Stores the final MAP state
        std::vector<size_t> _state;
        /// Stores the log probability of the MAP state
        Real _logp;
        /// Maximum difference encountered so far
        Real _maxdiff;
        /// Number of iterations needed
        size_t _iters;

    public:
        /// Parameters for DecMAP
        struct Properties {
            /// Verbosity (amount of output sent to stderr)
            size_t verbose;

            /// Complete or partial reinitialization of clamped subgraphs?
            bool reinit;

            /// Name of the algorithm used to calculate the beliefs on clamped subgraphs
            std::string ianame;

            /// Parameters for the algorithm used to calculate the beliefs on clamped subgraphs
            PropertySet iaopts;
        } props;

        /// Name of this inference algorithm
        static const char *Name;

    public:
        /// Default constructor
        DecMAP() : DAIAlgFG(), _state(), _logp(), _maxdiff(), _iters(), props() {}

        /// Construct from FactorGraph \a fg and PropertySet \a opts
        /** \param fg Factor graph.
         *  \param opts Parameters @see Properties
         */
        DecMAP( const FactorGraph &fg, const PropertySet &opts );


    /// \name General InfAlg interface
    //@{
        virtual DecMAP* clone() const { return new DecMAP(*this); }
        virtual std::string identify() const;
        virtual Factor belief( const Var &v ) const { return beliefV( findVar( v ) ); }
        virtual Factor belief( const VarSet &/*vs*/ ) const;
        virtual Factor beliefV( size_t i ) const;
        virtual Factor beliefF( size_t I ) const { return belief( factor(I).vars() ); }
        virtual std::vector<Factor> beliefs() const;
        virtual Real logZ() const { return _logp; }
        virtual std::vector<size_t> findMaximum() const { return _state; }
        virtual void init() { _maxdiff = 0.0; _iters = 0; }
        virtual void init( const VarSet &/*ns*/ ) { init(); }
        virtual Real run();
        virtual Real maxDiff() const { return _maxdiff; }
        virtual size_t Iterations() const { return _iters; }
        virtual void setProperties( const PropertySet &opts );
        virtual PropertySet getProperties() const;
        virtual std::string printProperties() const;
    //@}
};


} // end of namespace dai


#endif
