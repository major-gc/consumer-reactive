package com.example.consumer.common.rabbitmq

import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Represents RabbitMQ Header. Tested on RabbitMQ 3.7.x.
 *
 * @author timpamungkas
 */
class RabbitmqHeader(headers: Map<String?, Any?>?) {
    private var xDeaths: MutableList<RabbitmqHeaderXDeath> = ArrayList(2)
    private var xFirstDeathExchange = StringUtils.EMPTY
    private var xFirstDeathQueue = StringUtils.EMPTY
    private var xFirstDeathReason = StringUtils.EMPTY

    // get from queue "wait"
    val failedRetryCount: Int
        get() {
            // get from queue "wait"
            for (xDeath in xDeaths) {
                if (xDeath.exchange!!.toLowerCase().endsWith(KEYWORD_QUEUE_WAIT)
                        && xDeath.queue!!.toLowerCase().endsWith(KEYWORD_QUEUE_WAIT)) {
                    return xDeath.count
                }
            }
            return 0
        }

    fun getxDeaths(): List<RabbitmqHeaderXDeath> {
        return xDeaths
    }

    fun getxFirstDeathExchange(): String {
        return xFirstDeathExchange
    }

    fun getxFirstDeathQueue(): String {
        return xFirstDeathQueue
    }

    fun getxFirstDeathReason(): String {
        return xFirstDeathReason
    }

    fun setxDeaths(xDeaths: MutableList<RabbitmqHeaderXDeath>) {
        this.xDeaths = xDeaths
    }

    fun setxFirstDeathExchange(xFirstDeathExchange: String) {
        this.xFirstDeathExchange = xFirstDeathExchange
    }

    fun setxFirstDeathQueue(xFirstDeathQueue: String) {
        this.xFirstDeathQueue = xFirstDeathQueue
    }

    fun setxFirstDeathReason(xFirstDeathReason: String) {
        this.xFirstDeathReason = xFirstDeathReason
    }

    companion object {
        private const val KEYWORD_QUEUE_WAIT = "wait"
    }

    init {
        if (headers != null) {
            val xFirstDeathExchange: Optional<*> = Optional.ofNullable(headers["x-first-death-exchange"])
            val xFirstDeathQueue: Optional<*> = Optional.ofNullable(headers["x-first-death-queue"])
            val xFirstDeathReason: Optional<*> = Optional.ofNullable(headers["x-first-death-reason"])
            xFirstDeathExchange.ifPresent { s: Any -> setxFirstDeathExchange(s.toString()) }
            xFirstDeathQueue.ifPresent { s: Any -> setxFirstDeathQueue(s.toString()) }
            xFirstDeathReason.ifPresent { s: Any -> setxFirstDeathReason(s.toString()) }
            val xDeathHeaders = headers["x-death"] as List<Map<String, Any>>?
            if (xDeathHeaders != null) {
                for (x in xDeathHeaders) {
                    val hdrDeath = RabbitmqHeaderXDeath()
                    val reason: Optional<*> = Optional.ofNullable(x["reason"])
                    val count: Optional<*> = Optional.ofNullable(x["count"])
                    val exchange: Optional<*> = Optional.ofNullable(x["exchange"])
                    val queue: Optional<*> = Optional.ofNullable(x["queue"])
                    val routingKeys: Optional<*> = Optional.ofNullable(x["routing-keys"])
                    val time: Optional<*> = Optional.ofNullable(x["time"])
                    reason.ifPresent { s: Any -> hdrDeath.reason = s.toString() }
                    count.ifPresent { s: Any -> hdrDeath.count = s.toString().toInt() }
                    exchange.ifPresent { s: Any -> hdrDeath.exchange = s.toString() }
                    queue.ifPresent { s: Any -> hdrDeath.queue = s.toString() }
                    routingKeys.ifPresent { r: Any? ->
                        val listR = r as List<String>?
                        hdrDeath.routingKeys = listR
                    }
                    time.ifPresent { d: Any? -> hdrDeath.time = d as Date? }
                    xDeaths.add(hdrDeath)
                }
            }
        }
    }
}