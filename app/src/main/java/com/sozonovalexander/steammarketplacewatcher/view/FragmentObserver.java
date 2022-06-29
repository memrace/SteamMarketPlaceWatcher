package com.sozonovalexander.steammarketplacewatcher.view;

import androidx.fragment.app.Fragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class FragmentObserver extends Fragment {
    protected final CompositeDisposable disposableCollector = new CompositeDisposable();

    protected <T> void subscribeOnFlowableWithLifecycle(Flowable<T> publisher, Consumer<T> onNext, Consumer<? super Throwable> onError) {
        disposableCollector.add(publisher.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposableCollector.dispose();
    }
}
