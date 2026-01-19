fun main() {
//     print(birthdayGreeting("Prabhas"))
//Task 1
    println("----\nTask-1:\n-----")
    print(
        "Use the val keyword when the value doesn't change.\nUse the var keyword when the value can change." +
                "\nWhen you define a function, you define the parameters that can be passed to it." +
                "\nWhen you call a function, you pass arguments for the parameters.\n"
    )
//Task 2
    println("----\nTask-2:\n-----")
    //println("New chat message from a friend'}
    println("New chat message from a friend")
// Task 3
    println("----\nTask-3:\n-----")
    var discountPercentage: Int = 0
    var offer: String = ""
    var item = "Google Chromecast"
    discountPercentage = 20
    offer = "Sale - Up to $discountPercentage% discount on $item! Hurry up!"

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
    println("Congratulations for your bonus! You will receive a total of $totalSalary (additional bonus).")
// Task 6
    println("----\nTask-6:\n-----")
    val firstNumber = 10
    val secondNumber = 5
    val thirdNumber = 8

    val result = add(firstNumber, secondNumber)
    val anotherResult = add(firstNumber, thirdNumber)

    println("$firstNumber + $secondNumber = $result")
    println("$firstNumber + $thirdNumber = $anotherResult")
// Task 7
    println("----\nTask-7:\n-----")
    val firstUserEmailId = "user_one@gmail.com"

    // The following line of code assumes that you named your parameter as emailId.
    // If you named it differently, feel free to update the name.
    println(displayAlertMessage(emailId = firstUserEmailId))
    println()

    val secondUserOperatingSystem = "Windows OS"
    val secondUserEmailId = "user_two@gmail.com"

    println(displayAlertMessage(secondUserOperatingSystem, secondUserEmailId))
    println()

    val thirdUserOperatingSystem = "Mac OS"
    val thirdUserEmailId = "user_three@gmail.com"

    println(displayAlertMessage(thirdUserOperatingSystem, thirdUserEmailId))
    println()
// Task 8
    /**
    The pedometer is an electronic device that counts the number of steps taken. Nowadays, almost all mobile phones, smart watches, and fitness gear come with pedometers built into them. The health and fitness app uses built-in pedometers to calculate the number of steps taken. This function calculates the number of calories that the user burns based on the user's number of steps.

    Can you rename the functions, function parameters, and variables in this program based on best practices?

    fun main() {
    val Steps = 4000
    val caloriesBurned = PEDOMETERstepsTOcalories(Steps);
    println("Walking $Steps steps burns $caloriesBurned calories")
    }

    fun PEDOMETERstepsTOcalories(NumberOFStepS: Int): Double {
    val CaloriesBURNEDforEachStep = 0.04
    val TotalCALORIESburned = NumberOFStepS * CaloriesBURNEDforEachStep
    return TotalCALORIESburned
    }
     **/
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
    printWeatherForCity("Ongole", 59, 64, 2)
    printWeatherForCity("Waaji City", 50, 55, 7)
}

fun printWeatherForCity(cityName: String, lowTemp: Int, highTemp: Int, chanceOfRain: Int) {
    println("City: $cityName")
    println("Low temperature: $lowTemp, High temperature: $highTemp")
    println("Chance of rain: $chanceOfRain%")
    println()
}

fun compareTime(timeSpentToday: Int, timeSpentYesterday: Int): String {
    return if(timeSpentToday > timeSpentYesterday)"Yes" else "No"
}
fun stepsToCalories(numberOfSteps: Int): Double {
    val caloriesPerStep = 0.04
    val totalCaloriesBurned = numberOfSteps * caloriesPerStep
    return totalCaloriesBurned
}
fun add(firstNumber:Int,secondNumber:Int):Int{
    return firstNumber+secondNumber
}
fun displayAlertMessage(os:String="Unknown OS",emailId:String):String{
    return "There's a new sign-in request on $os for your Google Account $emailId."
}

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

    private var volume: Int = 10
    private var channel: Int = 1

    fun increaseVolume() {
        if (deviceStatus == "on") {
            volume++
            println("TV volume increased to $volume")
        }
    }

    fun decreaseVolume() {
        if (deviceStatus == "on" && volume > 0) {
            volume--
            println("TV volume decreased to $volume")
        }
    }

    fun nextChannel() {
        if (deviceStatus == "on") {
            channel++
            println("TV channel changed to $channel")
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

    private var brightness: Int = 10

    fun increaseBrightness() {
        if (deviceStatus == "on") {
            brightness++
            println("Light brightness increased to $brightness")
        }
    }

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

    fun turnOffTv() {
        if (tv.deviceStatus == "on") {
            tv.turnOff()
            deviceTurnOnCount--
        }
    }

    fun turnOnLight() {
        if (light.deviceStatus != "on") {
            light.turnOn()
            deviceTurnOnCount++
        }
    }

    fun turnOffLight() {
        if (light.deviceStatus == "on") {
            light.turnOff()
            deviceTurnOnCount--
        }
    }

    fun decreaseTvVolume() {
        if (tv.deviceStatus == "on") {
            tv.decreaseVolume()
        }
    }

    fun changeTvChannelToPrevious() {
        if (tv.deviceStatus == "on") {
            tv.previousChannel()
        }
    }

    fun decreaseLightBrightness() {
        if (light.deviceStatus == "on") {
            light.decreaseBrightness()
        }
    }

    fun printSmartTvInfo() {
        tv.printDeviceInfo()
    }

    fun printSmartLightInfo() {
        light.printDeviceInfo()
    }
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
    println(square(5))

    val sum = { a: Int, b: Int -> a + b }
    println(sum(10, 5))
    var c=0;
    repeat(6) {
        var i=c
        println("Kotlin is fun!")
        println("Square of "+i+"="+square(i))
        c++
        println("Sum of "+c+"+"+i+"="+sum(c,i))

    }
}