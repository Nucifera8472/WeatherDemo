package at.nuceria.weatherdemo.data

import kotlinx.coroutines.flow.*
import timber.log.Timber

/**
 * A more modern version of Android's NetworkBoundResource class using kotlin flow.
 * As seen in Coding Flow's course https://github.com/codinginflow/MVVMNewsApp <3
 */
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType?>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType?) -> Boolean = { true }
) = flow {
    Timber.d("START networkBoundResource flow")
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))
        try {
            saveFetchResult(fetch())
            query().map { Resource.Success(it) }
        } catch (t: Throwable) {
            query().map { Resource.Error(t, it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }
    emitAll(flow)
}
