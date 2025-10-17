# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is a research project for **Code Smells Detection in JavaScript**. The project is structured as a Java Eclipse project that processes and analyzes a large dataset of JavaScript projects to detect code smells. Despite the JavaScript focus, the analysis tools are implemented in Java.

## Repository Structure

- **JS-CS-Detection-byExample/**: Main Java Eclipse project directory
  - `.project`, `.classpath`: Eclipse project configuration files
  - `Dataset (ALERT 5 GB)/`: Large dataset containing JavaScript projects for analysis (numbered directories like 100211, 1092864, etc.)
  - `.settings/`: Eclipse Java development settings

The dataset contains various JavaScript projects (including major projects like ExoPlayer, GoogleTest, etc.) stored in numbered directories, each containing Excel spreadsheets with analysis results and the actual project source code.

## Development Environment

This project is set up as an **Eclipse Java project**:
- Java development environment required
- Eclipse IDE recommended (project configured with `.project` and `.classpath`)
- CDC-1.1%Foundation-1.1 JRE container configured

## Common Commands

### Building the Project
Since this is an Eclipse project:
```powershell
# If using Eclipse IDE - use Eclipse build commands
# For command line compilation (if needed):
javac -cp .classpath src/**/*.java -d bin/
```

### Working with the Dataset
The dataset is large (5GB warning in directory name):
```powershell
# Navigate to specific dataset projects
Get-ChildItem "JS-CS-Detection-byExample\Dataset (ALERT 5 GB)"

# View Excel analysis files
Get-ChildItem "JS-CS-Detection-byExample\Dataset (ALERT 5 GB)\*\*.xlsx"
```

### Git Operations
```powershell
# Standard git operations
git status
git add .
git commit -m "message"
git push origin master
```

## Architecture Notes

### Data Processing Pipeline
- **Input**: JavaScript projects stored in numbered dataset directories
- **Processing**: Java-based analysis tools (source not currently visible in main directory)
- **Output**: Excel files containing code smell detection results

### Dataset Organization
- Each numbered directory (e.g., 100211, 1092864) represents a different JavaScript project or analysis run
- Projects include major open source JavaScript/web projects
- Analysis results are stored as Excel spreadsheets with naming pattern: `{id}-{id}.xlsx`

### Key Dependencies
Based on git history, the project includes Maven dependencies for:
- Apache HttpComponents
- SnakeYAML
- Various web crawling and text processing libraries

## Dataset Considerations

- The dataset is very large (5GB+) and should be handled carefully
- Dataset directories contain both source code and analysis results
- Some projects in the dataset are large (e.g., ExoPlayer, GoogleTest variants)

## Development Workflow

1. **Eclipse Setup**: Import as existing Eclipse project
2. **Dataset Analysis**: Focus on specific numbered directories in the dataset
3. **Result Analysis**: Work with Excel files containing code smell detection results
4. **Code Modification**: Any analysis tool modifications would be in Java source files

## Research Context

This appears to be an academic/research project focused on:
- Code smell detection in JavaScript codebases
- Large-scale analysis of open source JavaScript projects
- Empirical software engineering research

The project maintainer appears to be mkaouer based on GitHub repository structure.