package com.sozonovalexander.steammarketplacewatcher.view;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.NonNull;

public abstract class FragmentObserver extends Fragment {
    protected final CompositeDisposable disposableCollector = new CompositeDisposable();

    protected <T> void subscribeOnFlowableWithLifecycle(Flowable<T> publisher, Consumer<T> onNext, Consumer<? super Throwable> onError) {
        getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), (state) -> {
            if (state.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                disposableCollector.add(publisher.subscribe(onNext, onError));
            }
            if (state.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                disposableCollector.dispose();
            }
        });
    }

    protected void subscribeOnCompletableWithLifecycle(Completable completable, @NonNull Action onComplete, @NonNull Consumer<? super Throwable> onError) {
        getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), (state) -> {
            if (state.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                disposableCollector.add(completable.subscribe(onComplete, onError));
            }
            if (state.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                disposableCollector.dispose();
            }
        });
    }
}
