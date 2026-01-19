fun main() {

    /* -------------------------------
       Create a MutableSet
    -------------------------------- */

    val solarSystem = mutableSetOf(
        "Mercury",
        "Venus",
        "Earth",
        "Mars",
        "Jupiter",
        "Saturn",
        "Uranus",
        "Neptune"
    )

    // Print initial size
    println(solarSystem.size)

    /* -------------------------------
       Add an Element
    -------------------------------- */

    solarSystem.add("Pluto")
    println(solarSystem.size)

    /* -------------------------------
       Check for an Element
    -------------------------------- */

    println(solarSystem.contains("Pluto"))

    /* -------------------------------
       Try Adding Duplicate
    -------------------------------- */

    solarSystem.add("Pluto")
    println(solarSystem.size)

    /* -------------------------------
       Remove an Element
    -------------------------------- */

    solarSystem.remove("Pluto")
    println(solarSystem.size)
    println(solarSystem.contains("Pluto"))
}