#include <document/collection.h>
#include <bundles/bundles.h>
#include <settings/settings.h>
#include <file/path_info.h>
#include <command/runner.h>

class UIProxyFixture : public CxxTest::GlobalFixture
{
public:
	bool setUpWorld()
	{
		static struct proxy_t : document::ui_proxy_t
		{
			void show_documents (std::vector<document::document_ptr> const& documents, std::string const& browserPath) { }
			void show_document (oak::uuid_t const& collection, document::document_ptr document, text::range_t const& range, bool bringToFront) const { }

			bool load_session (std::string const& path) const { return false; }
			bool save_session (std::string const& path, bool includeUntitled) const { return false; }

			void run (bundle_command_t const& command, ng::buffer_t const& buffer, ng::ranges_t const& selection, document::document_ptr document, std::map<std::string, std::string> const& baseEnv, document::run_callback_ptr callback)
			{
				struct delegate_t : command::delegate_t
				{
					delegate_t (document::document_ptr document) : _document(document)
					{
					}

					bool accept_html_data (command::runner_ptr runner, char const* data, size_t len) { return fprintf(stderr, "html: %.*s", (int)len, data), false; }

					void show_document (std::string const& str) { fprintf(stderr, "document: %s\n", str.c_str()); }
					void show_tool_tip (std::string const& str) { fprintf(stderr, "tool tip: %s\n", str.c_str()); }
					void show_error (bundle_command_t const& command, int rc, std::string const& out, std::string const& err) { fprintf(stderr, "error: %s%s\n", out.c_str(), err.c_str()); }

					text::range_t write_unit_to_fd (int fd, input::type unit, input::type fallbackUnit, input_format::type format, scope::selector_t const& scopeSelector, std::map<std::string, std::string>& variables, bool* inputWasSelection)
					{
						return text::range_t::undefined;
					}

					bool accept_result (std::string const& out, output::type placement, output_format::type format, output_caret::type outputCaret, text::range_t inputRange, std::map<std::string, std::string> const& environment)
					{
						if(_document && _document->is_open())
							return ng::editor_for_document(_document)->handle_result(out, placement, format, outputCaret, inputRange, environment);
						return false;
					}

				private:
					document::document_ptr _document;
				};

				std::map<std::string, std::string> env;
				bundles::item_ptr item = bundles::lookup(command.uuid);
				env = item ? item->environment(baseEnv) : baseEnv;

				if(document && document->is_open())
						env = ng::editor_for_document(document)->variables(env, file::path_attributes(document->path()));
				else	env = variables_for_path(NULL_STR, "", env);
				command::runner_ptr runner = command::runner(command, buffer, selection, env, command::delegate_ptr((command::delegate_t*)new delegate_t(document)));
				runner->launch();
				runner->wait();
			}

		} proxy;

		document::set_ui_proxy(&proxy);
		return true;
	}

} ui_proxy_fixture;

class UiProxyFixturesTests : public CxxTest::TestSuite
{
public:
	void test_ui_proxy_fixtures ()
	{
		// must contain a test class to not be ignored…
	}
};
