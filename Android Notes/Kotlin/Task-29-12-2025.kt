/* -------------------------------
   Enum
-------------------------------- */

enum class Difficulty {
    EASY, MEDIUM, HARD
}

/* -------------------------------
   Generic Question (Reusable)
-------------------------------- */

class Question<T>(
    val questionText: String,
    val answer: T,
    val difficulty: String
)

/* -------------------------------
   Data Class with Generics
-------------------------------- */

data class QuestionData<T>(
    val questionText: String,
    val answer: T,
    val difficulty: Difficulty
)

/* -------------------------------
   Progress Interface
-------------------------------- */

interface ProgressPrintable {
    val progressText: String
    fun printProgressBar()
}

/* -------------------------------
   Quiz + Companion Object
-------------------------------- */

class Quiz : ProgressPrintable {

    companion object StudentProgress {
        var total: Int = 10
        var answered: Int = 3
    }

    // Questions used for scope functions
    val question1 = QuestionData(
        "Quoth the raven ___",
        "nevermore",
        Difficulty.MEDIUM
    )

    val question2 = QuestionData(
        "The sky is green. True or false",
        false,
        Difficulty.EASY
    )

    val question3 = QuestionData(
        "How many days are there between full moons?",
        28,
        Difficulty.HARD
    )

    override val progressText: String
        get() = "$answered of $total answered."

    override fun printProgressBar() {
        repeat(answered) { print("▓") }
        repeat(total - answered) { print("▒") }
        println()
        println(progressText)
    }

    // Scope function: let()
    fun printQuiz() {
        question1.let {
            println(it.questionText)
            println(it.answer)
            println(it.difficulty)
        }
        println()

        question2.let {
            println(it.questionText)
            println(it.answer)
            println(it.difficulty)
        }
        println()

        question3.let {
            println(it.questionText)
            println(it.answer)
            println(it.difficulty)
        }
        println()
    }
}

/* -------------------------------
   Tasks (unchanged)
-------------------------------- */

class Task22 {
    fun run() {
        println("----\nTask 22 - Reusable class with generics\n-----")

        Question("Quoth the raven ___", "nevermore", "medium")
        Question("The sky is green. True or false", false, "easy")
        Question("How many days are there between full moons?", 28, "hard")
    }
}

class Task23 {
    fun run() {
        println("----\nTask 23 - Data class with generics\n-----")

        val q1 = QuestionData("Quoth the raven ___", "nevermore", Difficulty.MEDIUM)
        val q2 = QuestionData("The sky is green. True or false", false, Difficulty.EASY)
        val q3 = QuestionData("How many days are there between full moons?", 28, Difficulty.HARD)

        println(q1)
        println(q2)
        println(q3)
    }
}

/* -------------------------------
   Main (apply scope function)
-------------------------------- */

fun main() {

    Quiz().printProgressBar()
    println()

    // apply() scope function
    Quiz().apply {
        printQuiz()
    }

    Task22().run()
    println()

    Task23().run()
}
