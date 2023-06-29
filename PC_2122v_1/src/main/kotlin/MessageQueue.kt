import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class MessageQueue<T>() {
    data class Request(val nOfMessages: Int, val condition: Condition)
    private val lock = ReentrantLock()
    private val messageQueue = mutableListOf<T>()
    private val consumeQueue = mutableListOf<Request>()

    fun enqueue(message: T): Unit = lock.withLock {
        messageQueue.add(message)

        if (consumeQueue.isNotEmpty() && messageQueue.size >= consumeQueue.first().nOfMessages) {
            consumeQueue.first().condition.signal()
        }
    }

    @Throws(InterruptedException::class)
    fun tryDequeue(nOfMessages: Int, timeout: Duration): List<T>? {
        require(nOfMessages > 0)

        lock.withLock {
            if (messageQueue.size >= nOfMessages) {
                val resultList = messageQueue.subList(0, nOfMessages)
                repeat(nOfMessages) {messageQueue.removeAt(0)}
                return resultList
            }

            val request = Request(nOfMessages, lock.newCondition())
            consumeQueue.add(request)
            var remainingNanos = timeout.inWholeNanoseconds

            while (true){
                try {
                    remainingNanos = request.condition.awaitNanos(remainingNanos)
                } catch (error: InterruptedException) {
                    val isFirst = consumeQueue.first() == request
                    consumeQueue.remove(request)

                    if (isFirst) {
                        return if (messageQueue.size >= nOfMessages) {
                            val resultList = messageQueue.subList(0, nOfMessages)
                            repeat(nOfMessages) {
                                messageQueue.removeAt(0)
                            }
                            Thread.currentThread().interrupt()
                            resultList
                        } else {
                            Thread.currentThread().interrupt()
                            null
                        }
                    }

                    throw error
                }

                if (messageQueue.size >= nOfMessages) {
                    val resultList = messageQueue.subList(0, nOfMessages)
                    repeat(nOfMessages) {
                        messageQueue.removeAt(0)
                    }
                    consumeQueue.remove(request)
                    return resultList
                }

                if (remainingNanos <= 0) {
                    consumeQueue.remove(request)
                    return null
                }
            }
        }
    }
}

