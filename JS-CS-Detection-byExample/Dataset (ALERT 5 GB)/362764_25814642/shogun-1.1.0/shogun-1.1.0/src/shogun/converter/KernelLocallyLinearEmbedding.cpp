/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Written (W) 2011 Sergey Lisitsyn
 * Copyright (C) 2011 Berlin Institute of Technology and Max-Planck-Society
 */

#include <shogun/converter/KernelLocallyLinearEmbedding.h>
#ifdef HAVE_LAPACK
#include <shogun/mathematics/lapack.h>
#include <shogun/lib/FibonacciHeap.h>
#include <shogun/lib/common.h>
#include <shogun/base/DynArray.h>
#include <shogun/mathematics/Math.h>
#include <shogun/io/SGIO.h>
#include <shogun/lib/Signal.h>

#ifdef HAVE_PTHREAD
#include <pthread.h>
#endif

using namespace shogun;

#ifndef DOXYGEN_SHOULD_SKIP_THIS
struct LK_RECONSTRUCTION_THREAD_PARAM
{
	/// starting index of loop
	int32_t idx_start;
	/// end loop index
	int32_t idx_stop;
	/// step of loop
	int32_t idx_step;
	/// number of neighbors
	int32_t m_k;
	/// number of vectors
	int32_t N;
	/// matrix containing indexes of ith neighbors of jth vector in ith column
	const int32_t* neighborhood_matrix;
	/// local gram matrix 
	float64_t* local_gram_matrix;
	/// gram matrix
	const float64_t* kernel_matrix;
	/// vector used for solving equation 
	float64_t* id_vector;
	/// reconstruction shift
	float64_t reconstruction_shift;
	/// weight matrix
	float64_t* W_matrix;
};

struct K_NEIGHBORHOOD_THREAD_PARAM
{
	/// starting index of loop
	int32_t idx_start;
	/// step of loop
	int32_t idx_step;
	/// end index of loop
	int32_t idx_stop;
	/// number of vectors
	int32_t N;
	/// number of neighbors
	int32_t m_k;
	/// fibonacci heaps
	CFibonacciHeap* heap;
	/// kernel matrix
	const float64_t* kernel_matrix;
	/// matrix containing neighbors indexes
	int32_t* neighborhood_matrix;
};
#endif /* DOXYGEN_SHOULD_SKIP_THIS */

CKernelLocallyLinearEmbedding::CKernelLocallyLinearEmbedding() :
		CLocallyLinearEmbedding()
{
}

CKernelLocallyLinearEmbedding::CKernelLocallyLinearEmbedding(CKernel* kernel) :
		CLocallyLinearEmbedding()
{
	set_kernel(kernel);
}

const char* CKernelLocallyLinearEmbedding::get_name() const
{
	return "KernelLocallyLinearEmbedding";
};

CKernelLocallyLinearEmbedding::~CKernelLocallyLinearEmbedding()
{
}

CFeatures* CKernelLocallyLinearEmbedding::apply(CFeatures* features)
{
	ASSERT(features);
	SG_REF(features);

	// get dimensionality and number of vectors of data
	int32_t N = features->get_num_vectors();
	if (m_k>=N)
		SG_ERROR("Number of neighbors (%d) should be less than number of objects (%d).\n",
		         m_k, N);

	// compute kernel matrix
	ASSERT(m_kernel);
	m_kernel->init(features,features);
	CSimpleFeatures<float64_t>* embedding = embed_kernel(m_kernel);
	m_kernel->cleanup();
	SG_UNREF(features);
	return (CFeatures*)embedding;
}

CSimpleFeatures<float64_t>* CKernelLocallyLinearEmbedding::embed_kernel(CKernel* kernel)
{
	SGMatrix<float64_t> kernel_matrix = kernel->get_kernel_matrix();
	SGMatrix<int32_t> neighborhood_matrix = get_neighborhood_matrix(kernel_matrix,m_k);

	SGMatrix<float64_t> M_matrix = construct_weight_matrix(kernel_matrix,neighborhood_matrix);
	neighborhood_matrix.destroy_matrix();

	SGMatrix<float64_t> nullspace = construct_embedding(M_matrix,m_target_dim);
	M_matrix.destroy_matrix();

	return new CSimpleFeatures<float64_t>(nullspace);
}

SGMatrix<float64_t> CKernelLocallyLinearEmbedding::construct_weight_matrix(SGMatrix<float64_t> kernel_matrix, 
                                                                           SGMatrix<int32_t> neighborhood_matrix)
{
	int32_t N = kernel_matrix.num_cols;
	// loop variables
	int32_t t;
#ifdef HAVE_PTHREAD
	int32_t num_threads = parallel->get_num_threads();
	ASSERT(num_threads>0);
	// allocate threads
	pthread_t* threads = SG_MALLOC(pthread_t, num_threads);
	LK_RECONSTRUCTION_THREAD_PARAM* parameters = SG_MALLOC(LK_RECONSTRUCTION_THREAD_PARAM, num_threads);
	pthread_attr_t attr;
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
#else
	int32_t num_threads = 1;
#endif 
	float64_t* W_matrix = SG_CALLOC(float64_t, N*N);
	// init matrices and norm factor to be used
	float64_t* local_gram_matrix = SG_MALLOC(float64_t, m_k*m_k*num_threads);
	float64_t* id_vector = SG_MALLOC(float64_t, m_k*num_threads);

#ifdef HAVE_PTHREAD
	for (t=0; t<num_threads; t++)
	{
		parameters[t].idx_start = t;
		parameters[t].idx_step = num_threads;
		parameters[t].idx_stop = N;
		parameters[t].m_k = m_k;
		parameters[t].N = N;
		parameters[t].neighborhood_matrix = neighborhood_matrix.matrix;
		parameters[t].kernel_matrix = kernel_matrix.matrix;
		parameters[t].local_gram_matrix = local_gram_matrix+(m_k*m_k)*t;
		parameters[t].id_vector = id_vector+m_k*t;
		parameters[t].W_matrix = W_matrix;
		parameters[t].reconstruction_shift = m_reconstruction_shift;
		pthread_create(&threads[t], &attr, run_linearreconstruction_thread, (void*)&parameters[t]);
	}
	for (t=0; t<num_threads; t++)
		pthread_join(threads[t], NULL);
	pthread_attr_destroy(&attr);
	SG_FREE(parameters);
	SG_FREE(threads);
#else
	LK_RECONSTRUCTION_THREAD_PARAM single_thread_param;
	single_thread_param.idx_start = 0;
	single_thread_param.idx_step = 1;
	single_thread_param.idx_stop = N;
	single_thread_param.m_k = m_k;
	single_thread_param.N = N;
	single_thread_param.neighborhood_matrix = neighborhood_matrix.matrix;
	single_thread_param.local_gram_matrix = local_gram_matrix;
	single_thread_param.kernel_matrix = kernel_matrix.matrix;
	single_thread_param.id_vector = id_vector;
	single_thread_param.W_matrix = W_matrix;
	run_linearreconstruction_thread((void*)single_thread_param);
#endif

	// clean
	SG_FREE(id_vector);
	SG_FREE(local_gram_matrix);

	return SGMatrix<float64_t>(W_matrix,N,N);
}

void* CKernelLocallyLinearEmbedding::run_linearreconstruction_thread(void* p)
{
	LK_RECONSTRUCTION_THREAD_PARAM* parameters = (LK_RECONSTRUCTION_THREAD_PARAM*)p;
	int32_t idx_start = parameters->idx_start;
	int32_t idx_step = parameters->idx_step;
	int32_t idx_stop = parameters->idx_stop;
	int32_t m_k = parameters->m_k;
	int32_t N = parameters->N;
	const int32_t* neighborhood_matrix = parameters->neighborhood_matrix;
	float64_t* local_gram_matrix = parameters->local_gram_matrix;
	const float64_t* kernel_matrix = parameters->kernel_matrix;
	float64_t* id_vector = parameters->id_vector;
	float64_t* W_matrix = parameters->W_matrix;
	float64_t reconstruction_shift = parameters->reconstruction_shift;

	int32_t i,j,k;
	float64_t norming,trace;

	for (i=idx_start; i<idx_stop; i+=idx_step)
	{
		for (j=0; j<m_k; j++)
		{
			for (k=0; k<m_k; k++)
				local_gram_matrix[j*m_k+k] = 
					kernel_matrix[i*N+i] -
					kernel_matrix[i*N+neighborhood_matrix[j*N+i]] -
					kernel_matrix[i*N+neighborhood_matrix[k*N+i]] +
					kernel_matrix[neighborhood_matrix[j*N+i]*N+neighborhood_matrix[k*N+i]];
		}

		for (j=0; j<m_k; j++)
			id_vector[j] = 1.0;

		// compute tr(C)
		trace = 0.0;
		for (j=0; j<m_k; j++)
			trace += local_gram_matrix[j*m_k+j];
		
		// regularize gram matrix
		for (j=0; j<m_k; j++)
			local_gram_matrix[j*m_k+j] += reconstruction_shift*trace;

		clapack_dposv(CblasColMajor,CblasLower,m_k,1,local_gram_matrix,m_k,id_vector,m_k);

		// normalize weights
		norming=0.0;
		for (j=0; j<m_k; j++)
			norming += id_vector[j];

		cblas_dscal(m_k,1.0/norming,id_vector,1);

		memset(local_gram_matrix,0,sizeof(float64_t)*m_k*m_k);
		cblas_dger(CblasColMajor,m_k,m_k,1.0,id_vector,1,id_vector,1,local_gram_matrix,m_k);

		// put weights into W matrix
		W_matrix[N*i+i] += 1.0;
		for (j=0; j<m_k; j++)
		{
			W_matrix[N*i+neighborhood_matrix[j*N+i]] -= id_vector[j];
			W_matrix[N*neighborhood_matrix[j*N+i]+i] -= id_vector[j];
		}
		for (j=0; j<m_k; j++)
		{
			for (k=0; k<m_k; k++)
				W_matrix[N*neighborhood_matrix[j*N+i]+neighborhood_matrix[k*N+i]]+=local_gram_matrix[j*m_k+k];
		}
	}
	return NULL;
}

SGMatrix<int32_t> CKernelLocallyLinearEmbedding::get_neighborhood_matrix(SGMatrix<float64_t> kernel_matrix, int32_t k)
{
	int32_t t;
	int32_t N = kernel_matrix.num_cols;
	// init matrix and heap to be used
	int32_t* neighborhood_matrix = SG_MALLOC(int32_t, N*k);
#ifdef HAVE_PTHREAD
	int32_t num_threads = parallel->get_num_threads();
	ASSERT(num_threads>0);
	K_NEIGHBORHOOD_THREAD_PARAM* parameters = SG_MALLOC(K_NEIGHBORHOOD_THREAD_PARAM, num_threads);
	pthread_t* threads = SG_MALLOC(pthread_t, num_threads);
	pthread_attr_t attr;
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
#else
	int32_t num_threads = 1;
#endif
	CFibonacciHeap** heaps = SG_MALLOC(CFibonacciHeap*, num_threads);
	for (t=0; t<num_threads; t++)
		heaps[t] = new CFibonacciHeap(N);

#ifdef HAVE_PTHREAD
	for (t=0; t<num_threads; t++)
	{
		parameters[t].idx_start = t;
		parameters[t].idx_step = num_threads;
		parameters[t].idx_stop = N;
		parameters[t].m_k = k;
		parameters[t].N = N;
		parameters[t].heap = heaps[t];
		parameters[t].neighborhood_matrix = neighborhood_matrix;
		parameters[t].kernel_matrix = kernel_matrix.matrix;
		pthread_create(&threads[t], &attr, run_neighborhood_thread, (void*)&parameters[t]);
	}
	for (t=0; t<num_threads; t++)
		pthread_join(threads[t], NULL);
	pthread_attr_destroy(&attr);
	SG_FREE(threads);
	SG_FREE(parameters);
#else
	K_NEIGHBORHOOD_THREAD_PARAM single_thread_param;
	single_thread_param.idx_start = 0;
	single_thread_param.idx_step = 1;
	single_thread_param.idx_stop = N;
	single_thread_param.m_k = k;
	single_thread_param.N = N;
	single_thread_param.heap = heaps[0]
	single_thread_param.neighborhood_matrix = neighborhood_matrix;
	single_thread_param.kernel_matrix = kernel_matrix.matrix;
	run_neighborhood_thread((void*)&single_thread_param);
#endif

	for (t=0; t<num_threads; t++)
		delete heaps[t];
	SG_FREE(heaps);

	return SGMatrix<int32_t>(neighborhood_matrix,k,N);
}

void* CKernelLocallyLinearEmbedding::run_neighborhood_thread(void* p)
{
	K_NEIGHBORHOOD_THREAD_PARAM* parameters = (K_NEIGHBORHOOD_THREAD_PARAM*)p;
	int32_t idx_start = parameters->idx_start;
	int32_t idx_step = parameters->idx_step;
	int32_t idx_stop = parameters->idx_stop;
	int32_t N = parameters->N;
	int32_t m_k = parameters->m_k;
	CFibonacciHeap* heap = parameters->heap;
	const float64_t* kernel_matrix = parameters->kernel_matrix;
	int32_t* neighborhood_matrix = parameters->neighborhood_matrix;

	int32_t i,j;
	float64_t tmp;
	for (i=idx_start; i<idx_stop; i+=idx_step)
	{
		for (j=0; j<N; j++)
		{
			heap->insert(j,kernel_matrix[i*N+i]-2*kernel_matrix[i*N+j]+kernel_matrix[j*N+j]);
		}

		heap->extract_min(tmp);

		for (j=0; j<m_k; j++)
			neighborhood_matrix[j*N+i] = heap->extract_min(tmp);

		heap->clear();
	}

	return NULL;
}
#endif /* HAVE_LAPACK */
