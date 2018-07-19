package io.github.wawakaka.basicframeworkproject.datasource.server.model

/**
 * Created by wawakaka on 19/07/18.
 */
open class ServerRequestError(protected val throwable: Throwable) : Throwable(throwable)