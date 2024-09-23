
# Репозиторий для демонстрации подключения системы логирования ELK

Этот репозиторий содержит все необходимые инструменты для демонстрации подключения системы логирования ELK. Включены два примера отправки логов: через Kafka и через TCP.

## Как начать

1. **Клонирование репозитория:**
   ```bash
   git clone [URL_репозитория]
   cd [имя_клонированной_папки]
   ```

2. **Запуск Docker Compose:**
   ```bash
   docker-compose up -d
   ```
* Zookeeper: Менеджмент кластера Kafka.
* Kafka: Система обработки потоковых данных.
* Kafka UI: Веб-интерфейс для просмотра топиков и сообщений Kafka.
* Elasticsearch: Поисковый и аналитический движок.
* Logstash: Сервер для обработки и пересылки логов.
* Kibana: Веб-интерфейс для визуализации данных из Elasticsearch.


_Доступ к Kibana и Kafka-UI:_
*    Kibana: Откройте браузер и введите http://localhost:5601.
* Kafka-UI: Откройте браузер и введите http://localhost:8089



3. **Запуск приложения:**

   Используйте IDE для запуска приложения или выполните команду:
   ```bash
     java -jar build/libs/elk-example-1.jar
   ```

## Отправка логов через Kafka

Для отправки логов через Kafka используется специальный `KafkaAppender`, разработанный как часть этого репозитория.

### Особенности KafkaAppender:
- **Топик:** Позволяет указать топик для отправки логов.
- **Адрес брокера:** Можно указать адрес брокера для отправки логов.
- **Минимальные зависимости:** Эта реализация не требует дополнительных зависимостей и показывает отличную производительность.

### Конфигурация Logstash

В репозитории представлена конфигурация для сервера Logstash, которая позволяет корректно обрабатывать и индексировать поступающие логи.

### Конфигурация Logstash для Kafka
Логи отправляются в формате json.

Фильтр извлекает сообщение из поля `message` и корректно конвертирует поля.

_Примечание: топик настраивается автоматически, но можно создать свой топик предварительно в Kafka, если требуется установить количество партиций и репликацию._

```json
input {
  kafka {
    bootstrap_servers => "kafka:29092"
    topics => ["log-topic"]
    group_id => "logstash"
    codec => json
    consumer_threads => 3
  }
}

filter {
  json {
    source => "message"
  }
}

output {
  elasticsearch {
    hosts => "elasticsearch:9200"
    index => "kafka-logs"
  }
}
```

### Примеры использования

#### Конфигурация KafkaAppender

Пример конфигурации `KafkaAppender` в вашем логирующем приложении:

```xml
<appender name="kafka" class="logger.KafkaAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                {
                "timestamp_logger":"%date{dd MMM yyyy;HH:mm:ss.SSS}",
                "app": "example-app-kafka",
                "level":"%level",
                "message":"%msg",
                "thread": "%thread",
                "exception":"%ex{full}"
                }
            </Pattern>
        </layout>
    </encoder>
    <topic>log-topic</topic>
    <brokers>localhost:9092</brokers>
</appender>
```

#### Параметры конфигурации
- **timestamp_logger:** Параметр для логирования времени. Logstash на самом деле сам устанавливает время, но бывают ситуации, когда расхождения между временем от Logstash и временем лога имеют значение.
- **app:** Имя приложения.
- **level:** Уровень логирования.
- **message:** Сообщение.
- **thread:** Поток.
- **exception:** Исключение.



## Отправка логов через TCP
Для этого используется библиотека logstash-logback-encoder для энкодера



### Конфигурация Logstash для Kafka
Логи отправляются в формате json.

```lombok.config
input {
   tcp{
   port => 5000
   codec => json
   }
}


output {
   elasticsearch {
   hosts => "elasticsearch:9200"
   index => "tcp"
  } 
}
```
### Примеры использования

#### Конфигурация LogstashTcpSocketAppender

Пример конфигурации `LogstashTcpSocketAppender` в вашем логирующем приложении:

```xml
<appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
   <destination>localhost:5000</destination>
   <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
      <providers>
         <loggerName />
         <pattern>
            <pattern>
               {
               "timestamp_logger":"%date{dd MMM yyyy;HH:mm:ss.SSS}",
               "app": "example-app-tsp",
               "level":"%level",
               "message":"%msg",
               "thread": "%thread",
               "exception":"%ex{full}"
               }
            </pattern>
         </pattern>
      </providers>
   </encoder>
</appender>
```
----

## Контакты
по всем вопросом обращаться по адресу: starinenko2251@gmail.com

или телеграм https://t.me/Local_engineer