import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicReference

suspend fun <A,B,C> run(f0: suspend ()-> A, f1: suspend ()-> B, f2: suspend (A, B)->C): C {
    val result = AtomicReference<C?>(null)

    coroutineScope {
        launch {
            val valueF0 = f0()
            val valueF1 = f1()
            result.set(f2(valueF0, valueF1))
        }
    }

    return result.get() ?: throw IllegalStateException("No coroutine finished successfully.")
}
