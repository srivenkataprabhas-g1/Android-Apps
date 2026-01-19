fun main() {

    // -------------------------------
    // READ-ONLY LIST (List)
    // -------------------------------

    // Create a read-only List using listOf()
    // You can READ data, but CANNOT modify it
    val planets: List<String> = listOf(
        "Mercury", "Venus", "Earth", "Mars",
        "Jupiter", "Saturn", "Uranus", "Neptune"
    )

    // Print size of the List
    println("List size: ${planets.size}")

    // Access elements from the List
    println("Planet at index 2: ${planets[2]}")     // Earth
    println("Planet at index 3: ${planets.get(3)}") // Mars

    // Find index of an element
    println("Index of Earth: ${planets.indexOf("Earth")}")
    println("Index of Pluto: ${planets.indexOf("Pluto")}") // -1

    // Loop through the List
    println("\nPlanets (Read-only List):")
    for (planet in planets) {
        println(planet)
    }

    // ❌ The following operations are NOT allowed on List
    // planets.add("Pluto")
    // planets.remove("Earth")
    // planets[0] = "Sun"

    // -------------------------------
    // MUTABLE LIST (MutableList)
    // -------------------------------

    // Create a MutableList using mutableListOf()
    // You can ADD, REMOVE, and UPDATE elements
    val solarSystem: MutableList<String> = mutableListOf(
        "Mercury", "Venus", "Earth", "Mars",
        "Jupiter", "Saturn", "Uranus", "Neptune"
    )

    // Add elements to the MutableList
    solarSystem.add("Pluto")          // Add to the end
    solarSystem.add(3, "Theia")       // Insert at index 3

    // Update an element
    solarSystem[3] = "Future Moon"

    // Access updated elements
    println("\nUpdated element at index 3: ${solarSystem[3]}")
    println("Element at index 9: ${solarSystem[9]}")

    // Remove elements
    solarSystem.removeAt(9)           // Remove by index
    solarSystem.remove("Future Moon") // Remove by value

    // Check if elements exist
    println("\nContains Pluto: ${solarSystem.contains("Pluto")}")
    println("Contains Future Moon: ${"Future Moon" in solarSystem}")

    // Loop through MutableList
    println("\nPlanets (Mutable List):")
    for (planet in solarSystem) {
        println(planet)
    }
}
