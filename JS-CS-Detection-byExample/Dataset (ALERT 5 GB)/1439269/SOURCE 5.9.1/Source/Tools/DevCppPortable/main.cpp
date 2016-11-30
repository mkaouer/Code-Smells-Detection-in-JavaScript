#include <windows.h>
#include <Shlwapi.h>
#include <string>
using std::wstring;

// Tried to use wmain, but GCC/MinGW doesn't define it because it's MS specific
// So, I'm sticking to the Windows API for now

int main() {
	// Fill an argv array and argc similar to the standard ones
	int ArgumentCount = 0;
	wchar_t** ArgumentInput = CommandLineToArgvW(GetCommandLineW(),&ArgumentCount);
	
	// Then build our selection to pass to devcpp.exe
	wstring ArgumentsToDev = L"-c .\\config ";
	for(int i = 1;i < ArgumentCount;i++) {
		ArgumentsToDev += '\"';
		ArgumentsToDev += ArgumentInput[i];
		ArgumentsToDev += '\"';
		if(i != ArgumentCount - 1) {
			ArgumentsToDev += ' ';
		}
	}
	
	// Free the strings pointed to by argv
	LocalFree(ArgumentInput);
	
	// Run "devcpp.exe" from the current directory and NOT from the directory 
	// from which files are dragged onto us for example
	wchar_t CurrentDirectory[32768]; // NTFS max length
	GetModuleFileNameW(NULL,CurrentDirectory,32768); // get full file path including filename to devcppPortable.exe
	PathRemoveFileSpecW(CurrentDirectory); // extract filename

	// Attempt to execute
	int Result = (INT_PTR)ShellExecuteW(
		NULL, // no parent window
		L"open", // open the file
		L"devcpp.exe", // the file to open
		ArgumentsToDev.c_str(), // extra parameters to pass
		CurrentDirectory, // use the current directory
		SW_SHOWNORMAL // activate and display window
	);
	if(Result <= 32) {
		switch(Result) {
			case ERROR_FILE_NOT_FOUND: {
				MessageBoxW(NULL,L"devcpp.exe",L"File not found",MB_OK);
				break;
			}
			default: {
				MessageBoxW(NULL,L"An unspecified error has occured!",L"Error",MB_OK);
				break;
			}
		}
	}
	return 0;
}
