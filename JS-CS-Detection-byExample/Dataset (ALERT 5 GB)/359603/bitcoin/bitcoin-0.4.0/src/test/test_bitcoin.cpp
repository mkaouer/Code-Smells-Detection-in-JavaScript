#define BOOST_TEST_MODULE Bitcoin Test Suite
#include <boost/test/unit_test.hpp>

#include "../main.h"
#include "../wallet.h"

#include "uint160_tests.cpp"
#include "uint256_tests.cpp"
#include "script_tests.cpp"
#include "transaction_tests.cpp"


CWallet* pwalletMain;

void Shutdown(void* parg)
{
	exit(0);
}
