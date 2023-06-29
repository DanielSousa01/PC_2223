# Implemente a função com a seguinte assinatura
```kotlin
suspend fun <A,B,C> run(f0: suspend ()->A, f1: suspend ()->B, f2: suspend (A,B)->C): C
```
# que retorna o valor da expressão f2(f0(), f1()), realizando a computação de f0() e f1() em paralelo.

[Resolução](../src/main/kotlin/run.kt)