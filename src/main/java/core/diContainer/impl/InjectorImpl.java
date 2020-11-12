package core.diContainer.impl;

import core.diContainer.Injector;
import core.diContainer.Provider;
import core.diContainer.annotations.Inject;
import core.diContainer.exceptions.BindingNotFoundException;
import core.diContainer.exceptions.ConstructorNotFoundException;
import core.diContainer.exceptions.TooManyConstructorsException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InjectorImpl implements Injector {

    private final ConcurrentHashMap<Class<?>, ObjectSpecification<?>> container =
            new ConcurrentHashMap<Class<?>, ObjectSpecification<?>>();

    public <T> Provider<T> getProvider(Class<T> type) {
        ObjectSpecification<T> spec = (ObjectSpecification<T>)container.get(type);

        if(spec == null){
            return null;
        }
        if (spec.isSingleton && spec.instance != null){
            return spec.instance;
        }

        List<Object> parameters = new ArrayList<Object>();
        for (Class<?> parameterType:
        spec.constructor.getParameterTypes()){
            Provider provider = this.getProvider(parameterType);
            if (provider == null){
                throw new BindingNotFoundException();
            }

            parameters.add(provider.getInstance());
        }

        try {

            ProviderImpl<T> provider =  new ProviderImpl<T>(parameters.size() == 0
                    ? spec.constructor.newInstance()
                    : spec.constructor.newInstance(parameters.toArray()));

            if (spec.isSingleton)
            {
                spec.instance = provider;
            }

            return provider;
        } catch (Exception e) {
            throw new BindingNotFoundException();
        }


    }

    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        this.container.put(intf,
                new ObjectSpecification<T>(
                        this.findAppropriateConstructor(impl),
                        false));
    }


    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        this.container.put(intf,
                new ObjectSpecification<T>(
                        this.findAppropriateConstructor(impl),
                        true));
    }

    private <T> Constructor<T> findAppropriateConstructor(Class<? extends T> impl) {
        Constructor<?> selectedConstructor = null;
        boolean isInjectUsed = false;

        for (Constructor<?> constructor :
                impl.getConstructors()) {

            if (!Modifier.isPublic(constructor.getModifiers())) {
                continue;
            }

            if (constructor.isAnnotationPresent(Inject.class)) {
                if (isInjectUsed) {
                    throw new TooManyConstructorsException();
                }

                selectedConstructor = constructor;
                isInjectUsed = true;
            }

            if (constructor.getParameterTypes().length == 0 && !isInjectUsed) {
                selectedConstructor = constructor;
            }
        }

        if (selectedConstructor == null) {
            throw new ConstructorNotFoundException();
        }

        return (Constructor<T>)selectedConstructor;
    }

    private class ObjectSpecification<T> {
        private final Constructor<T> constructor;
        private final boolean isSingleton;
        private Provider<T> instance;

        public ObjectSpecification(Constructor<T> constructor, boolean isSingleton) {
            this.constructor = constructor;
            this.isSingleton = isSingleton;
        }

    }
}
