[CmdletBinding()]
Param(
    $PluginPath = $env:PluginPath,
    $SonarQubeInstallPath = $env:SonarQubeInstallPath
)
 
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function RemoveOldPluginVersions($plugin) {
    $pluginName = $plugin.Name.SubString(0, $plugin.Name.LastIndexOf("-"))

    $Plugins = Get-ChildItem $PluginTarget | where {$_.Name.StartsWith($pluginName)}
    foreach ($oldPlugin in $Plugins) {
        "Removing plugin " + $oldPlugin
        Remove-Item $oldPlugin.FullName
    }
}
function InstallPlugin($plugin, $PluginTarget) {
    "Copying new plugin $plugin.FullName"
    Copy-Item $plugin.FullName -Destination $PluginTarget
}

if ( [string]::IsNullOrWhiteSpace($SonarQubeInstallPath)){
    "SonarQubeInstallPath cannot be empty"
    exit 1
}

if ( [string]::IsNullOrWhiteSpace($PluginPath)){
    "PluginPath cannot be empty"
    exit 1
}

if (-Not (Test-Path $SonarQubeInstallPath)) {
    Write-Error "$SonarQubeInstallPath does not exist."
    exit 1
}

$BinPath = Join-Path -Path $SonarQubeInstallPath -ChildPath "bin\windows-x86-64"
$StopSonarQube = Join-Path -Path $BinPath -ChildPath "StopNTService.bat"
$StartSonarQube = Join-Path -Path $BinPath -ChildPath "StartNTService.bat"

if (-Not (Test-Path $BinPath)) {
    Write-Error "$BinPath does not exist."
    exit 1
}

"Stopping SonarQube"
cmd.exe /c $StopSonarQube

$PluginTarget = Join-Path -Path $SonarQubeInstallPath -ChildPath "extensions\plugins" 

$Dir = Get-ChildItem $PluginPath 
$List = $Dir | where {$_.extension -eq ".jar"} 

if (-Not $List) {
    "No plugins (.jar) found in $PluginPath"
}
else {
    "Found the following plugins: "
    $List | format-table Name 

    foreach ($plugin in $List ) {
        "Deploying plugin $plugin"
        RemoveOldPluginVersions $plugin
        InstallPlugin  $plugin $PluginTarget
    }
}
"Starting SonarQube"
cmd.exe /c $StartSonarQube