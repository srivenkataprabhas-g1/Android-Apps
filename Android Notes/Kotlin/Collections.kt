// File: CollectionsExample.kt

fun main() {

    // -------------------------
    // LIST (Read-only)
    // -------------------------
    // List keeps order and allows duplicates
    val numbers: List<Int> = listOf(1, 2, 2, 3)

    println("List:")
    for (num in numbers) {
        println(num)
    }

    // -------------------------
    // MUTABLE LIST
    // -------------------------
    // MutableList can be modified
    val mutableNumbers: MutableList<Int> = mutableListOf(1, 2, 3)

    mutableNumbers.add(4)       // add element
    mutableNumbers[0] = 10      // update element
    mutableNumbers.remove(2)    // remove element

    println("\nMutableList:")
    for (num in mutableNumbers) {
        println(num)
    }

    // -------------------------
    // SET (Read-only)
    // -------------------------
    // Set stores unique values only
    val fruits: Set<String> = setOf("Apple", "Banana", "Apple")

    println("\nSet:")
    for (fruit in fruits) {
        println(fruit)  // "Apple" appears only once
    }

    // -------------------------
    // MUTABLE SET
    // -------------------------
    val mutableFruits: MutableSet<String> = mutableSetOf("Apple", "Banana")

    mutableFruits.add("Orange")
    mutableFruits.add("Apple") // duplicate, will not be added

    println("\nMutableSet:")
    for (fruit in mutableFruits) {
        println(fruit)
    }

    // -------------------------
    // MAP (Read-only)
    // -------------------------
    // Map stores key -> value pairs
    val ages: Map<String, Int> = mapOf(
        "Alex" to 15,
        "Sam" to 16
    )

    println("\nMap:")
    for ((name, age) in ages) {
        println("$name is $age years old")
    }

    // -------------------------
    // MUTABLE MAP
    // -------------------------
    val mutableAges: MutableMap<String, Int> = mutableMapOf()

    mutableAges["Alex"] = 15
    mutableAges["Sam"] = 16
    mutableAges["Alex"] = 17 // update value

    println("\nMutableMap:")
    for ((name, age) in mutableAges) {
        println("$name is $age years old")
    }

    // -------------------------
    // CONDITIONS WITH COLLECTIONS
    // -------------------------
    if ("Alex" in mutableAges) {
        println("\nAlex exists in the map")
    }

    // -------------------------
    // LOOPS WITH CONDITIONS
    // -------------------------
    println("\nChecking ages:")
    for ((name, age) in mutableAges) {
        if (age >= 16) {
            println("$name is 16 or older")
        } else {
            println("$name is younger than 16")
        }
    }

    // -------------------------
    // hashCode() EXAMPLE
    // -------------------------
    val word1 = "Kotlin"
    val word2 = "Kotlin"

    println("\nhashCode example:")
    println("word1 hashCode: ${word1.hashCode()}")
    println("word2 hashCode: ${word2.hashCode()}")
    // Same content = same hashCode
}
