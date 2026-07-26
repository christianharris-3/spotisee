package com.spotisee.app;

import com.spotisee.app.config.AppConfiguration;
import com.spotisee.app.resources.LoginResource;
import com.spotisee.app.resources.UploadDataResource;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;

public class Spotisee extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new Spotisee().run(args);
    }

    @Override
    public void run(AppConfiguration configuration,
                    Environment environment) {

        environment.jersey().register(new LoginResource());
        environment.jersey().register(new UploadDataResource());
    }
}