/*  This file is part of libDAI - http://www.libdai.org/
 *
 *  libDAI is licensed under the terms of the GNU General Public License version
 *  2, or (at your option) any later version. libDAI is distributed without any
 *  warranty. See the file COPYING for more details.
 *
 *  Copyright (C) 2006-2009  Joris Mooij  [joris dot mooij at libdai dot org]
 *  Copyright (C) 2006-2007  Radboud University Nijmegen, The Netherlands
 */


#include <algorithm>
#include <cmath>
#include <boost/dynamic_bitset.hpp>
#include <dai/regiongraph.h>
#include <dai/factorgraph.h>
#include <dai/clustergraph.h>


namespace dai {


using namespace std;


RegionGraph::RegionGraph( const FactorGraph &fg, const std::vector<Region> &ors, const std::vector<Region> &irs, const std::vector<std::pair<size_t,size_t> > &edges) : FactorGraph(fg), G(), ORs(), IRs(irs), fac2OR() {
    // Copy outer regions (giving them counting number 1.0)
    ORs.reserve( ors.size() );
    for( vector<Region>::const_iterator alpha = ors.begin(); alpha != ors.end(); alpha++ )
        ORs.push_back( FRegion(Factor(*alpha, 1.0), 1.0) );

    // For each factor, find an outer region that subsumes that factor.
    // Then, multiply the outer region with that factor.
    fac2OR.reserve( nrFactors() );
    for( size_t I = 0; I < nrFactors(); I++ ) {
        size_t alpha;
        for( alpha = 0; alpha < nrORs(); alpha++ )
            if( OR(alpha).vars() >> factor(I).vars() ) {
                fac2OR.push_back( alpha );
                break;
            }
        DAI_ASSERT( alpha != nrORs() );
    }
    RecomputeORs();

    // Create bipartite graph
    G.construct( nrORs(), nrIRs(), edges.begin(), edges.end() );

    // Check counting numbers
#ifdef DAI_DEBUG
    checkCountingNumbers();
#endif
}


// CVM style
RegionGraph::RegionGraph( const FactorGraph &fg, const std::vector<VarSet> &cl ) : FactorGraph(fg), G(), ORs(), IRs(), fac2OR() {
    // Retain only maximal clusters
    ClusterGraph cg( cl );
    cg.eraseNonMaximal();

    // Create outer regions, giving them counting number 1.0
    ORs.reserve( cg.size() );
    foreach( const VarSet &ns, cg.clusters )
        ORs.push_back( FRegion(Factor(ns, 1.0), 1.0) );

    // For each factor, find an outer regions that subsumes that factor.
    // Then, multiply the outer region with that factor.
    fac2OR.reserve( nrFactors() );
    for( size_t I = 0; I < nrFactors(); I++ ) {
        size_t alpha;
        for( alpha = 0; alpha < nrORs(); alpha++ )
            if( OR(alpha).vars() >> factor(I).vars() ) {
                fac2OR.push_back( alpha );
                break;
            }
        DAI_ASSERT( alpha != nrORs() );
    }
    RecomputeORs();

    // Create inner regions - first pass
    set<VarSet> betas;
    for( size_t alpha = 0; alpha < cg.clusters.size(); alpha++ )
        for( size_t alpha2 = alpha; (++alpha2) != cg.clusters.size(); ) {
            VarSet intersection = cg.clusters[alpha] & cg.clusters[alpha2];
            if( intersection.size() > 0 )
                betas.insert( intersection );
        }

    // Create inner regions - subsequent passes
    set<VarSet> new_betas;
    do {
        new_betas.clear();
        for( set<VarSet>::const_iterator gamma = betas.begin(); gamma != betas.end(); gamma++ )
            for( set<VarSet>::const_iterator gamma2 = gamma; (++gamma2) != betas.end(); ) {
                VarSet intersection = (*gamma) & (*gamma2);
                if( (intersection.size() > 0) && (betas.count(intersection) == 0) )
                    new_betas.insert( intersection );
            }
        betas.insert(new_betas.begin(), new_betas.end());
    } while( new_betas.size() );

    // Create inner regions - store them in the bipartite graph
    IRs.reserve( betas.size() );
    for( set<VarSet>::const_iterator beta = betas.begin(); beta != betas.end(); beta++ )
        IRs.push_back( Region(*beta,0.0) );

    // Create edges
    vector<pair<size_t,size_t> > edges;
    for( size_t beta = 0; beta < nrIRs(); beta++ ) {
        for( size_t alpha = 0; alpha < nrORs(); alpha++ ) {
            if( OR(alpha).vars() >> IR(beta) )
                edges.push_back( pair<size_t,size_t>(alpha,beta) );
        }
    }

    // Create bipartite graph
    G.construct( nrORs(), nrIRs(), edges.begin(), edges.end() );

    // Calculate counting numbers
    calcCountingNumbers();

    // Check counting numbers
#ifdef DAI_DEBUG
    checkCountingNumbers();
#endif
}


void RegionGraph::calcCountingNumbers() {
    // Calculates counting numbers of inner regions based upon counting numbers of outer regions

    vector<vector<size_t> > ancestors(nrIRs());
    boost::dynamic_bitset<> assigned(nrIRs());
    for( size_t beta = 0; beta < nrIRs(); beta++ ) {
        IR(beta).c() = 0.0;
        for( size_t beta2 = 0; beta2 < nrIRs(); beta2++ )
            if( (beta2 != beta) && IR(beta2) >> IR(beta) )
                ancestors[beta].push_back(beta2);
    }

    bool new_counting;
    do {
        new_counting = false;
        for( size_t beta = 0; beta < nrIRs(); beta++ ) {
            if( !assigned[beta] ) {
                bool has_unassigned_ancestor = false;
                for( vector<size_t>::const_iterator beta2 = ancestors[beta].begin(); (beta2 != ancestors[beta].end()) && !has_unassigned_ancestor; beta2++ )
                    if( !assigned[*beta2] )
                        has_unassigned_ancestor = true;
                if( !has_unassigned_ancestor ) {
                    Real c = 1.0;
                    foreach( const Neighbor &alpha, nbIR(beta) )
                        c -= OR(alpha).c();
                    for( vector<size_t>::const_iterator beta2 = ancestors[beta].begin(); beta2 != ancestors[beta].end(); beta2++ )
                        c -= IR(*beta2).c();
                    IR(beta).c() = c;
                    assigned.set(beta, true);
                    new_counting = true;
                }
            }
        }
    } while( new_counting );
}


bool RegionGraph::checkCountingNumbers() const {
    // Checks whether the counting numbers satisfy the fundamental relation

    bool all_valid = true;
    for( vector<Var>::const_iterator n = vars().begin(); n != vars().end(); n++ ) {
        Real c_n = 0.0;
        for( size_t alpha = 0; alpha < nrORs(); alpha++ )
            if( OR(alpha).vars().contains( *n ) )
                c_n += OR(alpha).c();
        for( size_t beta = 0; beta < nrIRs(); beta++ )
            if( IR(beta).contains( *n ) )
                c_n += IR(beta).c();
        if( fabs(c_n - 1.0) > 1e-15 ) {
            all_valid = false;
            cerr << "WARNING: counting numbers do not satisfy relation for " << *n << "(c_n = " << c_n << ")." << endl;
        }
    }

    return all_valid;
}


void RegionGraph::RecomputeORs() {
    for( size_t alpha = 0; alpha < nrORs(); alpha++ )
        OR(alpha).fill( 1.0 );
    for( size_t I = 0; I < nrFactors(); I++ )
        if( fac2OR[I] != -1U )
            OR( fac2OR[I] ) *= factor( I );
}


void RegionGraph::RecomputeORs( const VarSet &ns ) {
    for( size_t alpha = 0; alpha < nrORs(); alpha++ )
        if( OR(alpha).vars().intersects( ns ) )
            OR(alpha).fill( 1.0 );
    for( size_t I = 0; I < nrFactors(); I++ )
        if( fac2OR[I] != -1U )
            if( OR( fac2OR[I] ).vars().intersects( ns ) )
                OR( fac2OR[I] ) *= factor( I );
}


void RegionGraph::RecomputeOR( size_t I ) {
    DAI_ASSERT( I < nrFactors() );
    if( fac2OR[I] != -1U ) {
        size_t alpha = fac2OR[I];
        OR(alpha).fill( 1.0 );
        for( size_t J = 0; J < nrFactors(); J++ )
            if( fac2OR[J] == alpha )
                OR(alpha) *= factor( J );
    }
}


/// Send RegionGraph to output stream
ostream & operator << (ostream & os, const RegionGraph & rg) {
    os << "Outer regions" << endl;
    for( size_t alpha = 0; alpha < rg.nrORs(); alpha++ )
        os << alpha << ": " << rg.OR(alpha).vars() << ": c = " << rg.OR(alpha).c() << endl;

    os << "Inner regions" << endl;
    for( size_t beta = 0; beta < rg.nrIRs(); beta++ )
        os << beta << ": " << (VarSet)rg.IR(beta) << ": c = " << rg.IR(beta).c() << endl;

    os << "Edges" << endl;
    for( size_t alpha = 0; alpha < rg.nrORs(); alpha++ )
        foreach( const RegionGraph::Neighbor &beta, rg.nbOR(alpha) )
            os << alpha << "->" << beta << endl;

    return(os);
}


} // end of namespace dai
