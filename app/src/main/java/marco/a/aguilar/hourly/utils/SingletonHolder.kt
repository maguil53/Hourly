package marco.a.aguilar.hourly.utils

/**
 * A reusable class used to create a Singleton that requires an argument.
 *
 * Explanation behind Volatile and synchronized can be found here:
 *  https://developer.android.com/codelabs/kotlin-android-training-room-database#5
 */
open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if(i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if(i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }

}