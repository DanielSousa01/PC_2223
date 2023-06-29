import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Exchanger<T> {

    data class Request<T>(val exchangeValue: T, val continuation: Continuation<T>)

    private val lock = ReentrantLock()

    private val requestQueue = mutableListOf<Request<T>>()

    suspend fun exchange(value: T): T {
       lock.withLock {
           if (requestQueue.isNotEmpty()) {
               val pair = requestQueue.removeAt(0)
               pair.continuation.resume(value)
               return pair.exchangeValue
           }
       }

        return suspendCoroutine {continuation ->
            lock.withLock {
                if (requestQueue.isNotEmpty()) {
                    val pair = requestQueue.removeAt(0)
                    pair.continuation.resume(value)
                    continuation.resume(pair.exchangeValue)
                } else {
                    val request = Request(value, continuation)
                    requestQueue.add(request)
                }
            }
        }

    }
}
