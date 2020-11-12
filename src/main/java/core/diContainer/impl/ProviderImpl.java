package core.diContainer.impl;

import core.diContainer.Provider;

public class ProviderImpl<T> implements Provider<T> {
    private final T obj;

    public ProviderImpl(T obj)
    {
        this.obj = obj;
    }
    public T getInstance() {
        return obj;
    }
}
