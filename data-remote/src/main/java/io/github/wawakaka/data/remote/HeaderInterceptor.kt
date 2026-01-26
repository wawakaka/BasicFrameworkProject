package io.github.wawakaka.data.remote

import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

internal class HeaderInterceptor : Interceptor, Authenticator {

    /**
     * Interceptor class for setting of the headers for every request
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }

    /**
     * Authenticator for when the authToken need to be refresh and updated
     * everytime we get a 401 error code
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        var requestAvailable: Request? = null
        try {
            requestAvailable = response.request.newBuilder()
                .addHeader("AUTH_TOKEN", "UUID.randomUUID().toString()")
                .build()
            return requestAvailable
        } catch (ex: Exception) {
        }
        return requestAvailable
    }

}