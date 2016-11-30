#include <buffer/buffer.h>
#include <selection/selection.h>

class ColumnMovementTests : public CxxTest::TestSuite
{
public:
	void test_column_movement ()
	{
		ng::buffer_t buf(
			"  lorem\n"  // 1:  0-08
			"  sit\n"    // 2:  8-14
			"  ipsum\n"  // 3: 14-22
			"\n"         // 4: 22-23
			"  dollar\n" // 5: 23-32
			"  it\n"     // 6: 32-37
		);

		TS_ASSERT_EQUALS(ng::to_s(buf, ng::move(buf, ng::ranges_t( 2), kSelectionMoveToEndOfColumn)), "3:3");
		TS_ASSERT_EQUALS(ng::to_s(buf, ng::move(buf, ng::ranges_t( 5), kSelectionMoveToEndOfColumn)), "3:6");
		TS_ASSERT_EQUALS(ng::to_s(buf, ng::move(buf, ng::ranges_t( 7), kSelectionMoveToEndOfColumn)), "3:8");
		TS_ASSERT_EQUALS(ng::to_s(buf, ng::move(buf, ng::ranges_t(21), kSelectionMoveToEndOfColumn)), "5:8");
	}
};
