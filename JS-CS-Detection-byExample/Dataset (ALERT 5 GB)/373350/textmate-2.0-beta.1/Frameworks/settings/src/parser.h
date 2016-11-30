#ifndef INI_PARSER_H_LJ9XOMJG
#define INI_PARSER_H_LJ9XOMJG

struct ini_file_t
{
	struct section_t
	{
		section_t (std::vector<std::string> const& names) : names(names) { }

		struct value_t
		{
			value_t (std::string const& name, std::string const& value) : name(name), value(value) { }
			std::string name, value;
		};

		std::vector<std::string> names;
		std::vector<value_t> values;
	};

	ini_file_t (std::string const& path) : path(path) { }

	void new_section (std::vector<std::string> const& names)
	{
		sections.emplace_back(names);
	}

	void insert_value (std::string const& name, std::string const& value)
	{
		if(sections.empty())
			new_section(std::vector<std::string>());
		sections.back().values.push_back(section_t::value_t(name, value));
	}

	std::string path;
	std::vector<section_t> sections;
};

char const* parse_ini (char const* p, char const* pe, ini_file_t& iniFile);

#endif /* end of include guard: INI_PARSER_H_LJ9XOMJG */
