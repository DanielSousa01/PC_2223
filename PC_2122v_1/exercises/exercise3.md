# Implemente, sem utilizar locks, uma versão thread-safe da classe UnsafeContainer que armazena um conjunto de valores e o número de vezes que esses valores podem ser consumidos.

```kotlin
class UnsafeValue<T>(val value: T, var initialLives: Int)
class UnsafeContainer<T>(private val values: Array<UnsafeValue<T>>){
    private var index = 0
    fun consume(): T? {
        while(index < values.size) {
            if (values[index].lives > 0) {
                values[index].lives -= 1
                return values[index].value
            }
            index += 1
        }
        return null
    }
}
```

# A título de exemplo, o contentor construído por Container(Value("isel", 3), Value("pc", 4)) retorna, através do método consume, a string "isel" três vezes e a string "pc" quatro vezes. Depois disso, todas as chamadas a consume retornam null.

[Resolução](../src/main/kotlin/UnsafeContainer.kt)