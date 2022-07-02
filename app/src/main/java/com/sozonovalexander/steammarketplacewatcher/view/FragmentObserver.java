package com.sozonovalexander.steammarketplacewatcher.view;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class FragmentObserver extends Fragment {
    protected final CompositeDisposable disposableCollector = new CompositeDisposable();

    protected <T> void subscribeOnFlowableWithLifecycle(Flowable<T> publisher, Consumer<T> onNext, Consumer<? super Throwable> onError) {
        getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), (state) -> {
            if (state.getLifecycle().getCurrentState() == Lifecycle.State.STARTED) {
                disposableCollector.add(publisher.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError));
            }
            if (state.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                disposableCollector.dispose();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposableCollector.dispose();
    }
}
