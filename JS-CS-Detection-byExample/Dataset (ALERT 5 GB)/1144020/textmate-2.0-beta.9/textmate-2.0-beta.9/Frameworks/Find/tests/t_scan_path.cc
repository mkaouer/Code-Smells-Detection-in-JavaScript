#include <test/jail.h>
#include "../src/scan_path.h"
using namespace find;

static void run_scanner (scan_path_t& scanner)
{
	scanner.start();
	while(scanner.is_running())
		sleep(0);
}

void test_simple ()
{
	test::jail_t jail;
	jail.set_content("matches", "text");
	jail.touch("dummy");

	scan_path_t scanner;
	scanner.set_search_string("text");
	scanner.set_path(jail.path());
	run_scanner(scanner);

	std::vector<match_t> matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 1);
	OAK_ASSERT_EQ(scanner.scanned_file_count(), 2);
	OAK_ASSERT_EQ(matches.front().document->path(), jail.path("matches"));
}

void test_globs ()
{
	test::jail_t jail;
	jail.set_content("text.x", "text");
	jail.set_content("text.y", "text");
	jail.set_content("text.z", "dsalkdalsjas");

	scan_path_t scanner;
	scanner.set_search_string("text");
	scanner.set_path(jail.path());
	scanner.set_glob_list("*.{x,z}");
	run_scanner(scanner);

	std::vector<match_t> matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 1);
	OAK_ASSERT_EQ(scanner.scanned_file_count(), 2);
	OAK_ASSERT_EQ(matches.front().document->path(), jail.path("text.x"));
}

void test_exclude_globs ()
{
	test::jail_t jail;
	jail.set_content("text.x", "text");
	jail.set_content("text.y", "text");
	jail.set_content("text.z", "text");

	path::glob_list_t globs;
	globs.add_exclude_glob("*.y");
	globs.add_include_glob("*");

	scan_path_t scanner;
	scanner.set_search_string("text");
	scanner.set_path(jail.path());
	scanner.set_glob_list(globs);
	run_scanner(scanner);

	std::vector<match_t> matches = scanner.accept_matches();
	OAK_ASSERT_EQ(scanner.scanned_file_count(), 2);
	OAK_ASSERT_EQ(matches.size(), 2);
	OAK_ASSERT_EQ(matches[0].document->path(), jail.path("text.x"));
	OAK_ASSERT_EQ(matches[1].document->path(), jail.path("text.z"));
}

void test_ignore_hidden ()
{
	test::jail_t jail;
	jail.set_content("visible", "text");
	jail.set_content(".hidden/hidden", "text");

	std::vector<match_t> matches;

	scan_path_t scanner;
	scanner.set_search_string("text");
	scanner.set_path(jail.path());
	run_scanner(scanner);

	matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 1);
	OAK_ASSERT_EQ(scanner.scanned_file_count(), 1);

	scan_path_t hidden_scanner;
	path::glob_list_t globs("*");
	globs.add_include_glob(".*", path::kPathItemDirectory);
	hidden_scanner.set_glob_list(globs);
	hidden_scanner.set_search_string("text");
	hidden_scanner.set_path(jail.path());
	run_scanner(hidden_scanner);

	matches = hidden_scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 2);
	OAK_ASSERT_EQ(hidden_scanner.scanned_file_count(), 2);
}

void test_follow_links ()
{
	test::jail_t jail;
	jail.touch("start/foo.txt");
	jail.set_content("linked/match.txt", "text");
	jail.ln("start/link", "linked");

	std::vector<match_t> matches;

	scan_path_t scanner;
	scanner.set_search_string("text");
	scanner.set_path(jail.path("start"));
	run_scanner(scanner);

	matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 0);
	OAK_ASSERT_EQ(scanner.scanned_file_count(), 1);

	scan_path_t follow_scanner;
	follow_scanner.set_search_string("text");
	follow_scanner.set_path(jail.path("start"));
	follow_scanner.set_follow_links(true);
	run_scanner(follow_scanner);

	matches = follow_scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 1);
	OAK_ASSERT_EQ(follow_scanner.scanned_file_count(), 2);
}

void test_file_links_are_skipped ()
{
	test::jail_t jail;
	jail.set_content("match", "text");
	jail.ln("link", "match");

	scan_path_t scanner;
	scanner.set_search_string("text");
	scanner.set_path(jail.path());
	run_scanner(scanner);

	std::vector<match_t> matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 1);
	OAK_ASSERT_EQ(scanner.scanned_file_count(), 1);
}

void test_duplicate_links ()
{
	test::jail_t jail;
	jail.set_content("dir/match.txt", "text");
	jail.ln("link", "dir");

	scan_path_t scanner;
	scanner.set_search_string("text");
	scanner.set_path(jail.path());
	scanner.set_follow_links(true);
	run_scanner(scanner);

	std::vector<match_t> matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 1);
	OAK_ASSERT_EQ(scanner.scanned_file_count(), 1);
}

void test_file_lf ()
{
	test::jail_t jail;
	jail.set_content("match", "line 1\nline 2\nline 3\nline 4\n");

	scan_path_t scanner;
	scanner.set_search_string("line ");
	scanner.set_path(jail.path());
	run_scanner(scanner);

	std::vector<match_t> matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 4);

	OAK_ASSERT_EQ(matches[0].range.min().line, 0);
	OAK_ASSERT_EQ(matches[1].range.min().line, 1);
	OAK_ASSERT_EQ(matches[2].range.min().line, 2);
	OAK_ASSERT_EQ(matches[3].range.min().line, 3);
}

void test_file_cr ()
{
	test::jail_t jail;
	jail.set_content("match", "line 1\rline 2\rline 3\rline 4\r");

	scan_path_t scanner;
	scanner.set_search_string("line ");
	scanner.set_path(jail.path());
	run_scanner(scanner);

	std::vector<match_t> matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 4);

	OAK_ASSERT_EQ(matches[0].range.min().line, 0);
	OAK_ASSERT_EQ(matches[1].range.min().line, 1);
	OAK_ASSERT_EQ(matches[2].range.min().line, 2);
	OAK_ASSERT_EQ(matches[3].range.min().line, 3);
}

void test_file_crlf ()
{
	test::jail_t jail;
	jail.set_content("match", "line 1\r\nline 2\r\nline 3\r\nline 4\r\n");

	scan_path_t scanner;
	scanner.set_search_string("line ");
	scanner.set_path(jail.path());
	run_scanner(scanner);

	std::vector<match_t> matches = scanner.accept_matches();
	OAK_ASSERT_EQ(matches.size(), 4);

	OAK_ASSERT_EQ(matches[0].range.min().line, 0);
	OAK_ASSERT_EQ(matches[1].range.min().line, 1);
	OAK_ASSERT_EQ(matches[2].range.min().line, 2);
	OAK_ASSERT_EQ(matches[3].range.min().line, 3);
}
