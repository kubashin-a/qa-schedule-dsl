# Реализация DSL для e2e тестирования

Это проект-демонстрация концепции e2e тестирования систем с расписанием для статьи.

Концепция расписания реализована максимально близко к реальному проекту, хотя многие полезные вещи
(как указано в статье), были удалены для упрощения восприятие идеи.

## Запуск
Для работы проекта достаточно только установленного JDK не ниже 11-й версии.

Для запуска демонстрационных сценариев достаточно из корня проекта выполнить:

**под Linux:**
```
./gradlew run
```
**под Windows:**
```
gradlew.bat run
```

## Сценарии

Сценарии хранятся в виде отдельных файлов в каталоге  testscenarios/impl. Чтобы они загружались автоматически,
используется Java ClassLoader и немного ~~костылей~~ магии в build.gradle.kts скрипте, который генерирует
необходимый для ClassLoader файл.

Описание сценариев:
* **Scenario1** – ординарный сценарий.
* **Scenario2** – демонстрация негативных и частично негативных путей, а также демонстрация возвращения сразу нескольких значений из функции multiOrder.
* **Scenario3** – генерация нескольких однотипных сценариев на основании внешних данных.
* **Scenario4** – генерация однотипных частей одного сценария на основании внешних данных.
* **Auxiliary** – вспомогательный сценарий (подробнее можно почитать в статье).

Scenario1 и Scenario2 выполняются в виртуальный день D1, а Scenario3 и Scenario4 выполняются в виртуальный D2.
Это сделано для простоты восприятия вывода данных в консоли.

## Объекты сценариев

В качестве примера созданы Client и Order классы. При добавлении нового объекта, его необходимо встроить в DSL.
Для этого в dsl/DSL.kt нужно:
* добавить хранилище этого типа объектов в класс Scenario: `val newObjects = GenericScenarioObjects<newObject>(...)`
* сделать соответствующую ссылку из Scenario в класс Stage
* в runSchedule по аналогии со stage.clients и stage.orders добавить проверку необходимости вызова stateCheck для
нового объекта, а также непосредственный вызов функции для stateCheck (смотрите строки 41 и 68 в качестве примера).

## Расписание

Описывается в init секции файла dsl/Schedule.kt. При желании можно легко добавить/удалить/изменить stages.

## Global Context

В качестве примера реализованы следующие возможности:
* Переменная **prices** – общие для всех сценариев прайсы товаров. Здесь используется статические данные.
В реальном проекте, что-то подобное обычно загружается либо из внешнего файла, либо через вызов функции из
Auxiliary сценария.
* Переменная **deliveryConfirmation** – в нее из всех сценариев добавляется информация о доставках, после чего
она в агрегированном виде выводится из Auxiliary сценария.
* Функция **generate** – служит для генерации уникальных идентификаторов в пределах одного прогона. Она основана
на использовании глобального счётчика.

Если возникнет необходимость выполнять сценарии в многопоточном режиме, то все переменные из globalContext нужно
делать потокобезопасными.

## P.S.

Чтобы лучше понять работу концепции, я рекомендую попробовать создать свои версии сценариев, расписания
и возможно, даже сделать новые объекты или расширить возможности существующих.
