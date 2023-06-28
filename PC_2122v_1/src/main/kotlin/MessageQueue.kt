import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class MessageQueue<T>() {
    private val lock = ReentrantLock()
    private val messageQueue = mutableListOf<T>()
    private val consumeQueue = mutableListOf<Condition>()

    fun enqueue(message: T): Unit = lock.withLock {
        if (consumeQueue.isNotEmpty()) {
            consumeQueue.removeAt(0).signal()
        } else {
            messageQueue.add(message)
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

            val consumerCondition = lock.newCondition()
            consumeQueue.add(consumerCondition)
            var remainingNanos = timeout.inWholeNanoseconds

            while (true){
                try {
                    remainingNanos = consumerCondition.awaitNanos(remainingNanos)
                } catch (error: InterruptedException) {
                    consumeQueue.remove(consumerCondition)
                    if (messageQueue[0] == consumerCondition) {
                        if (messageQueue.size >= nOfMessages) {
                            val resultList = messageQueue.subList(0, nOfMessages)
                            repeat(nOfMessages) {
                                messageQueue.removeAt(0)
                            }

                            return resultList

                        } else {
                            Thread.currentThread().interrupt()
                            return null
                        }
                    }
                    throw error
                }

                if (messageQueue.size >= nOfMessages) {
                    val resultList = messageQueue.subList(0, nOfMessages)
                    repeat(nOfMessages) {
                        messageQueue.removeAt(0)
                    }
                    consumeQueue.remove(consumerCondition)
                    return resultList
                }

                if (remainingNanos <= 0) {
                    consumeQueue.remove(consumerCondition)
                    return null
                }
            }
        }
    }
}

