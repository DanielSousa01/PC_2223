# Implemente o sincronizador message queue, para suportar a comunicação entre threads produtoras e consumidoras através de mensagens do tipo genérico T. A comunicação deve usar o critério FIFO (first in first out). A interface pública deste sincronizador é a seguinte:

```kotlin
class MessageQueue<T>() {
    @Throws(InterruptedException::class)
    fun tryEnqueue(message: T, timeout: Duration): Thread? { … }
    @Throws(InterruptedException::class)
    fun tryDequeue(nOfMessages: Int, timeout: Duration): List<T> { … }
}
```

# O método tryEnqueue entrega uma mensagem à fila, retornando a referência para a thread que consumiu a mensagem. O método tryEnqueue fica bloqueado até que: 1) a mensagem entregue seja entregue a um consumidor, 2) o tempo timeout definido para a operação não expirar, ou 3) a thread não for interrompida. O método tryDequeue tenta remover nOfMessages mensagens da fila, bloqueando a thread invocante enquanto: essa operação não puder ser concluída com sucesso, 2) o tempo timeout definido para a operação não  expirar, ou 3) a thread não for interrompida. A remoção pode ser realizada parcialmente, apenas caso o tempo de espera seja excedido, ou seja, a função tryDequeue pode retornar uma lista com dimensão inferior a nOfMessages. Estas operações de remoção devem ser completadas pela ordem de chegada, independentemente dos valores de nOfMessages. Tenha em atenção as consequências de uma desistência, por interrupção ou timeout, de uma operação de tryDequeue.

[Resolução](../src/main/kotlin/MessageQueue.kt)
