# Implemente a classe ExecutorWithShutdown que adiciona a um executor, recebido no construtor, a capacidade de shutdown, isto é, colocar o objecto num estado em que não são aceites mais comandos.

```kotlin
class ExecutorWithShutdown(private val executor: Executor) {
    @Throws(RejectedExecutionException::class)
    fun execute(command: Runnable): Unit {...}
    fun shutdown(): Unit {...}
    @Throws(InterruptedException::class)
    fun awaitTermination(timeout: Duration): Boolean {...}
}
```

# A função execute delega a execução do comando no executor recebido no construtor. Caso esteja em modo de shutdown, lança a excepção RejectedExecutionException. A função shutdown coloca o executor em modo de shutdown, tendo como consequência que todas as chamadas de execute resultam no lançamento de RejectedExecutionException. A função awaitTermination bloqueia a thread invocante até que o executor esteja em modo de shutdown e que todos os comandos submetidos tenham sido executados. Esta função retorna true se a condição anterior for verdadeira, false se a condição anterior for falsa e o tempo máximo de espera definido por timeout tiver decorrido. A função awaitTermination deve reagir a interrupções da thread invocante, acabando a espera com o lançamento da excepção InterruptedException. Note que o comando que é passado a executor pode ser diferente do recebido na função execute.

[Resolução](../src/main/kotlin/ExecutorWithShutdown.kt)