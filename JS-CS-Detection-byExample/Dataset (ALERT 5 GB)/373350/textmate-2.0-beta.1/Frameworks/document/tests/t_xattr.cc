#include <test/jail.h>
#include <document/document.h>

void test_bookmarks ()
{
	test::jail_t jail;
	jail.set_content("test.txt", "foo\nbar\nfud\n");
	path::set_attr(jail.path("test.txt"), "com.macromates.bookmarks", "( '1:1', '1:8', '4:2' )");

	document::document_ptr doc = document::create(jail.path("test.txt"));
	doc->sync_open();

	std::map<size_t, std::string> marks = doc->buffer().get_marks(0, doc->buffer().size(), document::kBookmarkIdentifier);
	OAK_ASSERT_EQ(marks.size(), 3);
	OAK_ASSERT_EQ(marks[0], document::kBookmarkIdentifier);
	OAK_ASSERT_EQ(marks[3], document::kBookmarkIdentifier);
	OAK_ASSERT_EQ(marks[12], document::kBookmarkIdentifier);
}

void test_selection ()
{
	test::jail_t jail;
	jail.set_content("test.txt", "foo\nbar\nfud\n");
	path::set_attr(jail.path("test.txt"), "com.macromates.selectionRange", "2:2&3:1");

	document::document_ptr doc = document::create(jail.path("test.txt"));
	doc->sync_open();

	OAK_ASSERT_EQ(doc->selection(), "2:2&3:1");

	// ng::ranges_t const sel = convert(doc->buffer(), doc->selection());
	// for(auto const& range : sel)
	// 	fprintf(stderr, "%zu-%zu\n", range.first.index, range.last.index);
}
