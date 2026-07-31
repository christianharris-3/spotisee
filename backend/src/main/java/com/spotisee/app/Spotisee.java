package com.spotisee.app;

import com.spotisee.app.config.AppConfiguration;
import com.spotisee.app.dao.DataPreProcessingDao;
import com.spotisee.app.dao.UploadDao;
import com.spotisee.app.resources.LoginResource;
import com.spotisee.app.resources.UploadDataResource;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.ManagedDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class Spotisee extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new Spotisee().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)
            )
        );
    }

    @Override
    public void run(
        AppConfiguration configuration,
        Environment environment
    ) {
        final ManagedDataSource dataSource = configuration
            .getDatabase()
            .build(environment.metrics(), "db");

        final Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());

        UploadDao uploadDao = jdbi.onDemand(UploadDao.class);
        DataPreProcessingDao dataPreProcessingDao =
            jdbi.onDemand(DataPreProcessingDao.class);

        environment.jersey().register(new LoginResource());
        environment.jersey().register(
            new UploadDataResource(uploadDao, dataPreProcessingDao)
        );
    }
}