/*
 * */
package com.synectiks.process.server.decorators;

import com.google.inject.ImplementedBy;
import com.synectiks.process.server.database.NotFoundException;

import java.util.List;
import java.util.Map;

@ImplementedBy(DecoratorServiceImpl.class)
public interface DecoratorService {
    List<Decorator> findForStream(String streamId);
    List<Decorator> findForGlobal();
    List<Decorator> findAll();
    Decorator findById(String decoratorId) throws NotFoundException;
    Decorator create(String type, Map<String, Object> config, String stream, int order);
    Decorator create(String type, Map<String, Object> config, int order);
    Decorator save(Decorator decorator);
    int delete(String decoratorId);
}
