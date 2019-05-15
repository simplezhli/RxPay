package com.weilu.pay.api.utils;

import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by weilu on 2017/12/14.
 */
public class BusUtil {

    private HashMap<String, CompositeDisposable> mSubscriptionMap;
    private static volatile BusUtil mBusUtil;
    private final Subject<Object> mSubject;
    
    //单列模式
    public static BusUtil getDefault(){
        if (mBusUtil == null){
            synchronized (BusUtil.class){
                if (mBusUtil == null){
                    mBusUtil = new BusUtil();
                }
            }
        }
        return mBusUtil;
    }
    
    public BusUtil(){
        mSubject = PublishSubject.create().toSerialized();
    }

    public void post(Object o){
        mSubject.onNext(o);
    }
    
    /**
     * 返回指定类型的带背压的Flowable实例
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T>Flowable<T> toObservable(Class<T> type){
        return mSubject.toFlowable(BackpressureStrategy.BUFFER)
                .ofType(type);
    }
    /**
     * 一个默认的订阅方法
     *
     * @param <T>
     * @param type
     * @param next
     * @param error
     * @return
     */
    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error){
        return toObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }
    
    /**
     * 是否已有观察者订阅
     *
     * @return
     */
    public boolean hasObservers() {
        return mSubject.hasObservers();
    }
    
    /**
     * 保存订阅后的disposable
     * @param o
     * @param disposable
     */
    public void addSubscription(Object o, Disposable disposable) {
        if (mSubscriptionMap == null) {
            mSubscriptionMap = new HashMap<>();
        }
        String key = o.getClass().getName();
        CompositeDisposable compositeDisposable = mSubscriptionMap.get(key);
        if (compositeDisposable != null) {
            compositeDisposable.add(disposable);
        } else {
            //一次性容器,可以持有多个并提供 添加和移除。
            CompositeDisposable disposables = new CompositeDisposable();
            disposables.add(disposable);
            mSubscriptionMap.put(key, disposables);
        }
    }

    /**
     * 取消订阅
     * @param o
     */
    public void unSubscribe(Object o) {
        if (mSubscriptionMap == null) {
            return;
        }

        String key = o.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)){
            return;
        }
        
        CompositeDisposable compositeDisposable = mSubscriptionMap.get(key);
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }

        mSubscriptionMap.remove(key);
    }

}
