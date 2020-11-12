package core.service.impl;

import core.dao.EventDao;
import core.diContainer.annotations.Inject;
import core.service.EventService;

public class EventServiceImpl implements EventService {



    private int operationsCount = 0;

    @Inject
    public EventServiceImpl(EventDao dao){

    }

    
    public int getOperationsCount() {
        return operationsCount;
    }

    public void setOperationsCount(int operationsCount) {
        this.operationsCount = operationsCount;
    }
}
