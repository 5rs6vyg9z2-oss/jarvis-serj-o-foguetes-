$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $projectRoot '.env'

if (-not (Test-Path -LiteralPath $envFile)) {
    throw 'Arquivo .env nao encontrado na raiz do projeto.'
}

foreach ($line in Get-Content -LiteralPath $envFile) {
    if ($line -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*?)\s*$') {
        $name = $matches[1]
        $value = $matches[2]
        if ($value.Length -ge 2 -and
            (($value.StartsWith('"') -and $value.EndsWith('"')) -or
             ($value.StartsWith("'") -and $value.EndsWith("'")))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        [Environment]::SetEnvironmentVariable($name, $value, 'Process')
    }
}

foreach ($name in @('POSTGRES_DB', 'POSTGRES_USER', 'POSTGRES_PASSWORD')) {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($name, 'Process'))) {
        throw "A variavel $name precisa estar configurada no .env."
    }
}

if ([string]::IsNullOrWhiteSpace($env:GROQ_API_KEY)) {
    throw 'GROQ_API_KEY nao esta disponivel neste terminal. Configure-a nas variaveis de ambiente do Windows.'
}

$env:DB_URL = "jdbc:postgresql://localhost:5432/$env:POSTGRES_DB"
$env:DB_USER = $env:POSTGRES_USER
$env:DB_PASSWORD = $env:POSTGRES_PASSWORD
$env:SERVER_PORT = '8081'

Set-Location -LiteralPath $projectRoot
Write-Host 'Iniciando o Jarvis pelo Maven em http://localhost:8081'
Write-Host 'O PostgreSQL deve estar ativo pelo Docker Compose. Pressione Ctrl+C para encerrar.'

& mvn spring-boot:run
exit $LASTEXITCODE
