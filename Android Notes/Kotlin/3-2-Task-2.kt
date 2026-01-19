enum class Daypart{
    MORNING,AFTERNOON,EVENING
}
data class Event1(
    val title:String,
    val description: String?=null,
    val dayPart:Daypart,
    val duration: Int
){}
fun main(){
    val event=Event1("Study Kotlin","Commit to studying Kotlin at least 15 minutes per day.",Daypart.EVENING,15)
    println( event)
}