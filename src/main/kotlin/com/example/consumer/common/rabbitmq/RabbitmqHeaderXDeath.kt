package com.example.consumer.common.rabbitmq

import java.util.*

/**
 * Represents RabbitMQ Header, part x-death. Tested on RabbitMQ 3.7.x.
 *
 * @author timpamungkas
 */
class RabbitmqHeaderXDeath {
    var count = 0
    var exchange: String? = null
    var queue: String? = null
    var reason: String? = null
    var routingKeys: List<String>? = null
    var time: Date? = null
    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other = obj as RabbitmqHeaderXDeath
        if (count != other.count) {
            return false
        }
        if (exchange == null) {
            if (other.exchange != null) {
                return false
            }
        } else if (exchange != other.exchange) {
            return false
        }
        if (queue == null) {
            if (other.queue != null) {
                return false
            }
        } else if (queue != other.queue) {
            return false
        }
        if (reason == null) {
            if (other.reason != null) {
                return false
            }
        } else if (reason != other.reason) {
            return false
        }
        if (routingKeys == null) {
            if (other.routingKeys != null) {
                return false
            }
        } else if (routingKeys != other.routingKeys) {
            return false
        }
        if (time == null) {
            if (other.time != null) {
                return false
            }
        } else if (time != other.time) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 19
        var result = 1
        result = prime * result + count
        result = prime * result + if (exchange == null) 0 else exchange.hashCode()
        result = prime * result + if (queue == null) 0 else queue.hashCode()
        result = prime * result + if (reason == null) 0 else reason.hashCode()
        result = prime * result + if (routingKeys == null) 0 else routingKeys.hashCode()
        result = prime * result + if (time == null) 0 else time.hashCode()
        return result
    }
}