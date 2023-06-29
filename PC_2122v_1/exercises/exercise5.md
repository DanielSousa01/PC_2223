# Implemente a função com a seguinte assinatura
```kotlin
suspend fun race(f0: suspend () -> Int, f1: suspend () -> Int): Int
```
# Esta função executa de forma paralela as funções passadas como argumento, retornando o valor retornado pela primeira função a acabar com sucesso. Uma execução da função race só deve acabar quando as corrotinas criadas no seu âmbito tiverem terminado. Contudo, a função race deve terminar o mais depressa  possível, através do cancelamento da corrotina ainda em execução, após uma das funções passadas como argumento tiver terminado com sucesso.

[Resolução](../src/main/kotlin/race.kt)