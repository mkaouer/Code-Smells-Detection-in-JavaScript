#include "regexp.h"
#include "private.h"

#include <oak/oak.h>
#include <text/format.h>

namespace regexp
{
	// =============
	// = pattern_t =
	// =============

	void pattern_t::init (std::string const& pattern, OnigOptionType options)
	{
		regex_t* tmp = NULL;

		OnigErrorInfo einfo;
		int r = onig_new(&tmp, pattern.data(), pattern.data() + pattern.size(), options | ONIG_OPTION_CAPTURE_GROUP, ONIG_ENCODING_UTF8, ONIG_SYNTAX_RUBY, &einfo);
		if(r == ONIG_NORMAL)
		{
			compiled_pattern.reset(tmp, onig_free);
		}
		else
		{
			char s[ONIG_MAX_ERROR_MESSAGE_LEN];
			onig_error_code_to_str(s, r, &einfo);
			fprintf(stderr, "ERROR %s (%s)\n", s, pattern.c_str());

			if(tmp)
				onig_free(tmp);
		}
	}

	pattern_t::pattern_t (char const* pattern, OnigOptionType options) : pattern_string(pattern)
	{
		init(pattern, options);
	}

	pattern_t::pattern_t (std::string const& pattern, OnigOptionType options) : pattern_string(pattern)
	{
		init(pattern, options);
	}

	pattern_t::pattern_t (std::string const& pattern, std::string const& str_options) : pattern_string(pattern)
	{
		OnigOptionType options = ONIG_OPTION_NONE;
		iterate(it, str_options)
		{
			switch(*it)
			{
				case 'e': options |= ONIG_OPTION_EXTEND;		break;
				// case 'g': options |= ONIG_OPTION_REPEAT;		break;
				case 'i': options |= ONIG_OPTION_IGNORECASE;	break;
				case 'm': options |= ONIG_OPTION_MULTILINE;	break;
				case 's': options |= ONIG_OPTION_SINGLELINE;	break;
			}
		}
		init(pattern, options);
	}

	size_t pattern_t::indices_for_group (char const* name, int** list) const
	{
		return onig_name_to_group_numbers(compiled_pattern.get(), name, name + strlen(name), list);
	}

	// ===========
	// = match_t =
	// ===========

	std::map<std::string, std::string> const& match_t::captures () const
	{
		if(!captured_variables)
			captured_variables.reset(new std::map<std::string, std::string>(extract_captures(buffer(), region.get(), compiled_pattern.get())));
		return *captured_variables;
	}

	std::multimap<std::string, std::pair<size_t, size_t> > const& match_t::capture_indices () const
	{
		struct helper_t
		{
			static int main (UChar const* name, UChar const* name_end, int len, int* list, regex_t* pattern, void* udata)
			{
				match_t const& m = *((match_t const*)udata);
				foreach(it, list, list + len)
				{
					if(m.did_match(*it))
						m.captured_indices->insert(std::make_pair(std::string(name, name_end), std::make_pair(m.begin(*it), m.end(*it))));
				}
				return 0;
			}
		};

		if(!captured_indices)
		{
			captured_indices.reset(new std::multimap<std::string, std::pair<size_t, size_t> >);
			for(size_t i = 0; i < size(); ++i)
			{
				if(did_match(i))
					captured_indices->insert(std::make_pair(text::format("%zu", i), std::make_pair(begin(i), end(i))));
			}
			onig_foreach_name(compiled_pattern.get(), &helper_t::main, (void*)this);
		}
		return *captured_indices;
	}

	// ============
	// = Matching =
	// ============

	match_t search (pattern_t const& ptrn, char const* first, char const* last, char const* from, char const* to, OnigOptionType options)
	{
		if(ptrn)
		{
			struct helper_t { static void region_free (OnigRegion* r) { onig_region_free(r, 1); } };
			regexp::region_ptr region(onig_region_new(), &helper_t::region_free);
			if(ONIG_MISMATCH != onig_search(ptrn.get().get(), first, last, from ?: first, to ?: last, region.get(), options))
				return match_t(region, ptrn.get(), first);
		}
		return match_t();
	}

	// =====================
	// = Syntax validation =
	// =====================

	std::string validate (std::string const& pattern)
	{
		OnigErrorInfo einfo;
		regex_t* tmp = NULL;
		int r = onig_new(&tmp, pattern.data(), pattern.data() + pattern.size(), ONIG_OPTION_CAPTURE_GROUP, ONIG_ENCODING_UTF8, ONIG_SYNTAX_RUBY, &einfo);
		if(tmp)
			onig_free(tmp);

		std::string error = NULL_STR;
		if(r != ONIG_NORMAL)
		{
			char s[ONIG_MAX_ERROR_MESSAGE_LEN];
			onig_error_code_to_str(s, r, &einfo);
			error = s;
		}
		return error;
	}
} /* regexp */
