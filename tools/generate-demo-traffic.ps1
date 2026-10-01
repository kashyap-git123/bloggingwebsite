param(
    [string]$DatabaseUrl = "jdbc:mysql://localhost:3307/blogging_elk_demo?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",

    [string]$BaseUrl = "http://localhost:8080",
    [ValidateRange(1, 20)]
    [int]$Count = 3
)

$ErrorActionPreference = "Stop"

if ($DatabaseUrl -notmatch "(?i)^jdbc:mysql://[^/]+:3307/blogging_elk_demo(?:\?|$)") {
    throw "Demo traffic is allowed only when -DatabaseUrl targets the blogging_elk_demo MySQL schema."
}

function Invoke-AppForm {
    param(
        [Microsoft.PowerShell.Commands.WebRequestSession]$Session,
        [string]$Path,
        [hashtable]$Fields
    )

    try {
        Invoke-WebRequest -UseBasicParsing -Uri "$BaseUrl$Path" -Method Post -Body $Fields -WebSession $Session -MaximumRedirection 5
    } catch {
        $statusCode = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { "connection error" }
        throw "Demo request to $Path failed with $statusCode. No response body or submitted values were logged."
    }
}

function Register-DemoUser {
    param(
        [string]$Name,
        [string]$Email,
        [string]$Password,
        [string]$Role
    )

    $session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
    Invoke-WebRequest -UseBasicParsing -Uri "$BaseUrl/blog/users/register" -Method Get -WebSession $session | Out-Null
    Invoke-AppForm -Session $session -Path "/blog/users/register" -Fields @{
        name = $Name
        email = $Email
        password = $Password
        role = $Role
    } | Out-Null

    return @{ Session = $session; Email = $Email; Password = $Password }
}

function Login-DemoUser {
    param([hashtable]$Account)

    Invoke-AppForm -Session $Account.Session -Path "/login" -Fields @{
        username = $Account.Email
        password = $Account.Password
    } | Out-Null
}

for ($iteration = 1; $iteration -le $Count; $iteration++) {
    $suffix = [guid]::NewGuid().ToString("N").Substring(0, 10)
    $password = [guid]::NewGuid().ToString("N")
    $blogger = Register-DemoUser -Name "Demo Blogger $iteration" -Email "blogger-$suffix@example.test" -Password $password -Role "BLOGGER"
    $guest = Register-DemoUser -Name "Demo Reader $iteration" -Email "reader-$suffix@example.test" -Password $password -Role "GUEST"

    Login-DemoUser -Account $blogger
    $title = "ELK demo post $suffix"
    Invoke-AppForm -Session $blogger.Session -Path "/posts/create" -Fields @{
        title = $title
        content = "Synthetic blogging activity generated for local observability testing."
        status = "PUBLISHED"
    } | Out-Null

    $myPosts = Invoke-WebRequest -UseBasicParsing -Uri "$BaseUrl/posts/mine" -WebSession $blogger.Session
    $titlePattern = [regex]::Escape($title)
    $postMatch = [regex]::Match($myPosts.Content, "(?s)<a[^>]+href=[`"'](?:https?://[^/]+)?/posts/view/(\d+)[`"'][^>]*>\s*<strong[^>]*>\s*$titlePattern\s*</strong>")
    if (-not $postMatch.Success) {
        throw "Could not locate the synthetic post after creating it. Check the app response and logs."
    }
    $postId = $postMatch.Groups[1].Value

    Login-DemoUser -Account $guest
    Invoke-WebRequest -UseBasicParsing -Uri "$BaseUrl/posts/view/$postId" -WebSession $guest.Session | Out-Null
    Invoke-WebRequest -UseBasicParsing -Uri "$BaseUrl/posts/search?query=$([uri]::EscapeDataString($title))" -WebSession $guest.Session | Out-Null
    Invoke-AppForm -Session $guest.Session -Path "/blog/likes/toggle" -Fields @{ postId = $postId } | Out-Null

    $commentText = "Synthetic demo comment $suffix"
    Invoke-AppForm -Session $guest.Session -Path "/blog/comments/add" -Fields @{
        postId = $postId
        content = $commentText
    } | Out-Null
    $comments = Invoke-RestMethod -UseBasicParsing -Uri "$BaseUrl/blog/comments" -WebSession $guest.Session -Headers @{ Accept = "application/json" }
    $comment = $comments | Where-Object { $_.post.id -eq [long]$postId -and $_.content -eq $commentText } | Select-Object -First 1
    if ($null -eq $comment) {
        throw "Could not locate the synthetic comment to generate a reply. Check the app response and logs."
    }

    Login-DemoUser -Account $blogger
    Invoke-AppForm -Session $blogger.Session -Path "/blog/reply/add" -Fields @{
        commentId = $comment.id
        replyText = "Synthetic author reply $suffix"
    } | Out-Null

    Write-Output "Generated synthetic post, view, search, like, comment, and reply activity ($iteration of $Count)."
}