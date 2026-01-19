enum class Daypart5{
    MORNING,AFTERNOON,EVENING
}
data class Event6(
    val title:String,
    val description: String?=null,
    val daypart:Daypart,
    val durationInMinutes: Int
)
val Event6.durationOfEvent: String
    get() = if (this.durationInMinutes < 60) {
        "short"
    } else {
        "long"
    }
fun main(){
    val event1 = Event6(title = "Wake up", description = "Time to get up", daypart = Daypart.MORNING, durationInMinutes = 0)
    val event2 = Event6(title = "Eat breakfast", daypart = Daypart.MORNING, durationInMinutes = 15)
    val event3 = Event6(title = "Learn about Kotlin", daypart = Daypart.AFTERNOON, durationInMinutes = 30)
    val event4 = Event6(title = "Practice Compose", daypart = Daypart.AFTERNOON, durationInMinutes = 60)
    val event5 = Event6(title = "Watch latest DevBytes video", daypart = Daypart.AFTERNOON, durationInMinutes = 10)
    val event6 = Event6(title = "Check out latest Android Jetpack library", daypart = Daypart.EVENING, durationInMinutes = 45)
    val events= mutableListOf(event1,event2,event3,event4,event5,event6)

    println("Duration of first event of the day: ${events[0].durationOfEvent}")
}