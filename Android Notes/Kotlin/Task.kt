fun main() {
    Task().runTasks()
}

class Task {

    fun runTasks() {

        // Task 1
        println("----\nTask-1:\n-----")
        print(
            "Use the val keyword when the value doesn't change.\n" +
                    "Use the var keyword when the value can change.\n" +
                    "When you define a function, you define the parameters that can be passed to it.\n" +
                    "When you call a function, you pass arguments for the parameters.\n"
        )

        // Task 2
        println("----\nTask-2:\n-----")
        println("New chat message from a friend")

        // Task 3
        println("----\nTask-3:\n-----")
        var discountPercentage = 20
        val item = "Google Chromecast"
        val offer = "Sale - Up to $discountPercentage% discount on $item! Hurry up!"
        println(offer)

        // Task 4
        println("----\nTask-4:\n-----")
        val numberOfAdults = 20
        val numberOfKids = 30
        val total = numberOfAdults + numberOfKids
        println("The total party size is: $total")

        // Task 5
        println("----\nTask-5:\n-----")
        val baseSalary = 5000
        val bonusAmount = 1000
        val totalSalary = baseSalary + bonusAmount
        println("Congratulations for your bonus! You will receive a total of $totalSalary.")

        // Task 6
        println("----\nTask-6:\n-----")
        val firstNumber = 10
        val secondNumber = 5
        val thirdNumber = 8

        println("$firstNumber + $secondNumber = ${add(firstNumber, secondNumber)}")
        println("$firstNumber + $thirdNumber = ${add(firstNumber, thirdNumber)}")

        // Task 7
        println("----\nTask-7:\n-----")
        val firstUserEmailId = "user_one@gmail.com"
        println(displayAlertMessage(emailId = firstUserEmailId))
        println()

        println(displayAlertMessage("Windows OS", "user_two@gmail.com"))
        println()

        println(displayAlertMessage("Mac OS", "user_three@gmail.com"))
        println()

        // Task 8
        println("----\nTask-8:\n-----")
        val steps = 4000
        val caloriesBurned = stepsToCalories(steps)
        println("Walking $steps steps burns $caloriesBurned calories")

        // Task 9
        println("----\nTask-9:\n-----")
        println("Have I spent more time using my phone today: ${compareTime(500, 250)}")
        println("Have I spent more time using my phone today: ${compareTime(600, 900)}")
        println("Have I spent more time using my phone today: ${compareTime(600, 720)}")

        // Task 10
        println("----\nTask-10:\n-----")
        printWeatherForCity("Delhi", 27, 31, 82)
        printWeatherForCity("Hyderabad", 32, 36, 10)
        printWeatherForCity("Ongole", 29, 34, 2)
        printWeatherForCity("Waaji City", 30, 35, 7)

        // OOP Tasks
        println("----\nSmart Home Task:\n-----")
        task3Classes()

        println("----\nLambda Task:\n-----")
        task4Functions()
    }

    fun printWeatherForCity(cityName: String, lowTemp: Int, highTemp: Int, chanceOfRain: Int) {
        println("City: $cityName")
        println("Low temperature: $lowTemp, High temperature: $highTemp")
        println("Chance of rain: $chanceOfRain%")
        println()
    }

    fun compareTime(timeSpentToday: Int, timeSpentYesterday: Int): String {
        return if (timeSpentToday > timeSpentYesterday) "Yes" else "No"
    }

    fun stepsToCalories(numberOfSteps: Int): Double {
        val caloriesPerStep = 0.04
        return numberOfSteps * caloriesPerStep
    }

    fun add(firstNumber: Int, secondNumber: Int): Int {
        return firstNumber + secondNumber
    }

    fun displayAlertMessage(os: String = "Unknown OS", emailId: String): String {
        return "There's a new sign-in request on $os for your Google Account $emailId."
    }

    fun task3Classes() {
        val smartHome = SmartHome(
            SmartTvDevice("Samsung TV", "Entertainment"),
            SmartLightDevice("Philips Light", "Lighting")
        )

        smartHome.turnOnTv()
        smartHome.turnOnLight()

        smartHome.decreaseTvVolume()
        smartHome.changeTvChannelToPrevious()
        smartHome.decreaseLightBrightness()

        smartHome.printSmartTvInfo()
        smartHome.printSmartLightInfo()

        println("Devices currently ON: ${smartHome.deviceTurnOnCount}")
    }

    fun task4Functions() {
        val square: (Int) -> Int = { it * it }
        val sum: (Int, Int) -> Int = { a, b -> a + b }

        repeat(6) { i ->
            println("Kotlin is fun!")
            println("Square of $i = ${square(i)}")
            println("Sum of ${i + 1} + $i = ${sum(i + 1, i)}")
        }
    }
}

/* -------------------- SMART DEVICE -------------------- */

open class SmartDevice(
    val name: String,
    val category: String
) {
    var deviceStatus: String = "off"
    open val deviceType: String = "unknown"

    open fun turnOn() {
        deviceStatus = "on"
        println("$name is ON")
    }

    open fun turnOff() {
        deviceStatus = "off"
        println("$name is OFF")
    }

    fun printDeviceInfo() {
        println("Device name: $name, category: $category, type: $deviceType")
    }
}

/* -------------------- SMART TV -------------------- */

class SmartTvDevice(name: String, category: String) : SmartDevice(name, category) {

    override val deviceType = "Smart TV"
    private var volume = 10
    private var channel = 1

    fun decreaseVolume() {
        if (deviceStatus == "on" && volume > 0) {
            volume--
            println("TV volume decreased to $volume")
        }
    }

    fun previousChannel() {
        if (deviceStatus == "on" && channel > 1) {
            channel--
            println("TV channel changed to $channel")
        }
    }
}

/* -------------------- SMART LIGHT -------------------- */

class SmartLightDevice(name: String, category: String) : SmartDevice(name, category) {

    override val deviceType = "Smart Light"
    private var brightness = 10

    fun decreaseBrightness() {
        if (deviceStatus == "on" && brightness > 0) {
            brightness--
            println("Light brightness decreased to $brightness")
        }
    }
}

/* -------------------- SMART HOME -------------------- */

class SmartHome(
    private val tv: SmartTvDevice,
    private val light: SmartLightDevice
) {
    var deviceTurnOnCount = 0
        private set

    fun turnOnTv() {
        if (tv.deviceStatus != "on") {
            tv.turnOn()
            deviceTurnOnCount++
        }
    }

    fun turnOnLight() {
        if (light.deviceStatus != "on") {
            light.turnOn()
            deviceTurnOnCount++
        }
    }

    fun decreaseTvVolume() {
        tv.decreaseVolume()
    }

    fun changeTvChannelToPrevious() {
        tv.previousChannel()
    }

    fun decreaseLightBrightness() {
        light.decreaseBrightness()
    }

    fun printSmartTvInfo() {
        tv.printDeviceInfo()
    }

    fun printSmartLightInfo() {
        light.printDeviceInfo()
    }
}