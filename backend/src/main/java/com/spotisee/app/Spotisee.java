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
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.ManagedDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.text.StringSubstitutor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spotisee extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new Spotisee().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        Dotenv dotenv = Dotenv.configure().load();
        Map<String, String> envMap = new HashMap<>();

        for (String property : List.of("DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_PASSWORD")) {
            envMap.put(property, dotenv.get(property));
        }


        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new StringSubstitutor(envMap, "${", "}"))
        );
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
        SongMetaDataDao songMetaDataDao = jdbi.onDemand(SongMetaDataDao.class);

        // Managers
        UploadDataManager uploadDataManager = new UploadDataManager(uploadDao, authDao);
        MusicDataManager musicDataManager = new MusicDataManager(songDataDao);
        UserValidationManager userValidationManager = new UserValidationManager(authDao);
        SelectionManager selectionManager = new SelectionManager(selectionDao);
        GraphingManager graphingManager = new GraphingManager(graphingDao, selectionManager);
        SongMetaDataManager songMetaDataManager = new SongMetaDataManager(songMetaDataDao);

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
        environment.jersey().register(new UploadDataResource(uploadDataManager, userValidationManager));
        environment.jersey().register(new SelectionResource(selectionManager, userValidationManager));
        environment.jersey().register(new GraphingResource(graphingManager, userValidationManager));
        environment.jersey().register(new SongMetaDataResource(songMetaDataManager, userValidationManager));
    }
}