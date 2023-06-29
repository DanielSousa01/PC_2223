# Considere a seguinte implementação não thread-safe de um contentor de objectos com contagem de utilizações, que automaticamente chama a função close quando essa contagem de utilizações é zero. Realize, sem utilizar locks, uma versão thread-safe desta classe.

```kotlin
class UnsafeUsageCountedHolder<T : Closeable>(value: T) {
    private var value: T? = value
    private var useCounter: Int = 1
    fun startUse(): T {
        if (useCounter == 0) throw IllegalStateException("Already closed")
        useCounter += 1
        return value ?: throw IllegalStateException("Already closed")
    }
    fun endUse() {
        if (useCounter == 0) throw IllegalStateException("Already closed")
        if (--useCounter == 0) {
            value?.close()
            value = null
        }
    }
}
```

[Resolução](../src/main/kotlin/UnsafeUsageCountedHolder.kt)