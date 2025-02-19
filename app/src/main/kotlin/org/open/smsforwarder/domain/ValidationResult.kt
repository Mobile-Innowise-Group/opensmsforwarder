package org.open.smsforwarder.domain

import org.open.smsforwarder.utils.Resources

// TODO: Ticket 14. Refactor this entity and use cases - move errorMessage out of the domain.
data class ValidationResult(
    val successful: Boolean,
    val errorMessage: Resources.StringProvider? = null
)
