/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.commons.notifications.action

import org.opensearch.action.ActionRequest
import org.opensearch.action.ActionRequestValidationException
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.io.stream.Writeable
import org.opensearch.commons.destination.message.LegacyBaseMessage
import org.opensearch.commons.destination.message.LegacyChimeMessage
import org.opensearch.commons.destination.message.LegacyCustomWebhookMessage
import org.opensearch.commons.destination.message.LegacyDestinationType
import org.opensearch.commons.destination.message.LegacySlackMessage
import org.opensearch.commons.notifications.model.Feature
import java.io.IOException

/**
 * Action Request to publish notification. This is a legacy implementation.
 * This should not be used going forward, instead use [SendNotificationRequest].
 */
class LegacyPublishNotificationRequest : ActionRequest {
    val baseMessage: LegacyBaseMessage
    val feature: Feature

    companion object {
        /**
         * reader to create instance of class from writable.
         */
        val reader = Writeable.Reader { LegacyPublishNotificationRequest(it) }
    }

    /**
     * constructor for creating the class
     * @param baseMessage the base message to send
     * @param feature the feature that is trying to use this request
     */
    constructor(
        baseMessage: LegacyBaseMessage,
        feature: Feature
    ) {
        this.baseMessage = baseMessage
        this.feature = feature
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    constructor(input: StreamInput) : super(input) {
        baseMessage = when (requireNotNull(input.readEnum(LegacyDestinationType::class.java)) { "Destination type cannot be null" }) {
            LegacyDestinationType.CHIME -> LegacyChimeMessage(input)
            LegacyDestinationType.CUSTOMWEBHOOK -> LegacyCustomWebhookMessage(input)
            LegacyDestinationType.SLACK -> LegacySlackMessage(input)
        }
        feature = input.readEnum(Feature::class.java)
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun writeTo(output: StreamOutput) {
        super.writeTo(output)
        output.writeEnum(baseMessage.channelType)
        baseMessage.writeTo(output)
        output.writeEnum(feature)
    }

    /**
     * {@inheritDoc}
     */
    override fun validate(): ActionRequestValidationException? = null
}