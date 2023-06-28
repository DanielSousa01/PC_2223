# Implemente o sincronizador semáforo com capacidade de shutdown, com a seguinte interface pública:

```kotlin
class Semaphore(private val initialUnits: Int) {
    fun release(): Unit { … }
    @Throws(InterruptedException::class, RejectedExecutionException::class)
    fun acquire(timeout: Duration): Boolean { … }
    fun shutdown(): Unit { … }
    @Throws(InterruptedException::class)
    fun awaitTermination(timeout: Duration): Boolean { … }
}
```

# O método release entrega uma unidade ao semáforo, nunca ficando bloqueado. O método acquire tenta adquirir uma unidade, bloqueando a thread invocante enquanto: 1) essa operação não puder ser concluída com sucesso, ou 2) o tempo timeout definido para a operação não expirar, ou 3) a thread não for interrompida. O método shutdown coloca o sincronizador em modo de shutting-down, onde todas as operações de aquisição pendentes ou futuras terminam com a excepção RejectedExecutionException. O método awaitTermination espera até que: 1) o semáforo esteja em modo de shutting down e 2) que o número de unidades seja igual ao número inicial de unidades. Este método: 1) recebe o tempo máximo de espera, 2) deve também ser sensível a interrupções, 3) pode ser chamado por mais do que uma thread.

[Resolução](../src/main/kotlin/Semaphore.kt)
