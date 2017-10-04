package com.testj;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.ApplicationPath;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.testj.core.guice.GuiceModule;
import com.testj.health.AppHealthCheck;
import com.testj.resources.v1.FakeUserResource;
import com.testj.resources.v1.UserResource;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

@ApplicationPath("/")
public class TestJApplication extends Application<TestJConfiguration>
{
    
    private static TestJConfiguration config;
    private static Environment env;
    private static Injector injector;
    
    private final MigrationsBundle<TestJConfiguration> migration = new MigrationsBundle<TestJConfiguration>() {
        @Override
        public DataSourceFactory getDataSourceFactory(TestJConfiguration configuration)
        {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new TestJApplication().run(args);
    }

    @Override
    public String getName() {
        return "TestJ";
    }

    @Override
    public void initialize(final Bootstrap<TestJConfiguration> bootstrap)
    {
        bootstrap.addBundle(this.migration);
    }

    @Override
    public void run(final TestJConfiguration configuration,
                    final Environment environment
    ) {
        config = configuration;
        env    = environment;
        
        injector = Guice.createInjector(new GuiceModule(config, env));
        
        // set all Date serialization to ISO8601
        env.getObjectMapper()
           .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        this.registerHealthChecks()
            .registerResources();
    }
    
    private TestJApplication registerResources()
    {
        List<Class<?>> resourceClasses = Arrays.asList(
            UserResource.class,
            FakeUserResource.class
        );
        
        for (Class<?> resourceClass : resourceClasses) {
            env.jersey().register(injector.getInstance(resourceClass));
        }
        
        return this;
    }
    
    private TestJApplication registerHealthChecks()
    {
        env.healthChecks()
           .register("TestJ", injector.getInstance(AppHealthCheck.class));
        
        return this;
    }

}
