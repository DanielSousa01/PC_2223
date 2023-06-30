import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class Semaphore(private val initialUnits: Int) {
    private val lock = ReentrantLock()
    private val unitsAvailable = lock.newCondition()
    private var units = initialUnits
    private var shuttingDown = false

    fun release(): Unit = lock.withLock {
        units++
        unitsAvailable.signal()
    }

    @Throws(InterruptedException::class, RejectedExecutionException::class)
    fun acquire(timeout: Duration): Boolean {
        lock.withLock {
            if (shuttingDown) {
                throw RejectedExecutionException()
            }
            if (units > 0) {
                units -= 1
                return true
            }

            var remainingNanos = timeout.inWholeNanoseconds
            while (true) {
                try {
                    remainingNanos = unitsAvailable.awaitNanos(remainingNanos)
                } catch(error: InterruptedException) {
                    if (shuttingDown) {
                        throw RejectedExecutionException()
                    }
                    if (units > 0) {
                        units -= 1
                        if (units > 0) {
                            unitsAvailable.signal()
                        }
                        Thread.currentThread().interrupt()
                        return true
                    }
                    throw error
                }

                if (shuttingDown) {
                    throw RejectedExecutionException()
                }
                if (units > 0) {
                    units -= 1
                    if (units > 0) {
                        unitsAvailable.signal()
                    }
                    return true
                }

                if (remainingNanos < 0) return false
            }
        }
    }

    fun shutdown(): Unit = lock.withLock {
        shuttingDown = true
        units = initialUnits
        unitsAvailable.signalAll()
    }

    @Throws(InterruptedException::class)
    fun awaitTermination(timeout: Duration): Boolean {
        lock.withLock {
            if (shuttingDown && units == initialUnits) {
                return true
            }

            var remainingNanos = timeout.inWholeNanoseconds
            while (true) {
                try {
                    remainingNanos = unitsAvailable.awaitNanos(remainingNanos)
                } catch (error: InterruptedException) {
                    if (shuttingDown && units == initialUnits) {
                        Thread.currentThread().interrupt()
                        return true
                    }

                    throw error
                }

                if (shuttingDown && units == initialUnits) {
                    return true
                }

                if (remainingNanos <= 0) return false
            }
        }
    }
}