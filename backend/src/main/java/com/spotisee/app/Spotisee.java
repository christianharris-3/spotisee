package com.spotisee.app;

import com.spotisee.app.config.AppConfiguration;
import com.spotisee.app.config.MySqlLogger;
import com.spotisee.app.config.SpotiseeAuthenticator;
import com.spotisee.app.dao.*;
import com.spotisee.app.exceptions.mapper.UnauthorisedExceptionMapper;
import com.spotisee.app.managers.*;
import com.spotisee.app.models.User;
import com.spotisee.app.resources.*;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
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
        SelectionDao selectionDao = jdbi.onDemand(SelectionDao.class);
        GraphingDao graphingDao = jdbi.onDemand(GraphingDao.class);

        // Managers
        UploadDataManager uploadDataManager = new UploadDataManager(uploadDao);
        MusicDataManager musicDataManager = new MusicDataManager(songDataDao);
        UserValidationManager userValidationManager = new UserValidationManager(authDao);
        SelectionManager selectionManager = new SelectionManager(selectionDao);
        GraphingManager graphingManager = new GraphingManager(graphingDao, selectionManager);

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
        environment.jersey().register(new StatAggregationResource(musicDataManager, userValidationManager));
        environment.jersey().register(new UploadDataResource(uploadDataManager));
        environment.jersey().register(new SelectionResource(selectionManager, userValidationManager));
        environment.jersey().register(new GraphingResource(graphingManager, userValidationManager));
    }
}