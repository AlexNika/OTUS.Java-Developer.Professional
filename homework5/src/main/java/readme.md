# Домашнее задание 21/10/2024 - Практика по работе с паттернами. Часть 1
## Цель: Умение использовать паттерны в работе
### Описание/Пошаговая инструкция выполнения домашнего задания:
#### Паттерн Iterator
* Есть ящик. В ящике 4 матрёшки разных цветов. Каждая матрёшка состоит из 10 частей.
* Нужно программно реализовать данную концепцию. 
Создать возможность не менять клиентский код обеспечить возможность пересчитывать матрёшки различными способами.
1. Сначала самые маленькие (каждого из 4-х цветов), затем побольше и т.д. до самых больших
2. Сначала одну матрёшку от маленькой до большой, затем другую и т.д.
* Создайте класс Box с четырьмя внутренними листами строк, 
реализуйте Iterator который последовательно возвращает все строки из листов коробки.

Формально описание задачи про матрёшки. Имеем:
```
public final class Matryoshka {

    // [0] - the smallest / [9] - the biggest
    private final List<String> items; 

}
```
```
    public final class Box {

    private final Matryoshka red;        // "red0", "red1", ..., "red9"
    private final Matryoshka green;
    private final Matryoshka blue;
    private final Matryoshka magenta;
    
    // expected: "red0", "green0", "blue0", "magenta0", "red1", "green1", "blue1", "magenta1",... 
    public Iterator<String> getSmallFirstIterator() {
        // TODO
    }
    
    // expected: "red0", "red1", ..., "red9", "green0", "green1", ..., "green9", ... 
    public Iterator<String> getColorFirstIterator() {
        // TODO
    }

}
```

Нужно реализовать два итератора, которые в сумме выводят по 40 String, но в разном порядке.


@author [Alexander Nikolaev](https://github.com/AlexNika)\
@version 1.0
