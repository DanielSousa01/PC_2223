import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessageBox<T> {

    private val lock = ReentrantLock()
    private val waitingForMessageQueue = mutableListOf<Continuation<T>>()
    suspend fun waitForMessage(): T = suspendCoroutine<T> {continuation ->
        lock.withLock {
            waitingForMessageQueue.add(continuation)
        }
    }

    fun sendToAll(message: T): Int {
        lock.withLock {
            val totalReceivedMessages = waitingForMessageQueue.size

            for (waitingRequest in waitingForMessageQueue) {
                waitingRequest.resume(message)
            }

            return totalReceivedMessages
        }
    }
}