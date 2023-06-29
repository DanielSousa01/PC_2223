import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

class ExecutorWithShutdown(private val executor: Executor) {
    private val lock = ReentrantLock()
    private val terminationCondition = lock.newCondition()

    private val terminatedCommands = AtomicInteger(0)
    private var totalCommands = 0

    private var shutdown = false

    @Throws(RejectedExecutionException::class)
    fun execute(command: Runnable): Unit {
        lock.withLock {
            if (!shutdown) {
                totalCommands += 1
                executor.execute {
                    try {
                        command.run()
                    } finally {
                        terminatedCommands.incrementAndGet()
                    }
                }
            } else
                throw RejectedExecutionException()
        }
    }
    fun shutdown(): Unit {
        lock.withLock {
            shutdown = true

//          I'm not sure if it's necessary
            if (terminatedCommands.get() == totalCommands) {
                terminationCondition.signal()
            }
        }
    }
    @Throws(InterruptedException::class)
    fun awaitTermination(timeout: Duration): Boolean {
        lock.withLock {
            if (terminatedCommands.get() == totalCommands && shutdown)
                return true

            while (true) {
                var remainingNanos = timeout.inWholeNanoseconds
                try {
                    remainingNanos = terminationCondition.awaitNanos(remainingNanos)
                } catch (error: InterruptedException) {
                    if (terminatedCommands.get() == totalCommands && shutdown) return true
                    throw error
                }

                if (terminatedCommands.get() == totalCommands && shutdown)
                    return true
                else
                    if (remainingNanos <= 0) return false
            }
        }
    }
}

