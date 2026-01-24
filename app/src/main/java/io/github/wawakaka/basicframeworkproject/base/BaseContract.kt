package io.github.wawakaka.basicframeworkproject.base

/**
 * Base Contract for MVP pattern
 * @deprecated Migrate to TOAD pattern with State/Event/Effect sealed classes (Milestone 6).
 * This class will be removed in Milestone 7.
 */
@Deprecated(
    message = "Migrate to TOAD pattern with State/Event/Effect sealed classes (Milestone 6)",
    level = DeprecationLevel.WARNING
)
open class BaseContract {

    interface View

    interface Presenter<in T> {
        fun detach()
        fun attach(view: T)
    }
}