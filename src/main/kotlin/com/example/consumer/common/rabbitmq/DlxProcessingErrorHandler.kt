package com.example.consumer.common.rabbitmq

import com.example.consumer.common.rabbitmq.DlxProcessingErrorHandler
import com.rabbitmq.client.Channel
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.lang.NonNull
import java.io.IOException
import java.util.*

/**
 *
 *
 *
 * Generic class to handle RabbitMQ proccessing error that might occur on
 * `try-catch`. This will not handle invalid message conversion
 * though (for example if you has Employee JSON structure to process, but got
 * Animal JSON structure instead from Rabbit MQ queue).
 *
 *
 *
 *
 * In short, this is just a class to avoid boilerplate codes for your handler.
 * Default implementation is re-throw message to dead letter exchange, using
 * `DlxProcessingErrorHandler` class. The basic usage of the
 * interface is :<br></br>
 *
 * <pre>
 * public void handleMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
 * var jsonObjectToBeProcessed = null;
 *
 * try {
 * jsonObjectToBeProcessed = objectMapper.readValue(new String(message.getBody()),
 * JsonObjectToBeProcessed.class);
 *
 * // do real processing here
 * // ...
 * //
 *
 * channel.basicAck(tag, false);
 * } catch (Exception e) {
 * processingErrorHandler.handleErrorProcessingMessage(message, channel, tag);
 * }
 * }
</pre> *
 *
 * @author timpamungkas
 */
class DlxProcessingErrorHandler(deadExchangeName: String?) {
    /**
     * Dead exchange name
     */
    @NonNull
    val deadExchangeName: String?
    private var maxRetryCount = 3

    /**
     * Constructor. Will retry for `maxRetryCount` times and on the next
     * retry will consider message as dead, put it on dead exchange with given
     * `dlxExchangeName` and `routingKey`
     *
     * @param deadExchangeName dead exchange name. Not a dlx for work queue, but
     * exchange name for really dead message (wont processed
     * antmore).
     * @param maxRetryCount    number of retry before message considered as dead (0
     * >= ` maxRetryCount` >= 1000). If set less
     * than 0, will always retry
     * @throws IllegalArgumentException if `dlxExchangeName` or
     * `dlxRoutingKey` is null or empty.
     */
    constructor(deadExchangeName: String?, maxRetryCount: Int) : this(deadExchangeName) {
        setMaxRetryCount(maxRetryCount)
    }

    fun getMaxRetryCount(): Int {
        return maxRetryCount
    }

    /**
     * Handle AMQP message consume error. This default implementation will put
     * message to dead letter exchange for `maxRetryCount` times, thus
     * two variables are required when creating this object:
     * `dlxExchangeName` and `dlxRoutingKey`. <br></br>
     * `maxRetryCount` is 3 by default, but you can set it using
     * `setMaxRetryCount(int)`
     *
     * @param message AMQP message that caused error
     * @param channel channel for AMQP message
     * @param tag     message delivery tag
     * @return `true` if error handler works sucessfully,
     * `false` otherwise
     */
    fun handleErrorProcessingMessage(message: Message, channel: Channel): Boolean {
        val rabbitMqHeader = RabbitmqHeader(message.messageProperties.headers)
        try {
            if (rabbitMqHeader.failedRetryCount >= maxRetryCount) {
                // publish to dead and ack
                log.warn("[DEAD] Error at " + Date() + " on retry " + rabbitMqHeader.failedRetryCount
                        + " for message " + message)
                channel.basicPublish(deadExchangeName, message.messageProperties.receivedRoutingKey,
                        null, message.body)
                channel.basicAck(message.messageProperties.deliveryTag, false)
            } else {
                log.debug("[REQUEUE] Error at " + Date() + " on retry " + rabbitMqHeader.failedRetryCount
                        + " for message " + message)
                channel.basicReject(message.messageProperties.deliveryTag, false)
            }
            return true
        } catch (e: IOException) {
            log.warn("[HANDLER-FAILED] Error at " + Date() + " on retry " + rabbitMqHeader.failedRetryCount
                    + " for message " + message)
        }
        return false
    }

    @Throws(IllegalArgumentException::class)
    fun setMaxRetryCount(maxRetryCount: Int) {
        require(maxRetryCount <= 1000) { "max retry must between 0-1000" }
        this.maxRetryCount = maxRetryCount
    }

    companion object {
        private val log = LoggerFactory.getLogger(DlxProcessingErrorHandler::class.java)
    }

    /**
     * Constructor. Will retry for n times (default is 3) and on the next retry will
     * consider message as dead, put it on dead exchange with given
     * `dlxExchangeName` and `routingKey`
     *
     * @param deadExchangeName dead exchange name. Not a dlx for work queue, but
     * exchange name for really dead message (wont processed
     * antmore).
     * @throws IllegalArgumentException if `dlxExchangeName` or
     * `dlxRoutingKey` is null or empty.
     */
    init {
        require(!StringUtils.isAnyEmpty(deadExchangeName)) { "Must define dlx exchange name" }
        this.deadExchangeName = deadExchangeName
    }
}