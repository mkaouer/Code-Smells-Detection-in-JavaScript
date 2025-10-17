# Update JavaScript Dependencies Script
# This script updates deprecated and vulnerable dependencies in package.json files

Write-Host "Starting JavaScript dependency updates..." -ForegroundColor Green

# Define the mapping of deprecated/vulnerable dependencies to their updated versions
$dependencyUpdates = @{
    # Fix wildcard dependencies with specific stable versions
    '"tar": "*"' = '"tar": "6.2.0"'
    '"debug": "*"' = '"debug": "4.3.4"'
    '"should": "*"' = '"should": "13.2.3"'
    '"fs-extra": "*"' = '"fs-extra": "11.2.0"'
    
    # Update extremely old versions to modern stable versions
    '"jade": "0.26.3"' = '"pug": "3.0.2"'  # jade was renamed to pug
    '"coffee-script": "1.2"' = '"coffeescript": "2.7.0"'  # coffee-script was renamed
    '"commander": "0.6.1"' = '"commander": "11.1.0"'
    '"commander": "2.0.0"' = '"commander": "11.1.0"'
    '"growl": "1.6.x"' = '"growl": "1.10.5"'
    '"growl": "1.7.x"' = '"growl": "1.10.5"'
    '"mkdirp": "0.3.3"' = '"mkdirp": "3.0.1"'
    '"mkdirp": "0.3.5"' = '"mkdirp": "3.0.1"'
    '"diff": "1.0.2"' = '"diff": "5.1.0"'
    '"diff": "1.0.7"' = '"diff": "5.1.0"'
    '"ms": "0.3.0"' = '"ms": "2.1.3"'
    '"mocha": "1.18.2"' = '"mocha": "10.2.0"'
    '"mocha": "1.7.4"' = '"mocha": "10.2.0"'
    '"glob": "3.2.3"' = '"glob": "10.3.10"'
    '"http-proxy": "1.1.4"' = '"http-proxy": "1.18.1"'
    '"http-auth": "2.1.8"' = '"http-auth": "4.2.0"'
    
    # Update dev dependencies patterns
    '"should": ">= 2.0.x"' = '"should": "13.2.3"'
    '"should": ">= 0.0.1"' = '"should": "13.2.3"'
    '"mocha": "~1.6"' = '"mocha": "10.2.0"'
    
    # Update minimatch and related dependencies
    '"minimatch": "~0.2.11"' = '"minimatch": "9.0.3"'
    '"graceful-fs": "~2.0.0"' = '"graceful-fs": "4.2.11"'
    '"lru-cache": "2"' = '"lru-cache": "10.1.0"'
    '"sigmund": "~1.0.0"' = '"sigmund": "1.0.1"'
    '"inherits": "2"' = '"inherits": "2.0.4"'
}

# Find all package.json files
$packageJsonFiles = Get-ChildItem "JS-CS-Detection-byExample" -Recurse -Filter "package.json" -Force

Write-Host "Found $($packageJsonFiles.Count) package.json files to process" -ForegroundColor Yellow

$filesModified = 0
$totalReplacements = 0

foreach ($file in $packageJsonFiles) {
    Write-Host "Processing: $($file.FullName)" -ForegroundColor Cyan
    
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    $originalContent = $content
    $fileReplacements = 0
    
    # Apply all dependency updates
    foreach ($oldDep in $dependencyUpdates.Keys) {
        $newDep = $dependencyUpdates[$oldDep]
        if ($content -match [regex]::Escape($oldDep)) {
            $content = $content -replace [regex]::Escape($oldDep), $newDep
            $fileReplacements++
            $totalReplacements++
            Write-Host "  Replaced: $oldDep -> $newDep" -ForegroundColor Green
        }
    }
    
    # Fix common engine specifications that are too old
    $content = $content -replace '"node": ">= 0\.4\.x"', '"node": ">= 14.0.0"'
    $content = $content -replace '"node": ">= 0\.6\.x"', '"node": ">= 14.0.0"'
    $content = $content -replace '"node": "\*"', '"node": ">= 14.0.0"'
    
    # Save the updated content if changes were made
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        $filesModified++
        Write-Host "  Updated file with $fileReplacements replacements" -ForegroundColor Green
    } else {
        Write-Host "  No updates needed" -ForegroundColor Gray
    }
}

Write-Host "`nJavaScript dependency update completed!" -ForegroundColor Green
Write-Host "Files modified: $filesModified" -ForegroundColor Yellow  
Write-Host "Total replacements: $totalReplacements" -ForegroundColor Yellow

# Create a summary report
$reportContent = @"
JavaScript Dependency Update Report
Generated: $(Get-Date)

Files processed: $($packageJsonFiles.Count)
Files modified: $filesModified
Total replacements made: $totalReplacements

Key updates made:
- Replaced wildcard dependencies (*) with specific stable versions
- Updated jade to pug (package was renamed)
- Updated coffee-script to coffeescript (package was renamed)  
- Updated extremely old versions to modern stable versions
- Fixed vulnerable dependency versions
- Updated Node.js engine requirements to modern versions

Dependency Updates Applied:
$($dependencyUpdates.Keys | ForEach-Object { "  $_ -> $($dependencyUpdates[$_])" } | Out-String)
"@

Set-Content -Path "JS-dependency-update-report.txt" -Value $reportContent -Encoding UTF8
Write-Host "`nReport saved to: JS-dependency-update-report.txt" -ForegroundColor Green