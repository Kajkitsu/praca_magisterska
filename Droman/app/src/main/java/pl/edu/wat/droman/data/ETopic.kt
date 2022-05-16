package pl.edu.wat.droman.data

enum class ETopic(private val path: String) {
    LAST_WILL("droman/lastWill"),
    BIRTH("droman/birth"),
    STATE("droman/state"),
    COMMAND("droman/command"),
    PICTURE("droman/picture"),
    TEST("test"),
    VALIDATE("droman/validate");

    fun forClient(clientID: String): String {
        return "$path/$clientID"
    }
}