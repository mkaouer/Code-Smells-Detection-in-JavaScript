#NOTE: Please remove any commented lines to tidy up prior to releasing the package, including this one

$packageName = 'cuda' # arbitrary name for the package, used in messages
$installerType = 'msi' #only one of these two: exe or msi
$url = 'http://developer.download.nvidia.com/compute/cuda/5_0/rel-update-1/installers/cuda_5.0.35_winvista_win7_win8_general_32-3.msi' # download url
$url64 = 'http://developer.download.nvidia.com/compute/cuda/5_0/rel-update-1/installers/cuda_5.0.35_winvista_win7_win8_general_64-3.msi' # 64bit URL here or just use the same as $url
$silentArgs = '/quiet' # "/s /S /q /Q /quiet /silent /SILENT /VERYSILENT" # try any of these to get the silent installer #msi is always /quiet
$validExitCodes = @(0) #please insert other valid exit codes here, exit codes for ms http://msdn.microsoft.com/en-us/library/aa368542(VS.85).aspx

# main helpers - these have error handling tucked into them already
# installer, will assert administrative rights
Install-ChocolateyPackage "$packageName" "$installerType" "$silentArgs" "$url" "$url64"  -validExitCodes $validExitCodes