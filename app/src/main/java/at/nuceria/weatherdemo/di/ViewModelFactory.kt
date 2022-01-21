package com.itranslate.appkit.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * A ViewModel Factory responsible for creating a ViewModel of the requested type
 * Should be injected in all activities needing a ViewModel
 * Builds a separate ViewModel instance for each call (no sharing of a ViewModel between
 * Activities needing the same functionality. If the same ViewModel class is used in several
 * activities, the data layer is responsible for providing the same content
 * Inspired by the google sample, but uses dagger and kotlin magic https://goo.gl/3BxbuA
 */
@Singleton
class ViewModelFactory @Inject constructor(
        val app: Application,
        private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) :
        ViewModelProvider.AndroidViewModelFactory(app) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("unknown model class " + modelClass)
        }
        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
