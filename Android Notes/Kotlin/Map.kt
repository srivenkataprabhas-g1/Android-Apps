fun main() {

    /* -------------------------------
       Create a MutableMap
    -------------------------------- */

    val solarSystem = mutableMapOf(
        "Mercury" to 0,
        "Venus" to 0,
        "Earth" to 1,
        "Mars" to 2,
        "Jupiter" to 79,
        "Saturn" to 82,
        "Uranus" to 27,
        "Neptune" to 14
    )

    // Print initial size
    println(solarSystem.size)

    /* -------------------------------
       Add a Key-Value Pair
    -------------------------------- */

    solarSystem["Pluto"] = 5
    println(solarSystem.size)

    // Print Pluto's value
    println(solarSystem["Pluto"])

    /* -------------------------------
       Access a Non-Existent Key
    -------------------------------- */

    println(solarSystem.get("Theia")) // returns null

    /* -------------------------------
       Remove a Key-Value Pair
    -------------------------------- */

    solarSystem.remove("Pluto")
    println(solarSystem.size)

    /* -------------------------------
       Update an Existing Value
    -------------------------------- */

    solarSystem["Jupiter"] = 78
    println(solarSystem["Jupiter"])
}
