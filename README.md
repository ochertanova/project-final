## [REST API](http://localhost:8080/doc)

## Концепция:
- Spring Modulith
  - [Spring Modulith: достигли ли мы зрелости модульности](https://habr.com/ru/post/701984/)
  - [Introducing Spring Modulith](https://spring.io/blog/2022/10/21/introducing-spring-modulith)
  - [Spring Modulith - Reference documentation](https://docs.spring.io/spring-modulith/docs/current-SNAPSHOT/reference/html/)

```
  url: jdbc:postgresql://localhost:5432/jira
  username: jira
  password: JiraRush
```
- Есть 2 общие таблицы, на которых не fk
  - _Reference_ - справочник. Связь делаем по _code_ (по id нельзя, тк id привязано к окружению-конкретной базе)
  - _UserBelong_ - привязка юзеров с типом (owner, lead, ...) к объекту (таска, проект, спринт, ...). FK вручную будем проверять

## Аналоги
- https://java-source.net/open-source/issue-trackers

## Тестирование
- https://habr.com/ru/articles/259055/

Список выполненных задач:
п.1 Выполнен.
п.2 Удалены социальные сети: vk, yandex для возможности аутентификации в приложении.Удалены соответствующие Handlers.
п.3 В файле application.yaml для секьюрных данных задала enviroment variables. В IDEA создала enviroment variables в конфигурации, куда внесла значения для переменных используемого профиля prod.
Также сохранила файл .env локально с указанными значениями.
п.4 Отредактировала файл application-test.yaml, указала настройки для БД h2, но тесты запускаются с ошибкой (на текущий момент не исправила).
Поэтому код закомментирован.
п.5 Написала тесты для всех публичных методов контроллера ProfileRestController(негативные и позитивные).
п.6 Добавила контроллер TaskController, в котором 3 метода (addTags(), updateTags(), deleteTags()).
- Добавила обработку логики в TaskService. 
- Добавила тесты (позитивные) для методов котроллера, но работа методов протестирована (задавала негативные параметры).
- Для тегов добавила в таблицу Reference значения, которые будут добавляться в качестве тегов. Внесла в скрипт liquibase (в changelog.sql).
п.8 Добавила методы расчета времени. 
В TaskService добавила 2 метода: 
- getDayTaskInProgress() - рассчитывает, сколько дней задача была в работе.
- getDayTaskInTesting() - рассчитывает, сколько дней задача была в тестировании.
Статусы задач вынесла в отдельный класс Constants.

Методы не отлажены, необходимо исправить маппинг entity Activity.

п.9 Написала DockerFile, образ собрался успешно. 
При запуске приложения из контейнера падает ошибка, что не найдены параметры в application.yaml. 
Подскажите, пожалуйста, как правильно сделать? нужно задавать дефолтные значения?

п.11 Добавила локализацию в файлах:
- messages.properties - общий, если локаль не найдена (сделала параметры на английском языке).
- messages_fr.properties - для пользователей, находящихся во Франции.
- messages_ru.properties - для пользователей, находящихся в РФ.

