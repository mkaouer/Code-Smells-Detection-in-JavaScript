/*  This file is part of libDAI - http://www.libdai.org/
 *
 *  libDAI is licensed under the terms of the GNU General Public License version
 *  2, or (at your option) any later version. libDAI is distributed without any
 *  warranty. See the file COPYING for more details.
 *
 *  Copyright (C) 2006-2009  Joris Mooij  [joris dot mooij at libdai dot org]
 *  Copyright (C) 2006-2007  Radboud University Nijmegen, The Netherlands
 */


#include <iostream>
#include <sstream>
#include <map>
#include <set>
#include <algorithm>
#include <stack>
#include <dai/bp.h>
#include <dai/util.h>
#include <dai/properties.h>


namespace dai {


using namespace std;


const char *BP::Name = "BP";


#define DAI_BP_FAST 1


void BP::setProperties( const PropertySet &opts ) {
    DAI_ASSERT( opts.hasKey("tol") );
    DAI_ASSERT( opts.hasKey("maxiter") );
    DAI_ASSERT( opts.hasKey("logdomain") );
    DAI_ASSERT( opts.hasKey("updates") );

    props.tol = opts.getStringAs<Real>("tol");
    props.maxiter = opts.getStringAs<size_t>("maxiter");
    props.logdomain = opts.getStringAs<bool>("logdomain");
    props.updates = opts.getStringAs<Properties::UpdateType>("updates");

    if( opts.hasKey("verbose") )
        props.verbose = opts.getStringAs<size_t>("verbose");
    else
        props.verbose = 0;
    if( opts.hasKey("damping") )
        props.damping = opts.getStringAs<Real>("damping");
    else
        props.damping = 0.0;
    if( opts.hasKey("inference") )
        props.inference = opts.getStringAs<Properties::InfType>("inference");
    else
        props.inference = Properties::InfType::SUMPROD;
}


PropertySet BP::getProperties() const {
    PropertySet opts;
    opts.Set( "tol", props.tol );
    opts.Set( "maxiter", props.maxiter );
    opts.Set( "verbose", props.verbose );
    opts.Set( "logdomain", props.logdomain );
    opts.Set( "updates", props.updates );
    opts.Set( "damping", props.damping );
    opts.Set( "inference", props.inference );
    return opts;
}


string BP::printProperties() const {
    stringstream s( stringstream::out );
    s << "[";
    s << "tol=" << props.tol << ",";
    s << "maxiter=" << props.maxiter << ",";
    s << "verbose=" << props.verbose << ",";
    s << "logdomain=" << props.logdomain << ",";
    s << "updates=" << props.updates << ",";
    s << "damping=" << props.damping << ",";
    s << "inference=" << props.inference << "]";
    return s.str();
}


void BP::construct() {
    // create edge properties
    _edges.clear();
    _edges.reserve( nrVars() );
    _edge2lut.clear();
    if( props.updates == Properties::UpdateType::SEQMAX )
        _edge2lut.reserve( nrVars() );
    for( size_t i = 0; i < nrVars(); ++i ) {
        _edges.push_back( vector<EdgeProp>() );
        _edges[i].reserve( nbV(i).size() );
        if( props.updates == Properties::UpdateType::SEQMAX ) {
            _edge2lut.push_back( vector<LutType::iterator>() );
            _edge2lut[i].reserve( nbV(i).size() );
        }
        foreach( const Neighbor &I, nbV(i) ) {
            EdgeProp newEP;
            newEP.message = Prob( var(i).states() );
            newEP.newMessage = Prob( var(i).states() );

            if( DAI_BP_FAST ) {
                newEP.index.reserve( factor(I).states() );
                for( IndexFor k( var(i), factor(I).vars() ); k.valid(); ++k )
                    newEP.index.push_back( k );
            }

            newEP.residual = 0.0;
            _edges[i].push_back( newEP );
            if( props.updates == Properties::UpdateType::SEQMAX )
                _edge2lut[i].push_back( _lut.insert( make_pair( newEP.residual, make_pair( i, _edges[i].size() - 1 ))) );
        }
    }
}


void BP::init() {
    Real c = props.logdomain ? 0.0 : 1.0;
    for( size_t i = 0; i < nrVars(); ++i ) {
        foreach( const Neighbor &I, nbV(i) ) {
            message( i, I.iter ).fill( c );
            newMessage( i, I.iter ).fill( c );
            if( props.updates == Properties::UpdateType::SEQMAX )
                updateResidual( i, I.iter, 0.0 );
        }
    }
}


void BP::findMaxResidual( size_t &i, size_t &_I ) {
    DAI_ASSERT( !_lut.empty() );
    LutType::const_iterator largestEl = _lut.end();
    --largestEl;
    i  = largestEl->second.first;
    _I = largestEl->second.second;
}


void BP::calcNewMessage( size_t i, size_t _I ) {
    // calculate updated message I->i
    size_t I = nbV(i,_I);

    Factor Fprod( factor(I) );
    Prob &prod = Fprod.p();
    if( props.logdomain )
        prod.takeLog();

    // Calculate product of incoming messages and factor I
    foreach( const Neighbor &j, nbF(I) )
        if( j != i ) { // for all j in I \ i
            // prod_j will be the product of messages coming into j
            Prob prod_j( var(j).states(), props.logdomain ? 0.0 : 1.0 );
            foreach( const Neighbor &J, nbV(j) )
                if( J != I ) { // for all J in nb(j) \ I
                    if( props.logdomain )
                        prod_j += message( j, J.iter );
                    else
                        prod_j *= message( j, J.iter );
                }

            // multiply prod with prod_j
            if( !DAI_BP_FAST ) {
                /* UNOPTIMIZED (SIMPLE TO READ, BUT SLOW) VERSION */
                if( props.logdomain )
                    Fprod += Factor( var(j), prod_j );
                else
                    Fprod *= Factor( var(j), prod_j );
            } else {
                /* OPTIMIZED VERSION */
                size_t _I = j.dual;
                // ind is the precalculated IndexFor(j,I) i.e. to x_I == k corresponds x_j == ind[k]
                const ind_t &ind = index(j, _I);
                for( size_t r = 0; r < prod.size(); ++r )
                    if( props.logdomain )
                        prod[r] += prod_j[ind[r]];
                    else
                        prod[r] *= prod_j[ind[r]];
            }
        }

    if( props.logdomain ) {
        prod -= prod.max();
        prod.takeExp();
    }

    // Marginalize onto i
    Prob marg;
    if( !DAI_BP_FAST ) {
        /* UNOPTIMIZED (SIMPLE TO READ, BUT SLOW) VERSION */
        if( props.inference == Properties::InfType::SUMPROD )
            marg = Fprod.marginal( var(i) ).p();
        else
            marg = Fprod.maxMarginal( var(i) ).p();
    } else {
        /* OPTIMIZED VERSION */
        marg = Prob( var(i).states(), 0.0 );
        // ind is the precalculated IndexFor(i,I) i.e. to x_I == k corresponds x_i == ind[k]
        const ind_t ind = index(i,_I);
        if( props.inference == Properties::InfType::SUMPROD )
            for( size_t r = 0; r < prod.size(); ++r )
                marg[ind[r]] += prod[r];
        else
            for( size_t r = 0; r < prod.size(); ++r )
                if( prod[r] > marg[ind[r]] )
                    marg[ind[r]] = prod[r];
        marg.normalize();
    }

    // Store result
    if( props.logdomain )
        newMessage(i,_I) = marg.log();
    else
        newMessage(i,_I) = marg;

    // Update the residual if necessary
    if( props.updates == Properties::UpdateType::SEQMAX )
        updateResidual( i, _I , dist( newMessage( i, _I ), message( i, _I ), Prob::DISTLINF ) );
}


// BP::run does not check for NANs for performance reasons
// Somehow NaNs do not often occur in BP...
Real BP::run() {
    if( props.verbose >= 1 )
        cerr << "Starting " << identify() << "...";
    if( props.verbose >= 3)
        cerr << endl;

    double tic = toc();
    vector<Real> diffs( nrVars(), INFINITY );
    Real maxDiff = INFINITY;

    vector<Factor> old_beliefs;
    old_beliefs.reserve( nrVars() );
    for( size_t i = 0; i < nrVars(); ++i )
        old_beliefs.push_back( beliefV(i) );

    size_t nredges = nrEdges();
    vector<Edge> update_seq;
    if( props.updates == Properties::UpdateType::SEQMAX ) {
        // do the first pass
        for( size_t i = 0; i < nrVars(); ++i )
            foreach( const Neighbor &I, nbV(i) )
                calcNewMessage( i, I.iter );
    } else {
        update_seq.reserve( nredges );
        /// \todo Investigate whether performance increases by switching the order of the following two loops:
        for( size_t i = 0; i < nrVars(); ++i )
            foreach( const Neighbor &I, nbV(i) )
                update_seq.push_back( Edge( i, I.iter ) );
    }

    // do several passes over the network until maximum number of iterations has
    // been reached or until the maximum belief difference is smaller than tolerance
    for( _iters=0; _iters < props.maxiter && maxDiff > props.tol; ++_iters ) {
        if( props.updates == Properties::UpdateType::SEQMAX ) {
            // Residuals-BP by Koller et al.
            for( size_t t = 0; t < nredges; ++t ) {
                // update the message with the largest residual
                size_t i, _I;
                findMaxResidual( i, _I );
                updateMessage( i, _I );

                // I->i has been updated, which means that residuals for all
                // J->j with J in nb[i]\I and j in nb[J]\i have to be updated
                foreach( const Neighbor &J, nbV(i) ) {
                    if( J.iter != _I ) {
                        foreach( const Neighbor &j, nbF(J) ) {
                            size_t _J = j.dual;
                            if( j != i )
                                calcNewMessage( j, _J );
                        }
                    }
                }
            }
        } else if( props.updates == Properties::UpdateType::PARALL ) {
            // Parallel updates
            for( size_t i = 0; i < nrVars(); ++i )
                foreach( const Neighbor &I, nbV(i) )
                    calcNewMessage( i, I.iter );

            for( size_t i = 0; i < nrVars(); ++i )
                foreach( const Neighbor &I, nbV(i) )
                    updateMessage( i, I.iter );
        } else {
            // Sequential updates
            if( props.updates == Properties::UpdateType::SEQRND )
                random_shuffle( update_seq.begin(), update_seq.end() );

            foreach( const Edge &e, update_seq ) {
                calcNewMessage( e.first, e.second );
                updateMessage( e.first, e.second );
            }
        }

        // calculate new beliefs and compare with old ones
        for( size_t i = 0; i < nrVars(); ++i ) {
            Factor nb( beliefV(i) );
            diffs[i] = dist( nb, old_beliefs[i], Prob::DISTLINF );
            old_beliefs[i] = nb;
        }
        maxDiff = max( diffs );

        if( props.verbose >= 3 )
            cerr << Name << "::run:  maxdiff " << maxDiff << " after " << _iters+1 << " passes" << endl;
    }

    if( maxDiff > _maxdiff )
        _maxdiff = maxDiff;

    if( props.verbose >= 1 ) {
        if( maxDiff > props.tol ) {
            if( props.verbose == 1 )
                cerr << endl;
                cerr << Name << "::run:  WARNING: not converged within " << props.maxiter << " passes (" << toc() - tic << " seconds)...final maxdiff:" << maxDiff << endl;
        } else {
            if( props.verbose >= 3 )
                cerr << Name << "::run:  ";
                cerr << "converged in " << _iters << " passes (" << toc() - tic << " seconds)." << endl;
        }
    }

    return maxDiff;
}


void BP::calcBeliefV( size_t i, Prob &p ) const {
    p = Prob( var(i).states(), props.logdomain ? 0.0 : 1.0 );
    foreach( const Neighbor &I, nbV(i) )
        if( props.logdomain )
            p += newMessage( i, I.iter );
        else
            p *= newMessage( i, I.iter );
}


void BP::calcBeliefF( size_t I, Prob &p ) const {
    Factor Fprod( factor( I ) );
    Prob &prod = Fprod.p();

    if( props.logdomain )
        prod.takeLog();

    foreach( const Neighbor &j, nbF(I) ) {
        // prod_j will be the product of messages coming into j
        Prob prod_j( var(j).states(), props.logdomain ? 0.0 : 1.0 );
        foreach( const Neighbor &J, nbV(j) )
            if( J != I ) { // for all J in nb(j) \ I
                if( props.logdomain )
                    prod_j += newMessage( j, J.iter );
                else
                    prod_j *= newMessage( j, J.iter );
            }

        // multiply prod with prod_j
        if( !DAI_BP_FAST ) {
            /* UNOPTIMIZED (SIMPLE TO READ, BUT SLOW) VERSION */
            if( props.logdomain )
                Fprod += Factor( var(j), prod_j );
            else
                Fprod *= Factor( var(j), prod_j );
        } else {
            /* OPTIMIZED VERSION */
            size_t _I = j.dual;
            // ind is the precalculated IndexFor(j,I) i.e. to x_I == k corresponds x_j == ind[k]
            const ind_t & ind = index(j, _I);

            for( size_t r = 0; r < prod.size(); ++r ) {
                if( props.logdomain )
                    prod[r] += prod_j[ind[r]];
                else
                    prod[r] *= prod_j[ind[r]];
            }
        }
    }

    p = prod;
}


Factor BP::beliefV( size_t i ) const {
    Prob p;
    calcBeliefV( i, p );

    if( props.logdomain ) {
        p -= p.max();
        p.takeExp();
    }
    p.normalize();

    return( Factor( var(i), p ) );
}


Factor BP::beliefF( size_t I ) const {
    Prob p;
    calcBeliefF( I, p );

    if( props.logdomain ) {
        p -= p.max();
        p.takeExp();
    }
    p.normalize();

    return( Factor( factor(I).vars(), p ) );
}


Factor BP::belief( const Var &n ) const {
    return( beliefV( findVar( n ) ) );
}


vector<Factor> BP::beliefs() const {
    vector<Factor> result;
    for( size_t i = 0; i < nrVars(); ++i )
        result.push_back( beliefV(i) );
    for( size_t I = 0; I < nrFactors(); ++I )
        result.push_back( beliefF(I) );
    return result;
}


Factor BP::belief( const VarSet &ns ) const {
    if( ns.size() == 1 )
        return belief( *(ns.begin()) );
    else {
        size_t I;
        for( I = 0; I < nrFactors(); I++ )
            if( factor(I).vars() >> ns )
                break;
        DAI_ASSERT( I != nrFactors() );
        return beliefF(I).marginal(ns);
    }
}


Real BP::logZ() const {
    Real sum = 0.0;
    for(size_t i = 0; i < nrVars(); ++i )
        sum += (1.0 - nbV(i).size()) * beliefV(i).entropy();
    for( size_t I = 0; I < nrFactors(); ++I )
        sum -= dist( beliefF(I), factor(I), Prob::DISTKL );
    return sum;
}


string BP::identify() const {
    return string(Name) + printProperties();
}


void BP::init( const VarSet &ns ) {
    for( VarSet::const_iterator n = ns.begin(); n != ns.end(); ++n ) {
        size_t ni = findVar( *n );
        foreach( const Neighbor &I, nbV( ni ) ) {
            Real val = props.logdomain ? 0.0 : 1.0;
            message( ni, I.iter ).fill( val );
            newMessage( ni, I.iter ).fill( val );
            if( props.updates == Properties::UpdateType::SEQMAX )
                updateResidual( ni, I.iter, 0.0 );
        }
    }
}


void BP::updateMessage( size_t i, size_t _I ) {
    if( recordSentMessages )
        _sentMessages.push_back(make_pair(i,_I));
    if( props.damping == 0.0 ) {
        message(i,_I) = newMessage(i,_I);
        if( props.updates == Properties::UpdateType::SEQMAX )
            updateResidual( i, _I, 0.0 );
    } else {
        message(i,_I) = (message(i,_I) ^ props.damping) * (newMessage(i,_I) ^ (1.0 - props.damping));
        if( props.updates == Properties::UpdateType::SEQMAX )
            updateResidual( i, _I, dist( newMessage(i,_I), message(i,_I), Prob::DISTLINF ) );
    }
}


void BP::updateResidual( size_t i, size_t _I, Real r ) {
    EdgeProp* pEdge = &_edges[i][_I];
    pEdge->residual = r;

    // rearrange look-up table (delete and reinsert new key)
    _lut.erase( _edge2lut[i][_I] );
    _edge2lut[i][_I] = _lut.insert( make_pair( r, make_pair(i, _I) ) );
}


std::vector<size_t> BP::findMaximum() const {
    vector<size_t> maximum( nrVars() );
    vector<bool> visitedVars( nrVars(), false );
    vector<bool> visitedFactors( nrFactors(), false );
    stack<size_t> scheduledFactors;
    for( size_t i = 0; i < nrVars(); ++i ) {
        if( visitedVars[i] )
            continue;
        visitedVars[i] = true;

        // Maximise with respect to variable i
        Prob prod;
        calcBeliefV( i, prod );
        maximum[i] = prod.argmax().first;

        foreach( const Neighbor &I, nbV(i) )
            if( !visitedFactors[I] )
                scheduledFactors.push(I);

        while( !scheduledFactors.empty() ){
            size_t I = scheduledFactors.top();
            scheduledFactors.pop();
            if( visitedFactors[I] )
                continue;
            visitedFactors[I] = true;

            // Evaluate if some neighboring variables still need to be fixed; if not, we're done
            bool allDetermined = true;
            foreach( const Neighbor &j, nbF(I) )
                if( !visitedVars[j.node] ) {
                    allDetermined = false;
                    break;
                }
            if( allDetermined )
                continue;

            // Calculate product of incoming messages on factor I
            Prob prod2;
            calcBeliefF( I, prod2 );

            // The allowed configuration is restrained according to the variables assigned so far:
            // pick the argmax amongst the allowed states
            Real maxProb = numeric_limits<Real>::min();
            State maxState( factor(I).vars() );
            for( State s( factor(I).vars() ); s.valid(); ++s ){
                // First, calculate whether this state is consistent with variables that
                // have been assigned already
                bool allowedState = true;
                foreach( const Neighbor &j, nbF(I) )
                    if( visitedVars[j.node] && maximum[j.node] != s(var(j.node)) ) {
                        allowedState = false;
                        break;
                    }
                // If it is consistent, check if its probability is larger than what we have seen so far
                if( allowedState && prod2[s] > maxProb ) {
                    maxState = s;
                    maxProb = prod2[s];
                }
            }

            // Decode the argmax
            foreach( const Neighbor &j, nbF(I) ) {
                if( visitedVars[j.node] ) {
                    // We have already visited j earlier - hopefully our state is consistent
                    if( maximum[j.node] != maxState(var(j.node)) && props.verbose >= 1 )
                        cerr << "BP::findMaximum - warning: maximum not consistent due to loops." << endl;
                } else {
                    // We found a consistent state for variable j
                    visitedVars[j.node] = true;
                    maximum[j.node] = maxState( var(j.node) );
                    foreach( const Neighbor &J, nbV(j) )
                        if( !visitedFactors[J] )
                            scheduledFactors.push(J);
                }
            }
        }
    }
    return maximum;
}


} // end of namespace dai
