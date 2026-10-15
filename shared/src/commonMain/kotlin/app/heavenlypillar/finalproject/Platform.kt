package app.heavenlypillar.finalproject

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform