#NOTE: Please remove any commented lines to tidy up prior to releasing the package, including this one

$packageName = 'rootbeer' # arbitrary name for the package, used in messages
$url = 'http://rbcompiler.com/dist/Rootbeer-latest.jar' # download url
$url64 = $url # 64bit URL here or just use the same as $url

try { 
  $binRoot = "$env:systemdrive\"
  if($env:chocolatey_bin_root -ne $null){$binRoot = join-path $env:systemdrive $env:chocolatey_bin_root}
  $installDir = Join-Path $binRoot "Program Files"
  $installDir = Join-Path $installDir "Rootbeer"
  
  if (![System.IO.Directory]::Exists($installDir)) {[System.IO.Directory]::CreateDirectory($installDir)}
  
  $file = Join-Path $installDir "Rootbeer.jar"
  Get-ChocolateyWebFile "$packageName" "$file" "http://rbcompiler.com/dist/Rootbeer-latest.jar" "http://rbcompiler.com/dist/Rootbeer-latest.jar"
  
  $file = Join-Path $installDir "Rootbeer.bat"
  Get-ChocolateyWebFile "$packageName" "$file" "http://rbcompiler.com/dist/Rootbeer.bat" "http://rbcompiler.com/dist/Rootbeer.bat"
  
  Install-ChocolateyPath "$installDir"
  
  Write-ChocolateySuccess "$packageName"
} catch {
  Write-ChocolateyFailure "$packageName" "$($_.Exception.Message)"
  throw 
}