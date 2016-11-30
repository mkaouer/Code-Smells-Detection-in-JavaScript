#include <fstream>
using std::ifstream;
#include <string>
using std::string;
#include <iostream>
using std::cout;

void ViewFile(string File) {
	cout << "Opening file " << File << "...";
	cout << "\n\n";
	
	ifstream DelphiFile;
	DelphiFile.open(File.c_str());

	// Print list of functions
	string Line;
	bool ImplementationFound = false;
	while(getline(DelphiFile,Line)) {
		
		// Only start printing after implementation
		if(Line.find("implementation") == 0) {
			ImplementationFound = true;
			continue;
		}
		if(!ImplementationFound) {
			continue;
		}
		
		// Only print if it starts with 'procedure'
		if(Line.find("procedure ") == 0) {
			cout << Line.substr(sizeof("procedure")) << "\n";
			
		// Or 'function'
		} else if(Line.find("function ") == 0) {
			cout << Line.substr(sizeof("function")) << "\n";
		}
	}
}

int main(int argc, char** argv) {
	ViewFile("C:\\Program Files (x86)\\Dev-Cpp\\Source\\VCL\\ClassBrowsing\\CppParser.pas");
	return 0;
}
