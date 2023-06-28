import java.util.concurrent.atomic.AtomicInteger

class UnsafeValue<T>(val value: T, initialLives: Int) {
    val lives = AtomicInteger(initialLives)
}
class UnsafeContainer<T>(private val values: Array<UnsafeValue<T>>){
    private val index = AtomicInteger(0)
    fun consume(): T? {
        while (true) {
            val observedIndex = index.get()
            if (observedIndex >= values.size) return null

            val observedLives = values[observedIndex].lives.get()

            if (observedLives > 0) {
                if (values[observedIndex].lives.compareAndSet(observedLives, observedLives - 1))
                    return values[observedIndex].value
            }
            index.compareAndSet(observedIndex, observedIndex + 1)
        }
    }
}