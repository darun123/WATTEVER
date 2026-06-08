# setup-maven.ps1
# Downloads Apache Maven 3.9.9 and adds it to the current session PATH

$mavenVersion = "3.9.9"
$mavenDir = "C:\maven"
$mavenZip = "C:\tmp\maven.zip"
$mavenUrl = "https://dlcdn.apache.org/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"

Write-Host "📦 Downloading Apache Maven $mavenVersion..." -ForegroundColor Cyan

New-Item -ItemType Directory -Force -Path "C:\tmp" | Out-Null
New-Item -ItemType Directory -Force -Path $mavenDir | Out-Null

Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip -UseBasicParsing

Write-Host "📂 Extracting Maven..." -ForegroundColor Cyan
Expand-Archive -Path $mavenZip -DestinationPath "C:\tmp\maven-extracted" -Force

# Move contents to C:\maven
$extractedFolder = Get-ChildItem "C:\tmp\maven-extracted" | Select-Object -First 1
Copy-Item "$($extractedFolder.FullName)\*" $mavenDir -Recurse -Force

Write-Host "🔧 Adding Maven to PATH..." -ForegroundColor Cyan

# Add to system PATH permanently
$currentPath = [System.Environment]::GetEnvironmentVariable("PATH", "Machine")
if ($currentPath -notlike "*$mavenDir\bin*") {
    [System.Environment]::SetEnvironmentVariable("PATH", "$currentPath;$mavenDir\bin", "Machine")
    Write-Host "  Added $mavenDir\bin to system PATH" -ForegroundColor Green
} else {
    Write-Host "  Maven already in PATH" -ForegroundColor Yellow
}

# Also add to current session
$env:PATH = "$env:PATH;$mavenDir\bin"

Write-Host ""
Write-Host "✅ Maven installation complete!" -ForegroundColor Green
Write-Host ""
& "C:\maven\bin\mvn.cmd" --version

# Cleanup
Remove-Item $mavenZip -Force -ErrorAction SilentlyContinue
Remove-Item "C:\tmp\maven-extracted" -Recurse -Force -ErrorAction SilentlyContinue
