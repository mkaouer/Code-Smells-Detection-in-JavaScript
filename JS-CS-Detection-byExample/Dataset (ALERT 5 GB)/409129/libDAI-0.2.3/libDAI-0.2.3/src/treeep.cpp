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
#include <fstream>
#include <vector>
#include <dai/jtree.h>
#include <dai/treeep.h>
#include <dai/util.h>


namespace dai {


using namespace std;


const char *TreeEP::Name = "TREEEP";


void TreeEP::setProperties( const PropertySet &opts ) {
    DAI_ASSERT( opts.hasKey("tol") );
    DAI_ASSERT( opts.hasKey("maxiter") );
    DAI_ASSERT( opts.hasKey("verbose") );
    DAI_ASSERT( opts.hasKey("type") );

    props.tol = opts.getStringAs<Real>("tol");
    props.maxiter = opts.getStringAs<size_t>("maxiter");
    props.verbose = opts.getStringAs<size_t>("verbose");
    props.type = opts.getStringAs<Properties::TypeType>("type");
}


PropertySet TreeEP::getProperties() const {
    PropertySet opts;
    opts.Set( "tol", props.tol );
    opts.Set( "maxiter", props.maxiter );
    opts.Set( "verbose", props.verbose );
    opts.Set( "type", props.type );
    return opts;
}


string TreeEP::printProperties() const {
    stringstream s( stringstream::out );
    s << "[";
    s << "tol=" << props.tol << ",";
    s << "maxiter=" << props.maxiter << ",";
    s << "verbose=" << props.verbose << ",";
    s << "type=" << props.type << "]";
    return s.str();
}


TreeEP::TreeEPSubTree::TreeEPSubTree( const RootedTree &subRTree, const RootedTree &jt_RTree, const std::vector<Factor> &jt_Qa, const std::vector<Factor> &jt_Qb, const Factor *I ) : _Qa(), _Qb(), _RTree(), _a(), _b(), _I(I), _ns(), _nsrem(), _logZ(0.0) {
    _ns = _I->vars();

    // Make _Qa, _Qb, _a and _b corresponding to the subtree
    _b.reserve( subRTree.size() );
    _Qb.reserve( subRTree.size() );
    _RTree.reserve( subRTree.size() );
    for( size_t i = 0; i < subRTree.size(); i++ ) {
        size_t alpha1 = subRTree[i].n1;     // old index 1
        size_t alpha2 = subRTree[i].n2;     // old index 2
        size_t beta;                        // old sep index
        for( beta = 0; beta < jt_RTree.size(); beta++ )
            if( UEdge( jt_RTree[beta].n1, jt_RTree[beta].n2 ) == UEdge( alpha1, alpha2 ) )
                break;
        DAI_ASSERT( beta != jt_RTree.size() );

        size_t newalpha1 = find(_a.begin(), _a.end(), alpha1) - _a.begin();
        if( newalpha1 == _a.size() ) {
            _Qa.push_back( Factor( jt_Qa[alpha1].vars(), 1.0 ) );
            _a.push_back( alpha1 );         // save old index in index conversion table
        }

        size_t newalpha2 = find(_a.begin(), _a.end(), alpha2) - _a.begin();
        if( newalpha2 == _a.size() ) {
            _Qa.push_back( Factor( jt_Qa[alpha2].vars(), 1.0 ) );
            _a.push_back( alpha2 );         // save old index in index conversion table
        }

        _RTree.push_back( DEdge( newalpha1, newalpha2 ) );
        _Qb.push_back( Factor( jt_Qb[beta].vars(), 1.0 ) );
        _b.push_back( beta );
    }

    // Find remaining variables (which are not in the new root)
    _nsrem = _ns / _Qa[0].vars();
}


void TreeEP::TreeEPSubTree::init() {
    for( size_t alpha = 0; alpha < _Qa.size(); alpha++ )
        _Qa[alpha].fill( 1.0 );
    for( size_t beta = 0; beta < _Qb.size(); beta++ )
        _Qb[beta].fill( 1.0 );
}


void TreeEP::TreeEPSubTree::InvertAndMultiply( const std::vector<Factor> &Qa, const std::vector<Factor> &Qb ) {
    for( size_t alpha = 0; alpha < _Qa.size(); alpha++ )
        _Qa[alpha] = Qa[_a[alpha]] / _Qa[alpha];

    for( size_t beta = 0; beta < _Qb.size(); beta++ )
        _Qb[beta] = Qb[_b[beta]] / _Qb[beta];
}


void TreeEP::TreeEPSubTree::HUGIN_with_I( std::vector<Factor> &Qa, std::vector<Factor> &Qb ) {
    // Backup _Qa and _Qb
    vector<Factor> _Qa_old(_Qa);
    vector<Factor> _Qb_old(_Qb);

    // Clear Qa and Qb
    for( size_t alpha = 0; alpha < _Qa.size(); alpha++ )
        Qa[_a[alpha]].fill( 0.0 );
    for( size_t beta = 0; beta < _Qb.size(); beta++ )
        Qb[_b[beta]].fill( 0.0 );

    // For all states of _nsrem
    for( State s(_nsrem); s.valid(); s++ ) {
        // Multiply root with slice of I
        _Qa[0] *= _I->slice( _nsrem, s );

        // CollectEvidence
        for( size_t i = _RTree.size(); (i--) != 0; ) {
            // clamp variables in nsrem
            for( VarSet::const_iterator n = _nsrem.begin(); n != _nsrem.end(); n++ )
                if( _Qa[_RTree[i].n2].vars() >> *n ) {
                    Factor delta( *n, 0.0 );
                    delta[s(*n)] = 1.0;
                    _Qa[_RTree[i].n2] *= delta;
                }
            Factor new_Qb = _Qa[_RTree[i].n2].marginal( _Qb[i].vars(), false );
            _Qa[_RTree[i].n1] *= new_Qb / _Qb[i];
            _Qb[i] = new_Qb;
        }

        // DistributeEvidence
        for( size_t i = 0; i < _RTree.size(); i++ ) {
            Factor new_Qb = _Qa[_RTree[i].n1].marginal( _Qb[i].vars(), false );
            _Qa[_RTree[i].n2] *= new_Qb / _Qb[i];
            _Qb[i] = new_Qb;
        }

        // Store Qa's and Qb's
        for( size_t alpha = 0; alpha < _Qa.size(); alpha++ )
            Qa[_a[alpha]].p() += _Qa[alpha].p();
        for( size_t beta = 0; beta < _Qb.size(); beta++ )
            Qb[_b[beta]].p() += _Qb[beta].p();

        // Restore _Qa and _Qb
        _Qa = _Qa_old;
        _Qb = _Qb_old;
    }

    // Normalize Qa and Qb
    _logZ = 0.0;
    for( size_t alpha = 0; alpha < _Qa.size(); alpha++ ) {
        _logZ += log(Qa[_a[alpha]].sum());
        Qa[_a[alpha]].normalize();
    }
    for( size_t beta = 0; beta < _Qb.size(); beta++ ) {
        _logZ -= log(Qb[_b[beta]].sum());
        Qb[_b[beta]].normalize();
    }
}


Real TreeEP::TreeEPSubTree::logZ( const std::vector<Factor> &Qa, const std::vector<Factor> &Qb ) const {
    Real s = 0.0;
    for( size_t alpha = 0; alpha < _Qa.size(); alpha++ )
        s += (Qa[_a[alpha]] * _Qa[alpha].log(true)).sum();
    for( size_t beta = 0; beta < _Qb.size(); beta++ )
        s -= (Qb[_b[beta]] * _Qb[beta].log(true)).sum();
    return s + _logZ;
}


TreeEP::TreeEP( const FactorGraph &fg, const PropertySet &opts ) : JTree(fg, opts("updates",string("HUGIN")), false), _maxdiff(0.0), _iters(0), props(), _Q() {
    setProperties( opts );

    if( !isConnected() )
       DAI_THROW(FACTORGRAPH_NOT_CONNECTED);

    if( opts.hasKey("tree") ) {
        construct( opts.GetAs<RootedTree>("tree") );
    } else {
        if( props.type == Properties::TypeType::ORG || props.type == Properties::TypeType::ALT ) {
            // ORG: construct weighted graph with as weights a crude estimate of the
            // mutual information between the nodes
            // ALT: construct weighted graph with as weights an upper bound on the
            // effective interaction strength between pairs of nodes

            WeightedGraph<Real> wg;
            for( size_t i = 0; i < nrVars(); ++i ) {
                Var v_i = var(i);
                VarSet di = delta(i);
                for( VarSet::const_iterator j = di.begin(); j != di.end(); j++ )
                    if( v_i < *j ) {
                        VarSet ij(v_i,*j);
                        Factor piet;
                        for( size_t I = 0; I < nrFactors(); I++ ) {
                            VarSet Ivars = factor(I).vars();
                            if( props.type == Properties::TypeType::ORG ) {
                                if( (Ivars == v_i) || (Ivars == *j) )
                                    piet *= factor(I);
                                else if( Ivars >> ij )
                                    piet *= factor(I).marginal( ij );
                            } else {
                                if( Ivars >> ij )
                                    piet *= factor(I);
                            }
                        }
                        if( props.type == Properties::TypeType::ORG ) {
                            if( piet.vars() >> ij ) {
                                piet = piet.marginal( ij );
                                Factor pietf = piet.marginal(v_i) * piet.marginal(*j);
                                wg[UEdge(i,findVar(*j))] = dist( piet, pietf, Prob::DISTKL );
                            } else
                                wg[UEdge(i,findVar(*j))] = 0;
                        } else {
                            wg[UEdge(i,findVar(*j))] = piet.strength(v_i, *j);
                        }
                    }
            }

            // find maximal spanning tree
            construct( MaxSpanningTreePrims( wg ) );
        } else
            DAI_THROW(UNKNOWN_ENUM_VALUE);
    }
}


void TreeEP::construct( const RootedTree &tree ) {
    vector<VarSet> Cliques;
    for( size_t i = 0; i < tree.size(); i++ )
        Cliques.push_back( VarSet( var(tree[i].n1), var(tree[i].n2) ) );

    // Construct a weighted graph (each edge is weighted with the cardinality
    // of the intersection of the nodes, where the nodes are the elements of
    // Cliques).
    WeightedGraph<int> JuncGraph;
    for( size_t i = 0; i < Cliques.size(); i++ )
        for( size_t j = i+1; j < Cliques.size(); j++ ) {
            size_t w = (Cliques[i] & Cliques[j]).size();
            if( w )
                JuncGraph[UEdge(i,j)] = w;
        }

    // Construct maximal spanning tree using Prim's algorithm
    RTree = MaxSpanningTreePrims( JuncGraph );

    // Construct corresponding region graph

    // Create outer regions
    ORs.reserve( Cliques.size() );
    for( size_t i = 0; i < Cliques.size(); i++ )
        ORs.push_back( FRegion( Factor(Cliques[i], 1.0), 1.0 ) );

    // For each factor, find an outer region that subsumes that factor.
    // Then, multiply the outer region with that factor.
    // If no outer region can be found subsuming that factor, label the
    // factor as off-tree.
    fac2OR.clear();
    fac2OR.resize( nrFactors(), -1U );
    for( size_t I = 0; I < nrFactors(); I++ ) {
        size_t alpha;
        for( alpha = 0; alpha < nrORs(); alpha++ )
            if( OR(alpha).vars() >> factor(I).vars() ) {
                fac2OR[I] = alpha;
                break;
            }
    // DIFF WITH JTree::GenerateJT: assert
    }
    RecomputeORs();

    // Create inner regions and edges
    IRs.reserve( RTree.size() );
    vector<Edge> edges;
    edges.reserve( 2 * RTree.size() );
    for( size_t i = 0; i < RTree.size(); i++ ) {
        edges.push_back( Edge( RTree[i].n1, IRs.size() ) );
        edges.push_back( Edge( RTree[i].n2, IRs.size() ) );
        // inner clusters have counting number -1
        IRs.push_back( Region( Cliques[RTree[i].n1] & Cliques[RTree[i].n2], -1.0 ) );
    }

    // create bipartite graph
    G.construct( nrORs(), nrIRs(), edges.begin(), edges.end() );

    // Check counting numbers
    checkCountingNumbers();

    // Create messages and beliefs
    Qa.clear();
    Qa.reserve( nrORs() );
    for( size_t alpha = 0; alpha < nrORs(); alpha++ )
        Qa.push_back( OR(alpha) );

    Qb.clear();
    Qb.reserve( nrIRs() );
    for( size_t beta = 0; beta < nrIRs(); beta++ )
        Qb.push_back( Factor( IR(beta), 1.0 ) );

    // DIFF with JTree::GenerateJT:  no messages

    // DIFF with JTree::GenerateJT:
    // Create factor approximations
    _Q.clear();
    size_t PreviousRoot = (size_t)-1;
    for( size_t I = 0; I < nrFactors(); I++ )
        if( offtree(I) ) {
            // find efficient subtree
            RootedTree subTree;
            /*size_t subTreeSize =*/ findEfficientTree( factor(I).vars(), subTree, PreviousRoot );
            PreviousRoot = subTree[0].n1;
            //subTree.resize( subTreeSize );  // FIXME
//          cerr << "subtree " << I << " has size " << subTreeSize << endl;

            TreeEPSubTree QI( subTree, RTree, Qa, Qb, &factor(I) );
            _Q[I] = QI;
        }
    // Previous root of first off-tree factor should be the root of the last off-tree factor
    for( size_t I = 0; I < nrFactors(); I++ )
        if( offtree(I) ) {
            RootedTree subTree;
            /*size_t subTreeSize =*/ findEfficientTree( factor(I).vars(), subTree, PreviousRoot );
            PreviousRoot = subTree[0].n1;
            //subTree.resize( subTreeSize ); // FIXME
//          cerr << "subtree " << I << " has size " << subTreeSize << endl;

            TreeEPSubTree QI( subTree, RTree, Qa, Qb, &factor(I) );
            _Q[I] = QI;
            break;
        }

    if( props.verbose >= 3 ) {
        cerr << "Resulting regiongraph: " << *this << endl;
    }
}


string TreeEP::identify() const {
    return string(Name) + printProperties();
}


void TreeEP::init() {
    runHUGIN();

    // Init factor approximations
    for( size_t I = 0; I < nrFactors(); I++ )
        if( offtree(I) )
            _Q[I].init();
}


Real TreeEP::run() {
    if( props.verbose >= 1 )
        cerr << "Starting " << identify() << "...";
    if( props.verbose >= 3)
        cerr << endl;

    double tic = toc();
    vector<Real> diffs( nrVars(), INFINITY );
    Real maxDiff = INFINITY;

    vector<Factor> old_beliefs;
    old_beliefs.reserve( nrVars() );
    for( size_t i = 0; i < nrVars(); i++ )
        old_beliefs.push_back(belief(var(i)));

    // do several passes over the network until maximum number of iterations has
    // been reached or until the maximum belief difference is smaller than tolerance
    for( _iters=0; _iters < props.maxiter && maxDiff > props.tol; _iters++ ) {
        for( size_t I = 0; I < nrFactors(); I++ )
            if( offtree(I) ) {
                _Q[I].InvertAndMultiply( Qa, Qb );
                _Q[I].HUGIN_with_I( Qa, Qb );
                _Q[I].InvertAndMultiply( Qa, Qb );
            }

        // calculate new beliefs and compare with old ones
        for( size_t i = 0; i < nrVars(); i++ ) {
            Factor nb( belief(var(i)) );
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


Real TreeEP::logZ() const {
    Real s = 0.0;

    // entropy of the tree
    for( size_t beta = 0; beta < nrIRs(); beta++ )
        s -= Qb[beta].entropy();
    for( size_t alpha = 0; alpha < nrORs(); alpha++ )
        s += Qa[alpha].entropy();

    // energy of the on-tree factors
    for( size_t alpha = 0; alpha < nrORs(); alpha++ )
        s += (OR(alpha).log(true) * Qa[alpha]).sum();

    // energy of the off-tree factors
    for( size_t I = 0; I < nrFactors(); I++ )
        if( offtree(I) )
            s += (_Q.find(I))->second.logZ( Qa, Qb );

    return s;
}


} // end of namespace dai
