/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Written (W) 1999-2009 Soeren Sonnenburg
 * Copyright (C) 1999-2009 Fraunhofer Institute FIRST and Max-Planck-Society
 */

#include <shogun/machine/Machine.h>
#include <shogun/base/Parameter.h>
#include <shogun/mathematics/Math.h>

using namespace shogun;

CMachine::CMachine() : CSGObject(), max_train_time(0), labels(NULL),
	solver_type(ST_AUTO)
{
	m_parameters->add(&max_train_time, "max_train_time",
					  "Maximum training time.");
	m_parameters->add((machine_int_t*) &solver_type, "solver_type");
	m_parameters->add((CSGObject**) &labels, "labels");
	m_parameters->add(&m_store_model_features, "store_model_features",
			"Should feature data of model be stored after training?");

	m_store_model_features=false;
}

CMachine::~CMachine()
{
    SG_UNREF(labels);
}

bool CMachine::train(CFeatures* data)
{
	bool result = train_machine(data);

	if (m_store_model_features)
		store_model_features();

	return result;
}

float64_t CMachine::apply(int32_t num)
{
	SG_NOTIMPLEMENTED;
	return CMath::INFTY;
}

bool CMachine::load(FILE* srcfile)
{
	ASSERT(srcfile);
	return false;
}

bool CMachine::save(FILE* dstfile)
{
	ASSERT(dstfile);
	return false;
}

void CMachine::set_labels(CLabels* lab)
{
	SG_UNREF(labels);
	SG_REF(lab);
	labels = lab;
}

CLabels* CMachine::get_labels()
{
	SG_REF(labels);
	return labels;
}

float64_t CMachine::get_label(int32_t i)
{
	if (!labels)
		SG_ERROR("No Labels assigned\n");
	
	return labels->get_label(i);
}

void CMachine::set_max_train_time(float64_t t)
{
	max_train_time = t;
}

float64_t CMachine::get_max_train_time()
{
	return max_train_time;
}

EClassifierType CMachine::get_classifier_type()
{
	return CT_NONE;
}

void CMachine::set_solver_type(ESolverType st)
{
	solver_type = st;
}

ESolverType CMachine::get_solver_type()
{
	return solver_type;
}

void CMachine::set_store_model_features(bool store_model)
{
	m_store_model_features = store_model;
}


