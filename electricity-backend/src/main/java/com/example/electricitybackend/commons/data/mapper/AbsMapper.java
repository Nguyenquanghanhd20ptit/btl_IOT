package com.example.electricitybackend.commons.data.mapper;

import java.util.List;

public abstract class AbsMapper<Rq,Rp,E> {
    public abstract E toEntity(Rq req);
    public abstract Rp toResponse(E entity);
    public abstract List<E> toEntities(List<Rq> reqs);
    public abstract List<Rp> toResponses(List<E> entities);
}
