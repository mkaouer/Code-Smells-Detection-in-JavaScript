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


#include "mr.h"
#include "bp.h"
#include "jtree.h"
#include <stdio.h>
#include <time.h>
#include <math.h>
#include <stdlib.h>
#include "util.h"
#include "diffs.h"


using namespace std;


const char *MR::Name = "MR";


bool MR::checkProperties() {
    if( !HasProperty("updates") )
        return false;
    if( !HasProperty("inits") )
        return false;
    if( !HasProperty("verbose") )
        return false;
    if( !HasProperty("tol") )
        return false;
    
    ConvertPropertyTo<UpdateType>("updates");
    ConvertPropertyTo<InitType>("inits");
    ConvertPropertyTo<size_t>("verbose");
    ConvertPropertyTo<double>("tol");

    return true;
}


// init N, con, nb, tJ, theta
void MR::init(size_t _N, double *_w, double *_th) {
    size_t i,j;
    
    N = _N;

    con.resize(N);
    nb.resize(N);
    tJ.resize(N);
    for(i=0; i<N; i++ ) {
        nb[i].resize(kmax);
        tJ[i].resize(kmax);
        con[i]=0;
        for(j=0; j<N; j++ )
            if( _w[i*N+j] != 0.0 ) {
                nb[i][con[i]] = j;
                tJ[i][con[i]] = tanh(_w[i*N+j]);
                con[i]++;
            }
    }
    
    theta.resize(N);
    for(i=0; i<N; i++)
      theta[i] = _th[i];
}


// calculate cors
double MR::init_cor_resp() {
    size_t j,k,l, runx,i2;
    double variab1, variab2;
    double md, maxdev;
    double thbJsite[kmax];
    double xinter;
    double rinter;
    double res[kmax];
    size_t s2;
    size_t flag;
    size_t concav;
    size_t runs = 3000;
    double eps = 0.2;
    size_t cavity;

    vector<vector<double> > tJ_org;
    vector<vector<size_t> > nb_org;
    vector<size_t> con_org;
    vector<double> theta_org;

    vector<double> xfield(N*kmax,0.0);
    vector<double> rfield(N*kmax,0.0);
    vector<double> Hfield(N,0.0);
    vector<double> devs(N*kmax,0.0);
    vector<double> devs2(N*kmax,0.0);
    vector<double> dev(N,0.0);
    vector<double> avmag(N,0.0);

    // save original tJ, nb
    nb_org = nb;
    tJ_org = tJ;
    con_org = con;
    theta_org = theta;

    maxdev = 0.0;
    for(cavity=0; cavity<N; cavity++){    // for each spin to be removed
        con = con_org;
        concav=con[cavity];

        nb = nb_org;
        tJ = tJ_org;

        //  Adapt the graph variables nb[], tJ[] and con[]
        for(size_t i=0; i<con[cavity]; i++) {
            size_t ij = nb[cavity][i];
            flag=0;
            j=0;
            do{
                if(nb[ij][j]==cavity){
                    while(j<(con[ij]-1)){
                        nb[ij][j]=nb[ij][j+1];
                        tJ[ij][j] = tJ[ij][j+1];
                        j++;
                    }
                flag=1;
                }
                j++;
            } while(flag==0);
        }
        for(size_t i=0; i<con[cavity]; i++)
            con[nb[cavity][i]]--;
        con[cavity] = 0;


        // Do everything starting from the new graph********

        makekindex();
        theta = theta_org;

        for(size_t i=0; i<kmax*N; i++)
            xfield[i] = 3.0*(2*rnd_uniform()-1.);

        for(i2=0; i2<concav; i2++){ // Subsequently apply a field to each cavity spin ****************

            s2 = nb[cavity][i2];    // identify the index of the cavity spin
            for(size_t i=0; i<con[s2]; i++)
                rfield[kmax*s2+i] = 1.;

            runx=0;
            do {      // From here start the response and belief propagation
                runx++;
                md=0.0;
                for(k=0; k<N; k++){
                    if(k!=cavity) {
                        for(size_t i=0; i<con[k]; i++)
                            thbJsite[i] = tJ[k][i];       
                        for(l=0; l<con[k]; l++){
                            xinter = 1.;      
                            rinter = 0.;
                            if(k==s2) rinter += 1.;
                            for(j=0; j<con[k]; j++)
                                if(j!=l){
                                    variab2 = tanh(xfield[kmax*nb[k][j]+kindex[k][j]]);
                                    variab1 = thbJsite[j]*variab2;
                                    xinter *= (1.+variab1)/(1.-variab1);

                                    rinter += thbJsite[j]*rfield[kmax*nb[k][j]+kindex[k][j]]*(1-variab2*variab2)/(1-variab1*variab1);
                                }

                            variab1 = 0.5*log(xinter);
                            xinter = variab1 + theta[k];
                            devs[kmax*k+l] = xinter-xfield[kmax*k+l];
                            xfield[kmax*k+l] = xfield[kmax*k+l]+devs[kmax*k+l]*eps;
                            if( fabs(devs[kmax*k+l]) > md )
                                md = fabs(devs[kmax*k+l]);

                            devs2[kmax*k+l] = rinter-rfield[kmax*k+l];
                            rfield[kmax*k+l] = rfield[kmax*k+l]+devs2[kmax*k+l]*eps;
                            if( fabs(devs2[kmax*k+l]) > md )
                                md = fabs(devs2[kmax*k+l]);
                        }
                    }
                }
            } while((md > Tol())&&(runx<runs)); // Precision condition reached -> BP and RP finished
            if(runx==runs)
                if( Verbose() >= 2 )
                    cout << "init_cor_resp: Convergence not reached (md=" << md << ")..." << endl;
            if(md > maxdev)
                maxdev = md;

            // compute the observables (i.e. magnetizations and responses)******

            for(size_t i=0; i<concav; i++){
                rinter = 0.; 
                xinter = 1.;
                if(i!=i2)
                    for(j=0; j<con[nb[cavity][i]]; j++){ 
                        variab2 = tanh(xfield[kmax*nb[nb[cavity][i]][j]+kindex[nb[cavity][i]][j]]);
                        variab1 = tJ[nb[cavity][i]][j]*variab2;
                        rinter +=  tJ[nb[cavity][i]][j]*rfield[kmax*nb[nb[cavity][i]][j]+kindex[nb[cavity][i]][j]]*(1-variab2*variab2)/(1-variab1*variab1);
                        xinter *= (1.+variab1)/(1.-variab1);
                    }
                xinter = tanh(0.5*log(xinter)+theta[nb[cavity][i]]);
                res[i] = rinter*(1-xinter*xinter);
            }

            // *******************

            for(size_t i=0; i<concav; i++)
                if(nb[cavity][i]!=s2)
            //      if(i!=i2)
                    cors[cavity][i2][i] = res[i];
                else
                    cors[cavity][i2][i] = 0;
        } // close for i2 = 0...concav
    }

    // restore nb, tJ, con
    tJ = tJ_org;
    nb = nb_org;
    con = con_org;
    theta = theta_org;

    return maxdev;
}


double MR::T(size_t i, sub_nb A) {
    // i is a variable index
    // A is a subset of nb[i]
    //
    // calculate T{(i)}_A as defined in Rizzo&Montanari e-print (2.17)
    
    sub_nb _nbi_min_A(con[i]);
    for( size_t __j = 0; __j < A.size(); __j++ )
        _nbi_min_A -= A[__j];

    double res = theta[i];
    for( size_t __j = 0; __j < _nbi_min_A.size(); __j++ ) {
        size_t _j = _nbi_min_A[__j];
        res += atanh(tJ[i][_j] * M[i][_j]);
    }
    return tanh(res);
}


double MR::T(size_t i, size_t _j) {
    sub_nb j(con[i]);
    j.clear();
    j += _j;
    return T(i,j);
}


double MR::Omega(size_t i, size_t _j, size_t _l) {
    sub_nb jl(con[i]);
    jl.clear();
    jl += _j;
    jl += _l;
    double Tijl = T(i,jl);
    return Tijl / (1.0 + tJ[i][_l] * M[i][_l] * Tijl);
}


double MR::Gamma(size_t i, size_t _j, size_t _l1, size_t _l2) {
    sub_nb jll(con[i]);
    jll.clear();
    jll += _j;
    double Tij = T(i,jll);
    jll += _l1;
    jll += _l2;
    double Tijll = T(i,jll);

    return (Tijll - Tij) / (1.0 + tJ[i][_l1] * tJ[i][_l2] * M[i][_l1] * M[i][_l2] + tJ[i][_l1] * M[i][_l1] * Tijll + tJ[i][_l2] * M[i][_l2] * Tijll);
}


double MR::Gamma(size_t i, size_t _l1, size_t _l2) {
    sub_nb ll(con[i]);
    ll.clear();
    double Ti = T(i,ll);
    ll += _l1;
    ll += _l2;
    double Till = T(i,ll);

    return (Till - Ti) / (1.0 + tJ[i][_l1] * tJ[i][_l2] * M[i][_l1] * M[i][_l2] + tJ[i][_l1] * M[i][_l1] * Till + tJ[i][_l2] * M[i][_l2] * Till);
}


double MR::_tJ(size_t i, sub_nb A) {
    // i is a variable index
    // A is a subset of nb[i]
    //
    // calculate the product of all tJ[i][_j] for _j in A
    
    size_t Asize = A.size();
    switch( Asize ) {
        case 0: return 1.0;
//      case 1: return tJ[i][A[0]];
//      case 2: return tJ[i][A[0]] * tJ[i][A[1]];
//      case 3: return tJ[i][A[0]] * tJ[i][A[1]] * tJ[i][A[2]];
        default:
            size_t __j = Asize - 1;
            size_t _j = A[__j];
            sub_nb A_j = A - _j;
            return tJ[i][_j] * _tJ(i, A_j);
    }

}


double MR::appM(size_t i, sub_nb A) {
    // i is a variable index
    // A is a subset of nb[i]
    //
    // calculate the moment of variables in A from M and cors, neglecting higher order cumulants,
    // defined as the sum over all partitions of A into subsets of cardinality two at most of the
    // product of the cumulants (either first order, i.e. M, or second order, i.e. cors) of the 
    // entries of the partitions
    
    size_t Asize = A.size();
    switch( Asize ) {
        case 0: return 1.0;
//      case 1: return M[i][A[0]];
//      case 2: return M[i][A[0]] * M[i][A[1]] + cors[i][A[0]][A[1]];
//      case 3: return M[i][A[0]] * M[i][A[1]] * M[i][A[2]] + M[i][A[0]] * cors[i][A[1]][A[2]] + M[i][A[1]] * cors[i][A[0]][A[2]] + M[i][A[2]] * cors[i][A[0]][A[1]];
        default:
            size_t __j = Asize - 1;
            size_t _j = A[__j];
            sub_nb A_j = A - _j;

            double result = M[i][_j] * appM(i, A_j);
            for( size_t __k = 0; __k < __j; __k++ ) {
                size_t _k = A[__k];
                result += cors[i][_j][_k] * appM(i,A_j - _k);
            }

            return result;
    }
}


void MR::sum_subs(size_t j, sub_nb A, double *sum_even, double *sum_odd) {
    // j is a variable index
    // A is a subset of nb[j]

    // calculate sum over all even/odd subsets B of A of _tJ(j,B) appM(j,B)

    *sum_even = 0.0;
    *sum_odd = 0.0;

    sub_nb S(A.size());
    S.clear();
    do { // for all subsets of A

        // construct subset B of A corresponding to S
        sub_nb B = A;
        B.clear();
        size_t Ssize = S.size();
        for( size_t bit = 0; bit < Ssize; bit++ )
            B += A[S[bit]];
        
        if( S.size() % 2 )
            *sum_odd += _tJ(j,B) * appM(j,B);
        else
            *sum_even += _tJ(j,B) * appM(j,B);

        ++S;
    } while( !S.empty() );
}
            

void MR::solvemcav() { 
    double sum_even, sum_odd;
    double maxdev;
    size_t maxruns = 1000;

    makekindex();
    for(size_t i=0; i<N; i++)
        for(size_t _j=0; _j<con[i]; _j++)
            M[i][_j]=0.1;

    size_t run=0;
    do {
        maxdev=0.0;
        run++;
        for(size_t i=0; i<N; i++){ // for all i
            for(size_t _j=0; _j<con[i]; _j++){ // for all j in N_i
                size_t _i = kindex[i][_j];
                size_t j = nb[i][_j];
                assert( nb[j][_i] == i );

                double newM = 0.0;
                if( Updates() == UpdateType::FULL ) {
                    // find indices in nb[j] that do not correspond with i
                    sub_nb _nbj_min_i(con[j]);
                    _nbj_min_i -= kindex[i][_j];

                    // find indices in nb[i] that do not correspond with j
                    sub_nb _nbi_min_j(con[i]);
                    _nbi_min_j -= _j;

                    sum_subs(j, _nbj_min_i, &sum_even, &sum_odd);
                    newM = (tanh(theta[j]) * sum_even + sum_odd) / (sum_even + tanh(theta[j]) * sum_odd);

                    sum_subs(i, _nbi_min_j, &sum_even, &sum_odd);
                    double denom = sum_even + tanh(theta[i]) * sum_odd;
                    double numer = 0.0;
                    for(size_t _k=0; _k<con[i]; _k++) if(_k != _j) {
                        sum_subs(i, _nbi_min_j - _k, &sum_even, &sum_odd);
                        numer += tJ[i][_k] * cors[i][_j][_k] * (tanh(theta[i]) * sum_even + sum_odd);
                    }
                    newM -= numer / denom;
                } else if( Updates() == UpdateType::LINEAR ) {
                    newM = T(j,_i);
                    for(size_t _l=0; _l<con[i]; _l++) if( _l != _j )
                        newM -= Omega(i,_j,_l) * tJ[i][_l] * cors[i][_j][_l];
                    for(size_t _l1=0; _l1<con[j]; _l1++) if( _l1 != _i )
                        for( size_t _l2=_l1+1; _l2<con[j]; _l2++) if( _l2 != _i)
                            newM += Gamma(j,_i,_l1,_l2) * tJ[j][_l1] * tJ[j][_l2] * cors[j][_l1][_l2];
                }

                double dev = newM - M[i][_j];
//              dev *= 0.02;
                if( fabs(dev) >= maxdev )
                    maxdev = fabs(dev);

                newM = M[i][_j] + dev;
                if( fabs(newM) > 1.0 )
                    newM = sign(newM);
                M[i][_j] = newM;
            }
        }
    } while((maxdev>Tol())&&(run<maxruns));

    updateMaxDiff( maxdev );

    if(run==maxruns){
        if( Verbose() >= 1 )
            cout << "solve_mcav: Convergence not reached (maxdev=" << maxdev << ")..." << endl;
    }
}

 
void MR::solveM() { 
    for(size_t i=0; i<N; i++) {
        if( Updates() == UpdateType::FULL ) {
            // find indices in nb[i]
            sub_nb _nbi(con[i]);

            // calc numerator1 and denominator1
            double sum_even, sum_odd;
            sum_subs(i, _nbi, &sum_even, &sum_odd);

            Mag[i] = (tanh(theta[i]) * sum_even + sum_odd) / (sum_even + tanh(theta[i]) * sum_odd);

        } else if( Updates() == UpdateType::LINEAR ) {
            sub_nb empty(con[i]);
            empty.clear();
            Mag[i] = T(i,empty);

            for(size_t _l1=0; _l1<con[i]; _l1++)
                for( size_t _l2=_l1+1; _l2<con[i]; _l2++)
                    Mag[i] += Gamma(i,_l1,_l2) * tJ[i][_l1] * tJ[i][_l2] * cors[i][_l1][_l2];
        }
        if(fabs(Mag[i])>1.)
            Mag[i] = sign(Mag[i]);
    }
}


double MR::init_cor() {
    for( size_t i = 0; i < nrVars(); i++ ) {
        vector<Factor> pairq;
        if( Inits() == InitType::CLAMPING ) {
            BP bpcav(*this, Properties()("updates",string("SEQMAX"))("tol", 1e-9)("maxiter", 1000UL)("verbose", 0UL));
            bpcav.makeCavity( var(i) );
            pairq = calcPairBeliefs( bpcav, delta(var(i)), false );
        } else if( Inits() == InitType::EXACT ) {
            JTree jtcav(*this, Properties()("updates",string("HUGIN"))("verbose", 0UL) );
            jtcav.makeCavity( var(i) );
            pairq = calcPairBeliefs( jtcav, delta(var(i)), false );
        }
        for( size_t jk = 0; jk < pairq.size(); jk++ ) {
            VarSet::const_iterator kit = pairq[jk].vars().begin();
            size_t j = findVar( *(kit) );
            size_t k = findVar( *(++kit) );
            pairq[jk].normalize(Prob::NORMPROB);
            double cor = (pairq[jk][3] - pairq[jk][2] - pairq[jk][1] + pairq[jk][0]) - (pairq[jk][3] + pairq[jk][2] - pairq[jk][1] - pairq[jk][0]) * (pairq[jk][3] - pairq[jk][2] + pairq[jk][1] - pairq[jk][0]);
            for( size_t _j = 0; _j < con[i]; _j++ ) if( nb[i][_j] == j )
                for( size_t _k = 0; _k < con[i]; _k++ ) if( nb[i][_k] == k ) {
                    cors[i][_j][_k] = cor;
                    cors[i][_k][_j] = cor;
                }
        }
    }
}


string MR::identify() const { 
    stringstream result (stringstream::out);
    result << Name << GetProperties();
    return result.str();
}


double MR::run() {
    if( supported ) {
        if( Verbose() >= 1 )
            cout << "Starting " << identify() << "...";

        clock_t tic = toc();
//        Diffs diffs(nrVars(), 1.0);

        M.resize(N);
        for(size_t i=0; i<N; i++)
          M[i].resize(kmax);

        cors.resize(N);
        for(size_t i=0; i<N; i++)
          cors[i].resize(kmax);
        for(size_t i=0; i<N; i++)
          for(size_t j=0; j<kmax; j++)
            cors[i][j].resize(kmax);

        kindex.resize(N);
        for(size_t i=0; i<N; i++)
          kindex[i].resize(kmax);

        if( Inits() == InitType::RESPPROP )
            updateMaxDiff( init_cor_resp() );
        else if( Inits() == InitType::EXACT )
            init_cor(); // FIXME no MaxDiff() calculation
        else if( Inits() == InitType::CLAMPING )
            init_cor(); // FIXME no MaxDiff() calculation

        solvemcav();

        Mag.resize(N);
        solveM();

        if( Verbose() >= 1 )
            cout << "MR needed " << toc() - tic << " clocks." << endl;

        return 0.0;
    } else
        return NAN;
}


void MR::makekindex() {
    for(size_t i=0; i<N; i++)
        for(size_t j=0; j<con[i]; j++) {
            size_t ij = nb[i][j];       // ij is the j'th neighbour of spin i
            size_t k=0;
            while( nb[ij][k] != i )
                k++;
            kindex[i][j] = k;   // the j'th neighbour of spin i has spin i as its k'th neighbour
        }
}


Factor MR::belief( const Var &n ) const {
    if( supported ) {
        size_t i = findVar( n );

        double x[2];
        x[0] = 0.5 - Mag[i] / 2.0;
        x[1] = 0.5 + Mag[i] / 2.0;

        return Factor( n, x );
    } else
        return Factor();
}


vector<Factor> MR::beliefs() const {
    vector<Factor> result;
    for( size_t i = 0; i < nrVars(); i++ )
        result.push_back( belief( var(i) ) );
    return result;
}



MR::MR( const FactorGraph &fg, const Properties &opts ) : DAIAlgFG(fg, opts), supported(true) {
    // check whether all vars in fg are binary
    // check whether connectivity is <= kmax
    for( size_t i = 0; i < fg.nrVars(); i++ )
        if( (fg.var(i).states() > 2) || (fg.delta(fg.var(i)).size() > kmax) ) {
            supported = false;
            break;
        }
    
    if( !supported )
        return;
    
    // check whether all interactions are pairwise or single
    for( size_t I = 0; I < fg.nrFactors(); I++ )
        if( fg.factor(I).vars().size() > 2 ) {
            supported = false;
            break;
        }

    if( !supported )
        return;

    // create w and th
    size_t _N = fg.nrVars();

    double *w = new double[_N*_N];
    double *th = new double[_N];
    
    for( size_t i = 0; i < _N; i++ ) {
        th[i] = 0.0;
        for( size_t j = 0; j < _N; j++ )
            w[i*_N+j] = 0.0;
    }

    for( size_t I = 0; I < fg.nrFactors(); I++ ) {
        const Factor &psi = fg.factor(I);
        if( psi.vars().size() == 1 ) {
            size_t i = fg.findVar( *(psi.vars().begin()) );
            th[i] += 0.5 * log(psi[1] / psi[0]);
        } else if( psi.vars().size() == 2 ) {
            size_t i = fg.findVar( *(psi.vars().begin()) );
            VarSet::const_iterator jit = psi.vars().begin();
            size_t j = fg.findVar( *(++jit) );

            w[i*_N+j] += 0.25 * log(psi[3] * psi[0] / (psi[2] * psi[1])); 
            w[j*_N+i] += 0.25 * log(psi[3] * psi[0] / (psi[2] * psi[1])); 

            th[i] += 0.25 * log(psi[3] / psi[2] * psi[1] / psi[0]);
            th[j] += 0.25 * log(psi[3] / psi[1] * psi[2] / psi[0]);
        }
    }
    
    init(_N, w, th);

    delete th;
    delete w;
}
