import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference

suspend fun race1(f0: suspend () -> Int, f1: suspend () -> Int): Int = coroutineScope {

    val jobs = listOf(async { f0()}, async { f1() })

    val completedJob = CompletableFuture<Deferred<Int>>()

    jobs.forEach {job ->
        job.invokeOnCompletion {
            completedJob.complete(job)
        }
    }

    return@coroutineScope completedJob.get().await()
}

suspend fun race2(f0: suspend () -> Int, f1: suspend () -> Int): Int {
    val result = AtomicReference<Int?>(null)

    coroutineScope {
        launch {
            val res = f0()
            result.compareAndSet(null, res)
            cancel()
        }

        launch{
            val res = f1()
            result.compareAndSet(null, res)
            cancel()
        }
    }

    return result.get() ?: throw IllegalStateException("No coroutine finished successfully.")
}
