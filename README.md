# ELK Logging System Demonstration Repository

This repository contains all the necessary tools for demonstrating the integration of the ELK logging system. It includes two examples of log forwarding: through Kafka and via TCP.

RU-README: [README-ru.md](README-ru.md)
## Getting Started

1. **Clone the repository:**
   ```bash
   git clone [repository_URL]
   cd [cloned_folder_name]
   ```

2. **Start Docker Compose:**
   ```bash
   docker-compose up -d
   ```
* Zookeeper: Manages the Kafka cluster.
* Kafka: Stream processing system.
* Kafka UI: Web interface for viewing Kafka topics and messages.
* Elasticsearch: Search and analytics engine.
* Logstash: Server for processing and forwarding logs.
* Kibana: Web interface for visualizing data from Elasticsearch.

_Access Kibana and Kafka-UI:_
* Kibana: Open your browser and go to http://localhost:5601.
* Kafka-UI: Open your browser and go to http://localhost:8089.


3. **Start the application:**

   Use your IDE to run the application or execute the command:
   ```bash
     java -jar build/libs/elk-example-1.jar
   ```

## Sending Logs through Kafka

Logs are sent via a special `KafkaAppender` developed as part of this repository.

### KafkaAppender Features:
- **Topic:** Allows specifying a topic for log forwarding.
- **Broker Address:** You can specify the broker address for sending logs.
- **Minimal Dependencies:** This implementation requires no additional dependencies and demonstrates excellent performance.

### Logstash Configuration

The repository provides a Logstash server configuration that correctly processes and indexes incoming logs.

### Logstash Configuration for Kafka
Logs are sent in JSON format.

The filter extracts the message from the `message` field and correctly converts fields.

_Note: The topic is configured automatically, but you can create your own topic in Kafka beforehand if you need to set the number of partitions and replication._

```lombok.config
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

### Usage Examples

#### KafkaAppender Configuration

Example configuration of `KafkaAppender` in your logging application:

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

#### Configuration Parameters
- **timestamp_logger:** Parameter for logging time. Although Logstash sets the time itself, there are cases where differences between Logstash time and log time are significant.
- **app:** Application name.
- **level:** Logging level.
- **message:** Message.
- **thread:** Thread.
- **exception:** Exception.


## Sending Logs via TCP
For this, the `logstash-logback-encoder` library is used for encoding.


### Logstash Configuration for TCP
Logs are sent in JSON format.

```json
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
### Usage Examples

#### LogstashTcpSocketAppender Configuration

Example configuration of `LogstashTcpSocketAppender` in your logging application:

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
               "app": "example-app-tcp",
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

## Contact
For any questions, contact: starinenko2251@gmail.com

or Telegram: https://t.me/Local_engineer