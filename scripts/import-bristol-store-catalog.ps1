param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$AdminEmail = "admin@bristol.com",
    [string]$AdminPassword = "Admin123!",
    [string]$CatalogPath = "$PSScriptRoot\\bristol-store-catalog.json"
)

$ErrorActionPreference = "Stop"

function Invoke-Api {
    param(
        [Parameter(Mandatory = $true)][string]$Method,
        [Parameter(Mandatory = $true)][string]$Url,
        [hashtable]$Headers,
        [object]$Body
    )

    $params = @{
        Method = $Method
        Uri = $Url
    }

    if ($Headers) {
        $params.Headers = $Headers
    }

    if ($null -ne $Body) {
        $params.ContentType = "application/json"
        $params.Body = ($Body | ConvertTo-Json -Depth 10)
    }

    Invoke-RestMethod @params
}

function Get-BearerHeaders {
    param([Parameter(Mandatory = $true)][string]$Token)
    @{ Authorization = "Bearer $Token" }
}

function Get-ExistingProductByName {
    param(
        [Parameter(Mandatory = $true)][array]$Products,
        [Parameter(Mandatory = $true)][string]$Name
    )

    $Products | Where-Object { $_.name -eq $Name } | Select-Object -First 1
}

function Get-ExistingVariant {
    param(
        [Parameter(Mandatory = $true)][array]$Variants,
        [Parameter(Mandatory = $true)][string]$Sku,
        [Parameter(Mandatory = $true)][string]$Size
    )

    $bySku = $Variants | Where-Object { $_.sku -eq $Sku } | Select-Object -First 1
    if ($null -ne $bySku) {
        return $bySku
    }

    $Variants | Where-Object { $_.size -eq $Size } | Select-Object -First 1
}

if (-not (Test-Path $CatalogPath)) {
    throw "Catalog file not found: $CatalogPath"
}

Write-Host "1. Logging in as admin..."
$auth = Invoke-Api -Method "POST" -Url "$BaseUrl/api/auth/login" -Body @{
    email = $AdminEmail
    password = $AdminPassword
}
$headers = Get-BearerHeaders -Token $auth.token

Write-Host "2. Loading catalog snapshot..."
$catalog = Get-Content $CatalogPath -Raw | ConvertFrom-Json

Write-Host "3. Fetching existing products..."
$existingProducts = @(Invoke-Api -Method "GET" -Url "$BaseUrl/api/products")

$createdProducts = 0
$updatedProducts = 0
$createdVariants = 0
$updatedVariants = 0

foreach ($catalogProduct in $catalog) {
    $createProductBody = @{
        name = $catalogProduct.name
        description = $catalogProduct.description
        category = $catalogProduct.category
        subcategory = $catalogProduct.subcategory
        beerType = $catalogProduct.beerType
        price = [decimal]$catalogProduct.price
        stockQuantity = [int]$catalogProduct.stockQuantity
        lowStockThreshold = [int]$catalogProduct.lowStockThreshold
    }

    $updateProductBody = @{
        name = $catalogProduct.name
        description = $catalogProduct.description
        category = $catalogProduct.category
        subcategory = $catalogProduct.subcategory
        beerType = $catalogProduct.beerType
        price = [decimal]$catalogProduct.price
    }

    $existingProduct = Get-ExistingProductByName -Products $existingProducts -Name $catalogProduct.name

    if ($null -eq $existingProduct) {
        Write-Host "Creating product: $($catalogProduct.name)"
        $product = Invoke-Api -Method "POST" -Url "$BaseUrl/api/products" -Headers $headers -Body $createProductBody
        $createdProducts++
        $existingProducts += $product
    }
    else {
        Write-Host "Reusing existing product: $($catalogProduct.name)"
        $product = $existingProduct
        $updatedProducts++
    }

    $productStock = [int]$catalogProduct.stockQuantity
    if (@($catalogProduct.variants).Count -gt 0) {
        $productStock = 0
        foreach ($variantSnapshot in $catalogProduct.variants) {
            $productStock += [int]$variantSnapshot.stockQuantity
        }
    }

    Invoke-Api -Method "PUT" -Url "$BaseUrl/api/products/$($product.id)/stock" -Headers $headers -Body @{
        stockQuantity = $productStock
    } | Out-Null

    if (@($catalogProduct.variants).Count -eq 0) {
        continue
    }

    $existingVariants = @(Invoke-Api -Method "GET" -Url "$BaseUrl/api/product-variants/product/$($product.id)" -Headers $headers)

    foreach ($variantSnapshot in $catalogProduct.variants) {
        $variantBody = @{
            productId = $product.id
            sku = $variantSnapshot.sku
            size = $variantSnapshot.size
            color = $variantSnapshot.color
            additionalPrice = [decimal]$variantSnapshot.additionalPrice
            stockQuantity = [int]$variantSnapshot.stockQuantity
            imageUrl = $null
        }

        $existingVariant = Get-ExistingVariant `
            -Variants $existingVariants `
            -Sku $variantSnapshot.sku `
            -Size $variantSnapshot.size

        if ($null -eq $existingVariant) {
            Write-Host "  Creating variant: $($catalogProduct.name) / $($variantSnapshot.size)"
            $createdVariant = Invoke-Api -Method "POST" -Url "$BaseUrl/api/product-variants" -Headers $headers -Body $variantBody
            $createdVariants++
            $existingVariants += $createdVariant
        }
        else {
            Write-Host "  Updating variant: $($catalogProduct.name) / $($variantSnapshot.size)"
            Invoke-Api -Method "PUT" -Url "$BaseUrl/api/product-variants/$($existingVariant.id)" -Headers $headers -Body $variantBody | Out-Null
            $updatedVariants++
        }
    }
}

Write-Host ""
Write-Host "Import completed." -ForegroundColor Green
Write-Host "Products created: $createdProducts"
Write-Host "Products updated: $updatedProducts"
Write-Host "Variants created: $createdVariants"
Write-Host "Variants updated: $updatedVariants"

