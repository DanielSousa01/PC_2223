import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicReference

suspend fun <A,B,C> run(f0: suspend ()-> A, f1: suspend ()-> B, f2: suspend (A, B)->C): C = coroutineScope {
    val jobF0 = async { f0() }
    val jobF1 = async { f1() }

    val a = jobF0.await()
    val b = jobF1.await()

    return@coroutineScope f2(a, b)
}
