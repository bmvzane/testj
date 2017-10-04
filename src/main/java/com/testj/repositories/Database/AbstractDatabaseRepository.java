package com.testj.repositories.Database;

import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.Update;

import com.google.inject.Inject;

public abstract class AbstractDatabaseRepository
{
    @Inject
    private ModelMapper mapper;
    private final IDBI dbi;

    public AbstractDatabaseRepository(IDBI dbi)
    {
        this.dbi = dbi;
    }
    
    protected IDBI getDBI()
    {
        return this.dbi;
    }
    
    protected ModelMapper getMapper()
    {
        return this.mapper;
    }
    
    protected void statement(final String sql, Optional<Map<String, Object>> bindings)
    {
        Handle h = this.dbi.open();
        
        Update stmt = h.createStatement(sql);
        
        if (bindings.isPresent()) {
            stmt = stmt.bindFromMap(bindings.get());
        }
        
        stmt.execute();
        
        h.close();
    }

}
