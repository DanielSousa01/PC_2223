# Realize o sincronizador MessageBox com a interface apresentada em seguida.

```kotlin
class MessageBox<T> {
    suspend fun waitForMessage(): T { … }
    fun sendToAll(message: T): Int { … }
}
```

# A função waitForMessage suspende a corrotina onde foi realizada a invocação até que uma mensagem seja enviada através da função sendToAll. A função sendToAll deve retornar o número exacto de chamadas a waitForMessage que receberam a mensagem, podendo este valor ser zero (não existiam corrotinas à espera de mensagem), um, ou maior que um. A mensagem passada na chamada sendToAll não deve ficar disponível para chamadas futuras da função waitForMessage.

[Resolução](../src/main/kotlin/MessageBox.kt)