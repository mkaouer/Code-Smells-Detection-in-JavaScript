#include <text/utf8.h>

void test_safe_end ()
{
	char const* first = "æblegrød";
	char const* last  = first + strlen(first);

	OAK_ASSERT_EQ(utf8::find_safe_end(first, first),   first);
	OAK_ASSERT_EQ(utf8::find_safe_end(first, first+1), first);
	OAK_ASSERT_EQ(utf8::find_safe_end(first, first+2), first+2);
	OAK_ASSERT_EQ(utf8::find_safe_end(first, first+3), first+3);

	OAK_ASSERT_EQ(utf8::find_safe_end(first, last),    last);
	OAK_ASSERT_EQ(utf8::find_safe_end(first, last-1),  last-1);
	OAK_ASSERT_EQ(utf8::find_safe_end(first, last-2),  last-3);
	OAK_ASSERT_EQ(utf8::find_safe_end(first, last-3),  last-3);
	OAK_ASSERT_EQ(utf8::find_safe_end(first, last-4),  last-4);

	OAK_ASSERT_EQ(utf8::find_safe_end(first+1, first+2), first+2);
}

void test_utf8_iterate ()
{
	static std::string const str = "“Æblegrød…” — 𠻵";
	static std::vector<uint32_t> const expected = { 0x201C, 0xC6, 0x62, 0x6C, 0x65, 0x67, 0x72, 0xF8, 0x64, 0x2026, 0x201D, 0x20, 0x2014, 0x20, 0x20EF5 };

	std::vector<uint32_t> chars;
	foreach(ch, utf8::make(str.data()), utf8::make(str.data() + str.size()))
		chars.push_back(*ch);

	OAK_ASSERT_EQ(chars, expected);
}

void test_to_ch ()
{
	OAK_ASSERT_EQ(utf8::to_ch("♥"),             0x2665);
	OAK_ASSERT_EQ(utf8::to_ch("𠻵"),            0x20EF5);
	OAK_ASSERT_EQ(utf8::to_ch("\U0010FFFF"),  0x10FFFF);

	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00000003)), 0x00000003);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00000030)), 0x00000030);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00000300)), 0x00000300);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00003000)), 0x00003000);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00030000)), 0x00030000);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00300000)), 0x00300000);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x03000000)), 0x03000000);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x30000000)), 0x30000000);

	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x20000003)), 0x20000003);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x02000030)), 0x02000030);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00200300)), 0x00200300);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00023000)), 0x00023000);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00032000)), 0x00032000);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x00300200)), 0x00300200);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x03000020)), 0x03000020);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x30000002)), 0x30000002);

	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x3FFFFFFF)), 0x3fFFFFFF);
	OAK_ASSERT_EQ(utf8::to_ch(utf8::to_s(0x40000000)), 0x40000000);
}

void test_to_s ()
{
	OAK_ASSERT_EQ(utf8::to_s(    0x2665),           "♥");
	OAK_ASSERT_EQ(utf8::to_s(   0x20EF5),           "𠻵");
	OAK_ASSERT_EQ(utf8::to_s(  0x10FFFF),  "\U0010FFFF");

	static uint32_t const chars[] = { 0x201C, 0xC6, 0x62, 0x6C, 0x65, 0x67, 0x72, 0xF8, 0x64, 0x2026, 0x201D, 0x20, 0x2014, 0x20, 0x20EF5 };
	std::string str = "";
	for(auto const& ch : chars)
		str += utf8::to_s(ch);
	OAK_ASSERT_EQ(str, "“Æblegrød…” — 𠻵");
}

static std::string sanitize (std::string str)
{
	str.erase(utf8::remove_malformed(str.begin(), str.end()), str.end());
	return str;
}

void test_sanitize ()
{
	OAK_ASSERT_EQ(sanitize("Æblegrød"),           "Æblegrød");
	OAK_ASSERT_EQ(sanitize("Æb\xFFlegrød"),       "Æblegrød");
	OAK_ASSERT_EQ(sanitize("Æb\xC0legrød"),       "Æblegrød");
	OAK_ASSERT_EQ(sanitize("Æb\xC0\xFElegrød"),   "Æblegrød");
	OAK_ASSERT_EQ(sanitize("Æb\xFE\xC0legrød"),   "Æblegrød");
	OAK_ASSERT_EQ(sanitize("Æblegrød\xFE"),       "Æblegrød");

	OAK_ASSERT_EQ(sanitize("x\xE2\x99\xA5y"),     "x\xE2\x99\xA5y");
	OAK_ASSERT_EQ(sanitize("x\xE2\x99y"),         "xy");
	OAK_ASSERT_EQ(sanitize("x\xE2y"),             "xy");
	OAK_ASSERT_EQ(sanitize("x\x99\xA5y"),         "xy");
	OAK_ASSERT_EQ(sanitize("x\xA5y"),             "xy");

	OAK_ASSERT_EQ(sanitize("\xE2\x99\xA5"),       "\xE2\x99\xA5");
	OAK_ASSERT_EQ(sanitize("\xE2\x99"),           "");
	OAK_ASSERT_EQ(sanitize("\xE2"),               "");
	OAK_ASSERT_EQ(sanitize("\x99\xA5"),           "");
	OAK_ASSERT_EQ(sanitize("\xA5"),               "");

	OAK_ASSERT_EQ(sanitize("x\xF0\xA0\xBB\xB5y"), "x\xF0\xA0\xBB\xB5y");
	OAK_ASSERT_EQ(sanitize("x\xF0\xA0\xBBy"),     "xy");
	OAK_ASSERT_EQ(sanitize("x\xF0\xA0y"),         "xy");
	OAK_ASSERT_EQ(sanitize("x\xF0y"),             "xy");

	OAK_ASSERT_EQ(sanitize("\xF0\xA0\xBB\xB5"),   "\xF0\xA0\xBB\xB5");
	OAK_ASSERT_EQ(sanitize("\xF0\xA0\xBB"),       "");
	OAK_ASSERT_EQ(sanitize("\xF0\xA0"),           "");
	OAK_ASSERT_EQ(sanitize("\xF0"),               "");
}
