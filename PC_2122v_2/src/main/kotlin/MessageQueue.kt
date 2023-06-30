import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class MessageQueue<T>() {
    private val lock = ReentrantLock()

    data class EnqueueRequest<T>(val message: T, val condition: Condition, var thread: Thread? = null)
    data class DequeueRequest(val nOfMessages: Int, val condition: Condition, val thread: Thread)

    private val enqueueRequestQueue = mutableListOf<EnqueueRequest<T>>()
    private val dequeueRequestQueue = mutableListOf<DequeueRequest>()

    @Throws(InterruptedException::class)
    fun tryEnqueue(message: T, timeout: Duration): Thread? {
        lock.withLock {
            val request = EnqueueRequest(message, lock.newCondition())
            enqueueRequestQueue.add(request)

            if (dequeueRequestQueue.isNotEmpty() && dequeueRequestQueue[0].nOfMessages <= enqueueRequestQueue.size) {
                val consumer = dequeueRequestQueue.removeAt(0)
                consumer.condition.signal()
                return consumer.thread

            }

            var remainingNanos = timeout.inWholeNanoseconds
            while (true) {
                try {
                    remainingNanos = request.condition.awaitNanos(remainingNanos)
                } catch (error: InterruptedException) {
                    enqueueRequestQueue.remove(request)
                    if (request.thread != null) {
                        Thread.currentThread().interrupt()
                        return request.thread
                    }
                    throw error
                }

                if (request.thread != null) {
                    enqueueRequestQueue.remove(request)
                    return request.thread
                }

                if (remainingNanos <= 0) {
                    enqueueRequestQueue.remove(request)
                    return null
                }
            }
        }
    }

    @Throws(InterruptedException::class)
    fun tryDequeue(nOfMessages: Int, timeout: Duration): List<T> {
        lock.withLock {
            if (enqueueRequestQueue.size >= nOfMessages) {
                val resultList = enqueueRequestQueue.subList(0, nOfMessages)
                resultList.forEach{ request -> request.thread = Thread.currentThread() }
                return resultList.map { request -> request.message }
            }

            val request = DequeueRequest(nOfMessages, lock.newCondition(), Thread.currentThread())
            dequeueRequestQueue.add(request)
            var remainingNanos = timeout.inWholeNanoseconds
            while (true) {
                try {
                    remainingNanos = request.condition.awaitNanos(remainingNanos)
                } catch (error: InterruptedException) {
                    val isFirst = dequeueRequestQueue[0] == request
                    dequeueRequestQueue.remove(request)
                    if (isFirst){
                        if (enqueueRequestQueue.size >= nOfMessages){
                            val resultList = enqueueRequestQueue.subList(0, nOfMessages)
                            resultList.forEach{ request -> request.thread = Thread.currentThread() }
                            Thread.currentThread().interrupt()
                            return resultList.map { request -> request.message }
                        }
                    }
                    throw error
                }

                if (enqueueRequestQueue.size >= nOfMessages){
                    dequeueRequestQueue.remove(request)
                    val resultList = enqueueRequestQueue.subList(0, nOfMessages)
                    resultList.forEach{ request -> request.thread = Thread.currentThread() }
                    return resultList.map { request -> request.message }
                }

                if (remainingNanos <= 0){
                    return enqueueRequestQueue.subList(0, enqueueRequestQueue.size).map { request -> request.message }
                }
            }
        }
    }
}
