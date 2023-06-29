import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture

suspend fun race(f0: suspend () -> Int, f1: suspend () -> Int): Int = coroutineScope {

    val jobs = listOf(async { f0()}, async { f1() })

    val completedJob = CompletableFuture<Deferred<Int>>()

    jobs.forEach {job ->
        job.invokeOnCompletion {
            completedJob.complete(job)
        }
    }

    return@coroutineScope completedJob.get().await()
}
