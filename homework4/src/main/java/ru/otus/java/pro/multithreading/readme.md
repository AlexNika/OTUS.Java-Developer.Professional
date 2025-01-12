# Домашнее задание 07/10/2024 - Практика многопоточности

## Цель: Понять как работают пулы потоков в Java

### Описание/Пошаговая инструкция выполнения домашнего задания:

Реализуйте собственный пул потоков.

* Конструктор пула в качестве аргументов ожидает емкость (количество рабочих потоков).
* После создания объекта пула, он сразу инициализирует и запускает потоки.
* Внутри пула очередь задач на исполнение организуется через LinkedList.
* Для передачи пулу новой задачи используется метод execute(Runnable r),
  указанная задача попадает в очередь исполнения, и как только появится свободный поток – передается на исполнение.
* Также необходимо реализовать метод shutdown(), после выполнения которого новые задачи больше не принимаются пулом
  (при попытке добавить задачу можно бросать IllegalStateException),
  а все потоки для которых больше нет задач завершают свою работу.

### Критерии оценки:

Система оценки - зачет/не зачет.
Задание не принимается, если основной функционал не работает или есть критические недостатки
(например, copy-past кода, классы на 100500 строк, sql-конкатенация, race condition и т.д.).
Если все работает и критических недостатков нет, то зачет.
Но даже при зачете студенту необходимо указывать на код, который можно улучшить.

@author [Alexander Nikolaev](https://github.com/AlexNika)\
@version 1.0