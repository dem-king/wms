param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $RuleArgs
)

$ErrorActionPreference = "Stop"

function Get-RepoRoot {
    $scriptDir = Split-Path -Parent $PSCommandPath
    return (Resolve-Path (Join-Path $scriptDir "..")).Path
}

function Invoke-RuleCheckerWithBash {
    param(
        [string] $RepoRoot,
        [string[]] $Args
    )

    $bash = Get-Command bash -ErrorAction SilentlyContinue
    if ($null -eq $bash) {
        return $false
    }

    Push-Location $RepoRoot
    try {
        & $bash.Source "scripts/check-rules.sh" @Args
        exit $LASTEXITCODE
    } finally {
        Pop-Location
    }
}

function Invoke-RuleCheckerWithWsl {
    param(
        [string] $RepoRoot,
        [string[]] $Args
    )

    $wsl = Get-Command wsl -ErrorAction SilentlyContinue
    if ($null -eq $wsl) {
        return $false
    }

    $distributions = & $wsl.Source "--list" "--quiet" 2>$null
    if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace(($distributions -join ""))) {
        return $false
    }

    $linuxRepoRoot = & $wsl.Source "wslpath" "-a" $RepoRoot 2>$null
    if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($linuxRepoRoot)) {
        return $false
    }

    $quotedArgs = @()
    foreach ($arg in $Args) {
        $quotedArgs += "'" + ($arg -replace "'", "'\''") + "'"
    }
    $command = "cd '" + ($linuxRepoRoot.Trim() -replace "'", "'\''") + "' && bash scripts/check-rules.sh " + ($quotedArgs -join " ")

    & $wsl.Source "bash" "-lc" $command
    exit $LASTEXITCODE
}

$repoRoot = Get-RepoRoot

if (Invoke-RuleCheckerWithBash -RepoRoot $repoRoot -Args $RuleArgs) {
    exit $LASTEXITCODE
}

if (Invoke-RuleCheckerWithWsl -RepoRoot $repoRoot -Args $RuleArgs) {
    exit $LASTEXITCODE
}

[Console]::Error.WriteLine("bash or a usable WSL distribution was not found, so scripts/check-rules.sh cannot run locally. Install Git for Windows with Git Bash, or enable WSL with a Linux distribution. CI runs the same checker on Ubuntu.")
exit 2
