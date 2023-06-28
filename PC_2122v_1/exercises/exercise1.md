# Implemente o sincronizador message queue, para suportar a comunicação entre threads produtoras e consumidoras através de mensagens do tipo genérico T. A comunicação deve usar o critério FIFO (first in first out). A interface pública deste sincronizador é a seguinte:

```kotlin
class MessageQueue<T>() {
    fun enqueue(message: T): Unit { … }
    @Throws(InterruptedException::class)
    fun tryDequeue(nOfMessages: Int, timeout: Duration): List<T>? { … }
}
```

# O método enqueue entrega uma mensagem à fila nunca ficando bloqueado. O método tryDequeue tenta remover nOfMessages mensagens da fila, bloqueando a thread invocante enquanto: essa operação não puder ser concluída com sucesso, ou 2) o tempo timeout definido para a operação não expirar, ou 3) a thread não for interrompida. A remoção não pode ser realizada parcialmente, i.e., ou nOfMessages mensagens são removidas ou nenhuma mensagem é removida. Estas operações de remoção devem ser completadas pela ordem de chegada, independentemente dos valores de nOfMessages. Tenha em atenção as consequências de uma desistência, por cancelamento ou timeout, de uma operação de tryDequeue.

[Resolução](../src/main/kotlin/MessageQueue.kt)