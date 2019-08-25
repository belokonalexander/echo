package com.belokonalexander.example

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import java.util.*

object TestEnv {
    val testUid = UUID.randomUUID()

    fun <T> getTestObserver(): TestObserver<T> = TestObserver.create(object : Observer<T> {
        override fun onComplete() {
        }

        override fun onSubscribe(d: Disposable) {
        }

        override fun onNext(t: T) {
            println("value -> $t")
        }

        override fun onError(e: Throwable) {

        }
    })
}