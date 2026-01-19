// File: HigherOrderFunctionsWithCollections.kt

fun main() {

    // Sample list to work with
    val numbers = listOf(1, 2, 3, 4, 5)

    // -------------------------
    // forEach()
    // -------------------------
    // forEach runs a lambda on every item in the collection
    println("forEach example:")
    numbers.forEach { number ->
        println("Number: $number")
    }

    // -------------------------
    // String templates with lambdas
    // -------------------------
    // Lambdas can be used inside string templates
    println("\nString template with lambda:")
    numbers.forEach {
        println("Double of $it is ${it * 2}")
    }

    // -------------------------
    // map()
    // -------------------------
    // map transforms each item and returns a new list
    val doubledNumbers = numbers.map { it * 2 }

    println("\nmap example:")
    println(doubledNumbers)

    // -------------------------
    // filter()
    // -------------------------
    // filter keeps only elements that match a condition
    val evenNumbers = numbers.filter { it % 2 == 0 }

    println("\nfilter example:")
    println(evenNumbers)

    // -------------------------
    // groupBy()
    // -------------------------
    // groupBy groups elements based on a condition
    val groupedByEvenOdd = numbers.groupBy { number ->
        if (number % 2 == 0) "Even" else "Odd"
    }

    println("\ngroupBy example:")
    println(groupedByEvenOdd)

    // -------------------------
    // fold()
    // -------------------------
    // fold combines all elements into a single value
    // First parameter is the initial value
    val sum = numbers.fold(0) { total, number ->
        total + number
    }

    println("\nfold example:")
    println("Sum of numbers: $sum")

    // -------------------------
    // sortedBy()
    // -------------------------
    // sortedBy sorts items based on a selector
    val names = listOf("Alex", "Sam", "Jonathan", "Max")

    val sortedByLength = names.sortedBy { it.length }

    println("\nsortedBy example:")
    println(sortedByLength)

    // -------------------------
    // Combining higher-order functions
    // -------------------------
    // Filter even numbers, double them, then sort
    val result = numbers
        .filter { it % 2 == 0 }
        .map { it * 2 }
        .sortedBy { it }

    println("\nCombined functions result:")
    println(result)
}