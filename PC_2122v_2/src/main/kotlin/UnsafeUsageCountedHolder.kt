import java.io.Closeable
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class UnsafeUsageCountedHolder<T : Closeable>(value: T) {
    private val value = AtomicReference(value)
    private val useCounter = AtomicInteger(1)
    fun startUse(): T {
        while (true) {
            val observedUseCounter = useCounter.get()
            if (observedUseCounter == 0) throw IllegalStateException("Already closed")

            if(useCounter.compareAndSet(observedUseCounter, observedUseCounter + 1))
                return value.get() ?: throw IllegalStateException("Already closed")
        }
    }
    fun endUse() {
        while (true) {
            val observedUseCounter = useCounter.get()
            if (observedUseCounter == 0) throw IllegalStateException("Already closed")

            if (useCounter.compareAndSet(observedUseCounter, observedUseCounter - 1)) {
                if (observedUseCounter - 1 == 0) {
                    val observedValue = value.get()
                    if (value.compareAndSet(observedValue, null))
                        observedValue.close()
                }
            }
        }
    }
}