package com.belokon.echo

import com.belokon.echo.permissions.PermissionStatus
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.mockito.Mockito
import java.util.*

object TestEnv {
    val testUid = UUID.randomUUID()
    val activityMock = Mockito.mock(EchoActivity::class.java).apply {
        Mockito.`when`(this.uuid).then { testUid }
    }
    val onNextLogDelegate: Observer<PermissionStatus> = object : Observer<PermissionStatus> {
        override fun onComplete() {

        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onNext(t: PermissionStatus) {
            println("value -> $t")
        }

        override fun onError(e: Throwable) {

        }
    }
}