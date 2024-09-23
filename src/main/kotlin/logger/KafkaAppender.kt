package logger

import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.encoder.Encoder
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class KafkaAppender<E> : AppenderBase<E>() {
    var topic: String? = null
    var brokers: String? = null

    private var encoder: Encoder<E>? = null
    private var producer: KafkaProducer<String, ByteArray>? = null

    fun setEncoder(encoder: Encoder<E>) {
        this.encoder = encoder
    }

    override fun start() {
        if (this.encoder == null) {
            addError("No encoder set for the appender named [$name].")
            return
        }
        if (topic == null || brokers == null) {
            addError("Topic or brokers cannot be null.")
            return
        }

        val props = Properties()
        props["bootstrap.servers"] = brokers!!
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.ByteArraySerializer"
        val record =
            ProducerRecord<String, ByteArray>(
                topic,
                """
                {
                	Kafka start
                }
                """.trimIndent().toByteArray(),
            )
        producer = KafkaProducer(props)
        producer?.send(record)
        producer?.flush()
        super.start()
    }

    override fun append(eventObject: E) {
        val bytes = encoder!!.encode(eventObject)
        val record = ProducerRecord<String, ByteArray>(topic, bytes)
        producer?.send(record)
    }

    override fun stop() {
        super.stop()
        producer?.close()
    }
}
