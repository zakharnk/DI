import core.dao.EventDao;
import core.dao.UserDao;
import core.dao.impl.EventDaoImpl;
import core.dao.impl.UserDaoImpl;
import core.diContainer.Injector;
import core.diContainer.Provider;
import core.diContainer.exceptions.BindingNotFoundException;
import core.diContainer.exceptions.ConstructorNotFoundException;
import core.diContainer.exceptions.TooManyConstructorsException;
import core.diContainer.impl.InjectorImpl;
import core.service.EventService;
import core.service.UserService;
import core.service.impl.EventServiceImpl;
import core.service.impl.UserServiceImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiContainerTest {
    @Test
    public void testExistingBinding() {
        Injector injector = new InjectorImpl();
        injector.bind(EventDao.class, EventDaoImpl.class);
        Provider<EventDao> serviceProvider = injector.getProvider(EventDao.class);
        assertNotNull(serviceProvider);
        assertNotNull(serviceProvider.getInstance());
        assertSame(EventDaoImpl.class, serviceProvider.getInstance().getClass());
    }

    @Test
    public void testComplicatedBinding() {
        Injector injector = new InjectorImpl();
        injector.bind(EventService.class, EventServiceImpl.class);
        injector.bind(EventDao.class, EventDaoImpl.class);
        Provider<EventService> serviceProvider = injector.getProvider(EventService.class);
        assertNotNull(serviceProvider);
        assertNotNull(serviceProvider.getInstance());
        assertSame(EventServiceImpl.class, serviceProvider.getInstance().getClass());
    }

    @Test
    public void testNullBinding() {
        Injector injector = new InjectorImpl();
        Provider<EventService> serviceProvider = injector.getProvider(EventService.class);
        assertNull(serviceProvider);
    }

    @Test
    public void testSingletonBinding() {
        Injector injector = new InjectorImpl();
        injector.bindSingleton(EventService.class, EventServiceImpl.class);
        injector.bind(EventDao.class, EventDaoImpl.class);
        Provider<EventService> serviceProvider = injector.getProvider(EventService.class);
        serviceProvider.getInstance().setOperationsCount(2);
        Provider<EventService> serviceProvider2 = injector.getProvider(EventService.class);
        assertEquals(serviceProvider2.getInstance().getOperationsCount(), 2);
    }

    @Test(expected = BindingNotFoundException.class)
    public void testBindingNotFoundExceptionInBinding() {
        Injector injector = new InjectorImpl();
        injector.bind(EventService.class, EventServiceImpl.class);
        injector.getProvider(EventService.class);
    }

    @Test(expected = TooManyConstructorsException.class)
    public void testTooManyConstructorsExceptionInBinding() {
        Injector injector = new InjectorImpl();
        injector.bind(UserService.class, UserServiceImpl.class);
        injector.getProvider(EventService.class);
    }

    @Test(expected = ConstructorNotFoundException.class)
    public void testConstructorNotFoundExceptionInBinding() {
        Injector injector = new InjectorImpl();
        injector.bind(UserDao.class, UserDaoImpl.class);
        injector.getProvider(UserDao.class);
    }


}
