package com.example.consumer.stock.service

import com.example.consumer.common.rabbitmq.DlxProcessingErrorHandler
import com.example.consumer.stock.model.StockListingInformation
import com.fasterxml.jackson.module.kotlin.*
import com.rabbitmq.client.Channel
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import java.io.IOException
import org.springframework.amqp.core.Message;


// stock queue 에 대한 consumer
@Service
class StockSummaryConsumer {
    private var dlxProcessingErrorHandler: DlxProcessingErrorHandler? = null
    private var objectMapper  = jacksonObjectMapper() // koltin의 경우 jacksonObjectMapper

    init {
        this.dlxProcessingErrorHandler = DlxProcessingErrorHandler(DEAD_EXCHANGE_NAME)
    }

    // queue에 발행된 데이터를 기준으로 이메일 발송
    // ex) to-do: q name 수정해줘야한다.
    @RabbitListener(queues = ["q.inform.corona.work"])
    fun listenInform(message: Message, channel: Channel) {
        log.info(message.getBody().toString())
        try {
            val stockListingInformation : StockListingInformation = objectMapper.readValue(message.getBody(), StockListingInformation::class.java)
            // process the image
            if (stockListingInformation != null) {
                // throw exception, we will use DLX handler for retry mechanism
                throw IOException("Size too large")
            } else {
                log.info("Convert to image, creating thumbnail, & publishing : $stockListingInformation")
                // you must acknowledge that message already processed
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false)
            }
        } catch (e: IOException) {
            log.warn("Error processing message : " + String(message.getBody()) + " : " + e.message)
            dlxProcessingErrorHandler!!.handleErrorProcessingMessage(message, channel)
        }

    }

    // queue가 정상 소비가 되지않아 dead로 넘어간 큐에 대해서도 모니티링을 통해 처리해줘야한다.
    // ex) to-do: q name 수정해줘야한다.
    @RabbitListener(queues = ["q.inform.corona.dead"])
    fun listenInformDead(message: String?, channel: Channel) {
        log.info(message)
        val stockListingInformation : StockListingInformation  = objectMapper.readValue(message!!)
        log.error("dead queue coronaData is {}", stockListingInformation)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockSummaryConsumer::class.java)
        private const val DEAD_EXCHANGE_NAME = "q.inform.corona.dead"// ex) to-do: q name 수정해줘야한다.
    }
}