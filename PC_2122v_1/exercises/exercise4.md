# Considere o sincronizador Exchanger realizado na primeira série de exercícios. Realize um sincronizador com funcionalidade semelhante, mas em que a função exchange é suspend, ou seja, não bloqueia a thread  invocante durante a espera, e não suporta timeout nem cancelamento.

```kotlin
class Exchanger<T> {
    suspend fun exchange(value: T): T { … }
}
```

# Este sincronizador suporta a troca de informação entre pares de corrotinas. As corrotinas que utilizam este sincronizador manifestam a sua disponibilidade para iniciar uma troca invocando o método exchange, especificando o objecto que pretendem entregar à corrotina parceira (value). O método exchange termina devolvendo o valor trocado, quando é realizada a troca com outra corrotina.

[Resolução](../src/main/kotlin/Exchanger.kt)