# Update Android/Java Dependencies Script
# This script updates deprecated and vulnerable dependencies in build.gradle files

Write-Host "Starting Android/Java dependency updates..." -ForegroundColor Green

# Define the mapping of deprecated Android/Java dependencies to their updated versions
$gradleDependencyUpdates = @{
    # Android Gradle Plugin updates (major versions that might break builds, so we'll be conservative)
    "classpath 'com.android.tools.build:gradle:1.2.3'" = "classpath 'com.android.tools.build:gradle:7.4.2'"
    "classpath 'com.android.tools.build:gradle:1.3.0'" = "classpath 'com.android.tools.build:gradle:7.4.2'"
    "classpath 'com.android.tools.build:gradle:1.5.0'" = "classpath 'com.android.tools.build:gradle:7.4.2'"
    
    # Bintray release plugin updates
    "classpath 'com.novoda:bintray-release:0.3.4'" = "classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1'"
    "classpath 'com.novoda:bintray-release:0.4.0'" = "classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1'"
    
    # Old Mockito versions
    "androidTestCompile 'org.mockito:mockito-core:1.9.5'" = "androidTestImplementation 'org.mockito:mockito-core:5.7.0'"
    "androidTestCompile 'org.mockito:mockito-core:1.10.19'" = "androidTestImplementation 'org.mockito:mockito-core:5.7.0'"
    "testCompile 'org.mockito:mockito-core:1.9.5'" = "testImplementation 'org.mockito:mockito-core:5.7.0'"
    "testCompile 'org.mockito:mockito-core:1.10.19'" = "testImplementation 'org.mockito:mockito-core:5.7.0'"
    
    # DexMaker updates  
    "androidTestCompile 'com.google.dexmaker:dexmaker:1.2'" = "androidTestImplementation 'com.linkedin.dexmaker:dexmaker:2.28.3'"
    "androidTestCompile 'com.google.dexmaker:dexmaker-mockito:1.2'" = "androidTestImplementation 'com.linkedin.dexmaker:dexmaker-mockito:2.28.3'"
    
    # JUnit updates
    "androidTestCompile 'junit:junit:4.11'" = "androidTestImplementation 'junit:junit:4.13.2'"
    "androidTestCompile 'junit:junit:4.12'" = "androidTestImplementation 'junit:junit:4.13.2'"
    "testCompile 'junit:junit:4.11'" = "testImplementation 'junit:junit:4.13.2'"
    "testCompile 'junit:junit:4.12'" = "testImplementation 'junit:junit:4.13.2'"
    
    # Replace deprecated compile configurations with implementation
    "androidTestCompile" = "androidTestImplementation"
    "testCompile" = "testImplementation"
    "compile" = "implementation"
    "debugCompile" = "debugImplementation"
    "releaseCompile" = "releaseImplementation"
}

# SDK and build tools updates (more conservative approach)
$sdkUpdates = @{
    'compileSdkVersion 23' = 'compileSdkVersion 33'
    'compileSdkVersion 24' = 'compileSdkVersion 33' 
    'compileSdkVersion 25' = 'compileSdkVersion 33'
    'compileSdkVersion 26' = 'compileSdkVersion 33'
    'compileSdkVersion 27' = 'compileSdkVersion 33'
    'compileSdkVersion 28' = 'compileSdkVersion 33'
    
    'targetSdkVersion 23' = 'targetSdkVersion 33'
    'targetSdkVersion 24' = 'targetSdkVersion 33'
    'targetSdkVersion 25' = 'targetSdkVersion 33'
    'targetSdkVersion 26' = 'targetSdkVersion 33'
    'targetSdkVersion 27' = 'targetSdkVersion 33'
    'targetSdkVersion 28' = 'targetSdkVersion 33'
    
    'buildToolsVersion "23.0.1"' = 'buildToolsVersion "33.0.0"'
    'buildToolsVersion "23.0.2"' = 'buildToolsVersion "33.0.0"'
    'buildToolsVersion "23.0.3"' = 'buildToolsVersion "33.0.0"'
    'buildToolsVersion "24.0.0"' = 'buildToolsVersion "33.0.0"'
    'buildToolsVersion "24.0.1"' = 'buildToolsVersion "33.0.0"'
    'buildToolsVersion "25.0.0"' = 'buildToolsVersion "33.0.0"'
    'buildToolsVersion "25.0.1"' = 'buildToolsVersion "33.0.0"'
    'buildToolsVersion "25.0.2"' = 'buildToolsVersion "33.0.0"'
    'buildToolsVersion "25.0.3"' = 'buildToolsVersion "33.0.0"'
}

# Repository updates
$repositoryUpdates = @{
    'jcenter()' = 'mavenCentral() // jcenter() is deprecated'
}

# Find all build.gradle files
$gradleFiles = Get-ChildItem "JS-CS-Detection-byExample" -Recurse -Filter "build.gradle" -Force

Write-Host "Found $($gradleFiles.Count) build.gradle files to process" -ForegroundColor Yellow

$filesModified = 0
$totalReplacements = 0

foreach ($file in $gradleFiles) {
    Write-Host "Processing: $($file.FullName)" -ForegroundColor Cyan
    
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    $originalContent = $content
    $fileReplacements = 0
    
    # Apply dependency updates
    foreach ($oldDep in $gradleDependencyUpdates.Keys) {
        $newDep = $gradleDependencyUpdates[$oldDep]
        if ($content -match [regex]::Escape($oldDep)) {
            $content = $content -replace [regex]::Escape($oldDep), $newDep
            $fileReplacements++
            $totalReplacements++
            Write-Host "  Replaced: $oldDep -> $newDep" -ForegroundColor Green
        }
    }
    
    # Apply SDK and build tools updates
    foreach ($oldSdk in $sdkUpdates.Keys) {
        $newSdk = $sdkUpdates[$oldSdk]
        if ($content -match [regex]::Escape($oldSdk)) {
            $content = $content -replace [regex]::Escape($oldSdk), $newSdk
            $fileReplacements++
            $totalReplacements++
            Write-Host "  Replaced: $oldSdk -> $newSdk" -ForegroundColor Green
        }
    }
    
    # Apply repository updates
    foreach ($oldRepo in $repositoryUpdates.Keys) {
        $newRepo = $repositoryUpdates[$oldRepo]
        if ($content -match [regex]::Escape($oldRepo)) {
            $content = $content -replace [regex]::Escape($oldRepo), $newRepo
            $fileReplacements++
            $totalReplacements++
            Write-Host "  Replaced: $oldRepo -> $newRepo" -ForegroundColor Green
        }
    }
    
    # Save the updated content if changes were made
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        $filesModified++
        Write-Host "  Updated file with $fileReplacements replacements" -ForegroundColor Green
    } else {
        Write-Host "  No updates needed" -ForegroundColor Gray
    }
}

Write-Host "`nAndroid/Java dependency update completed!" -ForegroundColor Green
Write-Host "Files modified: $filesModified" -ForegroundColor Yellow  
Write-Host "Total replacements: $totalReplacements" -ForegroundColor Yellow

# Create a summary report
$reportContent = @"
Android/Java Dependency Update Report
Generated: $(Get-Date)

Files processed: $($gradleFiles.Count)
Files modified: $filesModified
Total replacements made: $totalReplacements

Key updates made:
- Updated Android Gradle Plugin to version 7.4.2
- Updated deprecated Bintray plugin to Maven alternative
- Updated Mockito from 1.x to 5.7.0
- Updated DexMaker to LinkedIn's maintained version
- Updated JUnit to 4.13.2
- Replaced deprecated compile configurations with implementation
- Updated SDK versions to API 33
- Updated build tools to 33.0.0
- Replaced deprecated jcenter() with mavenCentral()

Dependency Updates Applied:
$(($gradleDependencyUpdates.Keys + $sdkUpdates.Keys + $repositoryUpdates.Keys) | ForEach-Object { "  $_ -> $($gradleDependencyUpdates[$_] + $sdkUpdates[$_] + $repositoryUpdates[$_])" } | Out-String)

IMPORTANT NOTES:
- These updates represent major version changes that may affect build compatibility
- Test builds carefully after applying these updates
- Some apps may need additional configuration changes for the newer Android Gradle Plugin
- Consider updating minSdkVersion if targeting newer Android features
"@

Set-Content -Path "Android-dependency-update-report.txt" -Value $reportContent -Encoding UTF8
Write-Host "`nReport saved to: Android-dependency-update-report.txt" -ForegroundColor Green
Write-Host "`nIMPORTANT: These updates include major version changes." -ForegroundColor Yellow
Write-Host "Test builds carefully after applying updates!" -ForegroundColor Yellow