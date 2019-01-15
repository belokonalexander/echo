package com.belokon.myapplication

import android.app.Activity
import android.content.Context
import android.util.Log
import com.belokon.echo.ActivityProvider
import com.belokon.echo.Echo
import com.belokon.echo.results.ActivityResultDispatcher
import com.belokon.echo.UniqueActivityDelegate
import io.reactivex.subjects.PublishSubject
import org.kodein.di.Kodein
import org.kodein.di.bindings.Scope
import org.kodein.di.bindings.ScopeCloseable
import org.kodein.di.bindings.ScopeRegistry
import org.kodein.di.bindings.StandardScopeRegistry
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.multiton
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton
import java.util.*

class DiContainer(private val applicationContext: Context) {

    companion object {
        private const val APP_KEY = "app"
        private const val ACTIVITY_KEY = "activity"
    }

    private val cache = mutableMapOf<String, Kodein>()

    private fun create(key: String, default: Kodein): Kodein {
        val cachedValue = cache[key]
        return if (cachedValue != null) {
            cachedValue
        } else {
            cache[key] = default
            default
        }
    }

    private fun destroy(key: String) {
        cache.remove(key)
    }

    fun getAppScope() = create(APP_KEY, Kodein {
        bind() from singleton { Echo() }
        bind() from singleton { instance<Echo>().activityResultDispatcher }
        bind() from singleton { instance<Echo>().permissionDispatcher }
        bind() from multiton { uuid: UUID -> Presenter(instance(), instance(), uuid) }
    })

    fun getActivityScope(activity: Activity) = create(ACTIVITY_KEY, Kodein {
        extend(getAppScope())
    })

    fun destroyActivityScope() {
        destroy(ACTIVITY_KEY)
    }
}

data class ActivityScopeItem(val activity: Activity, var scopeRegistry: ScopeRegistry? = null)




object ActivityScope : Scope<ActivityScopeItem>, ScopeCloseable {

    override fun close() {
        Log.e("#1020", " CLOSE ")
    }

    override fun getRegistry(context: ActivityScopeItem): ScopeRegistry {
        return context.scopeRegistry ?: StandardScopeRegistry().also { context.scopeRegistry = it }
    }
}