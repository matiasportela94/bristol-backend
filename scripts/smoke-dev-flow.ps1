param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$AdminEmail = "admin@bristol.com",
    [string]$AdminPassword = "Admin123!",
    [string]$UserEmail = "demo.user@bristol.com",
    [string]$UserPassword = "User123!",
    [string]$ProductId = "44444444-4444-4444-4444-444444444444",
    [string]$ProductVariantId = "55555555-5555-5555-5555-555555555555",
    [string]$DeliveryZoneId = "11111111-1111-1111-1111-111111111111",
    [string]$DistributorId = "77777777-7777-7777-7777-777777777777"
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
        $params.Body = ($Body | ConvertTo-Json -Depth 8)
    }

    Invoke-RestMethod @params
}

function Get-BearerHeaders {
    param([Parameter(Mandatory = $true)][string]$Token)
    @{ Authorization = "Bearer $Token" }
}

Write-Host "1. Checking API health..."
$health = Invoke-Api -Method "GET" -Url "$BaseUrl/api/health"
if ($health.status -ne "UP") {
    throw "API health check failed."
}

Write-Host "2. Logging in as admin..."
$adminAuth = Invoke-Api -Method "POST" -Url "$BaseUrl/api/auth/login" -Body @{
    email = $AdminEmail
    password = $AdminPassword
}
$adminHeaders = Get-BearerHeaders -Token $adminAuth.token

Write-Host "3. Verifying demo catalog data..."
$product = Invoke-Api -Method "GET" -Url "$BaseUrl/api/products/$ProductId"
if ($product.id -ne $ProductId) {
    throw "Demo product not found: $ProductId"
}

$variant = Invoke-Api -Method "GET" -Url "$BaseUrl/api/product-variants/$ProductVariantId"
if ($variant.id -ne $ProductVariantId) {
    throw "Demo product variant not found: $ProductVariantId"
}

Write-Host "4. Verifying delivery zones and distributor..."
$deliveryZone = Invoke-Api -Method "GET" -Url "$BaseUrl/api/delivery-zones/$DeliveryZoneId" -Headers $adminHeaders
if ($deliveryZone.id -ne $DeliveryZoneId) {
    throw "Demo delivery zone not found: $DeliveryZoneId"
}

$distributor = Invoke-Api -Method "GET" -Url "$BaseUrl/api/distributors/$DistributorId" -Headers $adminHeaders
if ($distributor.id -ne $DistributorId) {
    throw "Demo distributor not found: $DistributorId"
}

Write-Host "5. Logging in as demo user..."
$userAuth = Invoke-Api -Method "POST" -Url "$BaseUrl/api/auth/login" -Body @{
    email = $UserEmail
    password = $UserPassword
}
$userHeaders = Get-BearerHeaders -Token $userAuth.token

Write-Host "6. Creating order..."
$order = Invoke-Api -Method "POST" -Url "$BaseUrl/api/orders" -Headers $userHeaders -Body @{
    userId = $userAuth.userId
    items = @(
        @{
            productId = $ProductId
            productVariantId = $ProductVariantId
            quantity = 1
            itemDiscountCouponCode = $null
        }
    )
    shippingAddress = @{
        addressLine1 = "Calle Demo 123"
        addressLine2 = "Piso 1"
        city = "La Plata"
        province = "Buenos Aires"
        postalCode = "1900"
        deliveryZoneId = $DeliveryZoneId
    }
    shippingCost = 1200
    orderDiscountCouponCode = $null
    shippingDiscountCouponCode = $null
    notes = "Smoke test order"
}

if ($order.status -ne "PENDING_PAYMENT") {
    throw "Unexpected initial order status: $($order.status)"
}

Write-Host "7. Marking order as PAID..."
$paidOrder = Invoke-Api -Method "PUT" -Url "$BaseUrl/api/orders/$($order.id)/status" -Headers $adminHeaders -Body @{
    status = "PAID"
}
if ($paidOrder.status -ne "PAID") {
    throw "Order did not transition to PAID. Current status: $($paidOrder.status)"
}

Write-Host "8. Assigning distributor..."
$assignedOrder = Invoke-Api -Method "PUT" -Url "$BaseUrl/api/orders/$($order.id)/assign-distributor" -Headers $adminHeaders -Body @{
    distributorId = $DistributorId
}

Write-Host ""
Write-Host "Smoke test completed successfully." -ForegroundColor Green
Write-Host "Order ID: $($assignedOrder.id)"
Write-Host "Final status: $($assignedOrder.status)"
