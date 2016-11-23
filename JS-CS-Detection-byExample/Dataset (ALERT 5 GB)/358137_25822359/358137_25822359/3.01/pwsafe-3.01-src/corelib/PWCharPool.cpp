/// \file PWCharPool.cpp
//-----------------------------------------------------------------------------

#include "PWCharPool.h"
#include "Util.h"
#include "PWSrand.h"

// Following macro get length of std_*_chars less the trailing \0
// compile time equivalent of strlen()
#define LENGTH(s) (sizeof(s)/sizeof(s[0]) - sizeof(s[0]))

const TCHAR 
CPasswordCharPool::std_lowercase_chars[] = _T("abcdefghijklmnopqrstuvwxyz");
const size_t
CPasswordCharPool::std_lowercase_len = LENGTH(std_lowercase_chars);
const TCHAR CPasswordCharPool::std_uppercase_chars[] = _T("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
const size_t
CPasswordCharPool::std_uppercase_len = LENGTH(std_uppercase_chars);
const TCHAR
CPasswordCharPool::std_digit_chars[] =
_T("0123456789");
const size_t
CPasswordCharPool::std_digit_len = LENGTH(std_digit_chars);
const TCHAR
CPasswordCharPool::std_hexdigit_chars[] = _T("0123456789abcdef");
const size_t
CPasswordCharPool::std_hexdigit_len = LENGTH(std_hexdigit_chars);
const TCHAR
CPasswordCharPool::std_symbol_chars[] = _T("+-=_@#$%^&;:,.<>/~\\[](){}?!|");
const size_t
CPasswordCharPool::std_symbol_len = LENGTH(std_symbol_chars);
const TCHAR
CPasswordCharPool::easyvision_lowercase_chars[] = _T("abcdefghijkmnopqrstuvwxyz");
const size_t
CPasswordCharPool::easyvision_lowercase_len = LENGTH(easyvision_lowercase_chars);
const TCHAR
CPasswordCharPool::easyvision_uppercase_chars[] = _T("ABCDEFGHJKLMNPQRTUVWXY");
const size_t
CPasswordCharPool::easyvision_uppercase_len = LENGTH(easyvision_uppercase_chars);
const TCHAR
CPasswordCharPool::easyvision_digit_chars[] = _T("346789");
const size_t
CPasswordCharPool::easyvision_digit_len = LENGTH(easyvision_digit_chars);
const TCHAR
CPasswordCharPool::easyvision_symbol_chars[] = _T("+-=_@#$%^&<>/~\\?");
const size_t
CPasswordCharPool::easyvision_symbol_len = LENGTH(easyvision_symbol_chars);
const TCHAR
CPasswordCharPool::easyvision_hexdigit_chars[] = _T("0123456789abcdef");
const size_t
CPasswordCharPool::easyvision_hexdigit_len = LENGTH(easyvision_hexdigit_chars);

//-----------------------------------------------------------------------------

CPasswordCharPool::CPasswordCharPool(UINT pwlen,
				     BOOL uselowercase, BOOL useuppercase,
				     BOOL usedigits, BOOL usesymbols, BOOL usehexdigits,
				     BOOL easyvision) :
  m_pwlen(pwlen), m_uselowercase(uselowercase), m_useuppercase(useuppercase),
  m_usedigits(usedigits), m_usesymbols(usesymbols), m_usehexdigits(usehexdigits)
{
  ASSERT(m_pwlen > 0);
  ASSERT(m_uselowercase || m_useuppercase || m_usedigits || m_usesymbols || m_usehexdigits);

  if (easyvision) {
    m_char_arrays[LOWERCASE] = (TCHAR *)easyvision_lowercase_chars;
    m_char_arrays[UPPERCASE] = (TCHAR *)easyvision_uppercase_chars;
    m_char_arrays[DIGIT] = (TCHAR *)easyvision_digit_chars;
    m_char_arrays[SYMBOL] = (TCHAR *)easyvision_symbol_chars;
	m_char_arrays[HEXDIGIT] = (TCHAR *)easyvision_hexdigit_chars;
    m_lengths[LOWERCASE] = uselowercase ? easyvision_lowercase_len : 0;
    m_lengths[UPPERCASE] = useuppercase ? easyvision_uppercase_len : 0;
    m_lengths[DIGIT] = usedigits ? easyvision_digit_len : 0;
    m_lengths[SYMBOL] = usesymbols ? easyvision_symbol_len : 0;
	m_lengths[HEXDIGIT] = usehexdigits ? easyvision_hexdigit_len : 0;
  } else { // !easyvision
    m_char_arrays[LOWERCASE] = (TCHAR *)std_lowercase_chars;
    m_char_arrays[UPPERCASE] = (TCHAR *)std_uppercase_chars;
    m_char_arrays[DIGIT] = (TCHAR *)std_digit_chars;
    m_char_arrays[SYMBOL] = (TCHAR *)std_symbol_chars;
	m_char_arrays[HEXDIGIT] = (TCHAR *)std_hexdigit_chars;
    m_lengths[LOWERCASE] = uselowercase ? std_lowercase_len : 0;
    m_lengths[UPPERCASE] = useuppercase ? std_uppercase_len : 0;
    m_lengths[DIGIT] = usedigits ? std_digit_len : 0;
    m_lengths[SYMBOL] = usesymbols ? std_symbol_len : 0;
	m_lengths[HEXDIGIT] = usehexdigits ? std_hexdigit_len : 0;
  }

  // See GetRandomCharType to understand what this does and why
  m_x[0] = 0;
  m_sumlengths = 0;
  for (int i = 0; i< NUMTYPES; i++) {
    m_x[i+1] = m_x[i] + m_lengths[i];
    m_sumlengths += m_lengths[i];
  }
  ASSERT(m_sumlengths > 0);
}

CPasswordCharPool::CharType CPasswordCharPool::GetRandomCharType(size_t rand) const
{
  /*
   * Following is needed in order to choose a char type with a probability
   * in proportion to its relative size, i.e., if chartype 'A' has 20 characters,
   * and chartype 'B' has 10, then the generated password will have twice as
   * many chars from 'A' than as from 'B'.
   * Think of m_x as the number axis. We treat the chartypes as intervals which
   * are laid out successively along the axis. Each number in m_x[] specifies
   * where one interval ends and the other begins. Choosing a chartype is then
   * reduced to seeing in which interval a random number falls.
   * The nice part is that this works correctly for non-selected chartypes
   * without any special logic.
   */
   int i;
   for (i = 0; i < NUMTYPES; i++) {
     if (rand < m_x[i+1]) {
       break;
     }
   }

   ASSERT(m_lengths[i] > 0 && i < NUMTYPES);
   return CharType(i);
}


TCHAR CPasswordCharPool::GetRandomChar(CPasswordCharPool::CharType t, size_t rand) const
{
  ASSERT(t < NUMTYPES);
  ASSERT(m_lengths[t] > 0);
  rand %= m_lengths[t];

  TCHAR retval = m_char_arrays[t][rand];
  return retval;
}

CMyString
CPasswordCharPool::MakePassword() const
{
  ASSERT(m_pwlen > 0);
  ASSERT(m_uselowercase || m_useuppercase || m_usedigits || m_usesymbols || m_usehexdigits);

  int lowercaseneeded;
  int uppercaseneeded;
  int digitsneeded;
  int symbolsneeded;
  int hexdigitsneeded;

  CMyString password = "";

  bool pwRulesMet;
  CMyString temp;

   do
   {
      TCHAR ch;
      CharType type;

      lowercaseneeded = (m_uselowercase) ? 1 : 0;
      uppercaseneeded = (m_useuppercase) ? 1 : 0;
      digitsneeded = (m_usedigits) ? 1 : 0;
      symbolsneeded = (m_usesymbols) ? 1 : 0;
	  hexdigitsneeded = (m_usehexdigits) ? 1 : 0;

      // If following assertion doesn't hold, we'll never exit the do loop!
      ASSERT(int(m_pwlen) >= lowercaseneeded + uppercaseneeded +
	     digitsneeded + symbolsneeded + hexdigitsneeded);

      temp = _T("");    // empty the password string

      for (UINT x = 0; x < m_pwlen; x++)
      {
	 size_t rand = PWSrand::GetInstance()->RangeRand(m_sumlengths);
	 // The only reason for passing rand as a parameter is to
	 // avoid having to generate two random numbers for each
	 // character. Alternately, we could have had a m_rand
	 // data member. Which solution is uglier is debatable.
	 type = GetRandomCharType(rand);
         ch = GetRandomChar(type, rand);
         temp += ch;
         /*
         **  Decrement the appropriate needed character type count.
         */
         switch (type)
         {
	    case LOWERCASE:
	      lowercaseneeded--;
               break;

            case UPPERCASE:
	      uppercaseneeded--;
               break;

            case DIGIT:
	      digitsneeded--;
               break;

            case SYMBOL:
	      symbolsneeded--;
               break;

			case HEXDIGIT:
				hexdigitsneeded--;
               break;

            default:
	      ASSERT(0); // should never happen!
               break;
         }
      } // for

      /*
       * Make sure we have at least one representative of each required type
       * after the for loop. If not, try again. Arguably, recursion would have
       * been more elegant than a do loop, but this takes less stack...
       */
      pwRulesMet = (lowercaseneeded <= 0 && uppercaseneeded <= 0 &&
		    digitsneeded <= 0 && symbolsneeded <= 0 && hexdigitsneeded <= 0);

      if (pwRulesMet)
      {
         password = temp;
      }
      // Otherwise, do not exit, do not collect $200, try again...
   } while (!pwRulesMet);
   ASSERT(password.GetLength() == int(m_pwlen));
   return password;
}

bool CPasswordCharPool::CheckPassword(const CMyString &pwd, CMyString &error)
{
  const int MinLength = 4;
  int length = pwd.GetLength();
  // check for minimun length
  if (length < MinLength) {
    error = _T("Password is too short");
    return false;
  }

  // check for at least one  uppercase and lowercase and  digit or other
  bool has_uc = false, has_lc = false, has_digit = false, has_other = false;

  for (int i = 0; i < length; i++) {
    TCHAR c = pwd[i];
    if (_istlower(c)) has_lc = true;
    else if (_istupper(c)) has_uc = true;
    else if (_istdigit(c)) has_digit = true;
    else has_other = true;
  }
  
  if (has_uc && has_lc && (has_digit || has_other)) {
    return true;
  } else {
    error = _T("Password should be mixed case, with at least one digit or punctuation character");
    return false;
  }
}
