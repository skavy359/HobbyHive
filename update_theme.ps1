$screensDir = "hobbyhive\app\src\main\java\com\example\hobbyhive\ui\screens"

Get-ChildItem -Path $screensDir -Filter "*.kt" | ForEach-Object {
    $content = [System.IO.File]::ReadAllText($_.FullName)
    $original = $content
    
    # Replace onSurfaceVariant first (longer match)
    $content = $content.Replace('MaterialTheme.colorScheme.onSurfaceVariant', 'Charcoal')
    
    # Replace onSurface (but not the ones we already changed)
    $content = $content.Replace('MaterialTheme.colorScheme.onSurface', 'InkBlack')
    # Fix overcorrection: "InkBlack" that was part of "Charcoal" replacement won't happen since we do exact string match
    
    # Replace surface colors  
    $content = $content.Replace('MaterialTheme.colorScheme.surfaceVariant', 'PaperWarm')
    $content = $content.Replace('containerColor = MaterialTheme.colorScheme.surface)', 'containerColor = PaperWhite)')
    $content = $content.Replace('containerColor = MaterialTheme.colorScheme.surface,', 'containerColor = PaperWhite,')
    
    # Replace background
    $content = $content.Replace('MaterialTheme.colorScheme.background', 'PaperCream')

    # Add BorderStroke import if file uses Card but doesn't have the import
    if ($content.Contains("Card(") -and -not $content.Contains("import androidx.compose.foundation.BorderStroke")) {
        $content = $content.Replace(
            "import androidx.compose.foundation.layout.*",
            "import androidx.compose.foundation.BorderStroke`nimport androidx.compose.foundation.layout.*"
        )
    }

    # Replace Color.White with Color.White only where used as button/surface bg
    $content = $content.Replace(
        'containerColor = Color.White.copy(alpha = 0.2f))',
        'containerColor = PaperWhite, contentColor = InkBlack), border = BorderStroke(2.dp, InkBlack)'
    )

    # Add missing theme import for specific files
    if ($_.Name -in @("AddEditHobbyScreen.kt", "CategoryBrowseScreen.kt")) {
        if (-not $content.Contains("import com.example.hobbyhive.ui.theme.*")) {
            $content = $content.Replace(
                "import com.example.hobbyhive.model.HobbyCategory",
                "import com.example.hobbyhive.model.HobbyCategory`nimport com.example.hobbyhive.ui.theme.*"
            )
        }
    }

    # Add border to Card declarations that don't already have them
    $content = $content -replace 'Card\(shape = RoundedCornerShape\((\d+)\.dp\), colors = CardDefaults\.cardColors\(containerColor = PaperWhite\)(?!, border)', 'Card(shape = RoundedCornerShape($1.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack)'
    $content = $content -replace 'Card\(shape = RoundedCornerShape\((\d+)\.dp\), colors = CardDefaults\.cardColors\(containerColor = PaperWhite\), modifier', 'Card(shape = RoundedCornerShape($1.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack), modifier'

    if ($content -ne $original) {
        [System.IO.File]::WriteAllText($_.FullName, $content)
        Write-Host "Updated: $($_.Name)"
    }
}

