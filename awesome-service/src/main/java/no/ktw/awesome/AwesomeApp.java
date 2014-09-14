package no.ktw.awesome;

import com.sun.jersey.api.client.Client;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.client.JerseyClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import no.ktw.awesome.resource.AwesomeResource;
import no.ktw.awesome.security.OpenIDAuthenticator;
import no.ktw.awesome.security.OpenIDRestrictedToProvider;
import no.ktw.awesome.security.PublicOAuthResource;
import no.ktw.awesome.security.User;
import org.eclipse.jetty.server.session.SessionHandler;

/**
 *
 * @author ktw
 */
public class AwesomeApp extends Service<AwesomeConfiguration> {

    public static void main(String[] args) throws Exception {
        new AwesomeApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<AwesomeConfiguration> bootstrap) {
        bootstrap.setName("awesome-app");
    }

    @Override
    public void run(AwesomeConfiguration config, Environment environment) throws Exception {

        final Client client = new JerseyClientBuilder().using(config.getJerseyClientConfiguration())
                .using(environment)
                .build();
        
        environment.addResource(new AwesomeResource(client));
        environment.addResource(new PublicOAuthResource(config));
        
        environment.setSessionHandler(new SessionHandler());
        
        // Configure authenticator
        OpenIDAuthenticator authenticator = new OpenIDAuthenticator();
        
        environment.addProvider(new OpenIDRestrictedToProvider<User>(
                authenticator, "OpenID"));        
//        environment.addProvider(new OAuthProvider<UserInfo>());
        
//        GoogleAuthExample example = new GoogleAuthExample();
    }

}
