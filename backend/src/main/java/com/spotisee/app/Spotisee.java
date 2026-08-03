package com.spotisee.app;

import com.spotisee.app.config.AppConfiguration;
import com.spotisee.app.config.MySqlLogger;
import com.spotisee.app.config.SpotiseeAuthenticator;
import com.spotisee.app.dao.AuthDao;
import com.spotisee.app.dao.DataPreProcessingDao;
import com.spotisee.app.dao.SongDataDao;
import com.spotisee.app.dao.UploadDao;
import com.spotisee.app.exceptions.mapper.UnauthorisedExceptionMapper;
import com.spotisee.app.models.User;
import com.spotisee.app.resources.LoginResource;
import com.spotisee.app.resources.StatAggregationResource;
import com.spotisee.app.resources.UploadDataResource;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.UnauthorizedHandler;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.ManagedDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class Spotisee extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new Spotisee().run(args);
    }

    @Override
    public void run(AppConfiguration configuration,
                    Environment environment) {

        final ManagedDataSource dataSource = configuration.getDatabase().build(
                environment.metrics(), "db"
        );

        // DB setup
        final Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.setSqlLogger(new MySqlLogger());

        UploadDao uploadDao = jdbi.onDemand(UploadDao.class);
        SongDataDao songDataDao = jdbi.onDemand(SongDataDao.class);
        AuthDao authDao = jdbi.onDemand(AuthDao.class);

        // Auth
        SpotiseeAuthenticator authenticator = new SpotiseeAuthenticator(authDao);

        environment.jersey().register(
                new AuthDynamicFeature(
                        new OAuthCredentialAuthFilter.Builder<User>()
                                .setAuthenticator(authenticator)
                                .setPrefix("Bearer")
                                .setUnauthorizedHandler(new UnauthorisedExceptionMapper())
                                .buildAuthFilter()
                )
        );
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));


        // API endpoints
        environment.jersey().register(new LoginResource(authenticator));
        environment.jersey().register(new StatAggregationResource(songDataDao, authDao));
        environment.jersey().register(new UploadDataResource(uploadDao));
    }
}