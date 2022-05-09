package pl.edu.wat.droman.data

enum class ETopic(val path: String) {
    LAST_WILL("/droman/lastWill"),
    BIRTH("/droman/birth"),
    STATE("/droman/state"),
    PICTURE("/droman/picture"),
    TEST("/test"),
    VALIDATE("/droman/validate")
}