package com.testj.core.guice;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NameTokenizers;
import org.skife.jdbi.v2.IDBI;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.testj.TestJConfiguration;
import com.testj.core.services.user.UserService;
import com.testj.repositories.IUsersRepository;
import com.testj.repositories.Database.UsersRepository;

import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;

public class GuiceModule extends AbstractModule
{
    
    private final TestJConfiguration config;
    private final Environment env;
    private final IDBI dbi;
    
    public GuiceModule(TestJConfiguration config, Environment env)
    {
        this.config = config;
        this.env    = env;
        this.dbi    = new DBIFactory().build(this.env, this.config.getDataSourceFactory(), "mysql");
    }

    @Override
    protected void configure()
    {
        this.bindRepositories()
            .bindAppService();
    }
    
    private GuiceModule bindRepositories()
    {
        this.bind(IDBI.class).toInstance(this.dbi);
        
        this.bind(IUsersRepository.class).to(UsersRepository.class).in(Singleton.class);
        
        return this;
    }
    
    private GuiceModule bindAppService()
    {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
              .setFieldMatchingEnabled(true)
              .setFieldAccessLevel(AccessLevel.PUBLIC)
              .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
              .setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        
        this.bind(ModelMapper.class).toInstance(mapper);
        this.bind(UserService.class).in(Singleton.class);
        
        return this;
    }

}
