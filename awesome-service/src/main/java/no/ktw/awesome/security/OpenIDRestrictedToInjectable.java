/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ktw.awesome.security;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import no.ktw.awesome.AwesomeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author KellyATWhiteley
 */
public class OpenIDRestrictedToInjectable<T> extends AbstractHttpContextInjectable<T> {

    private static final Logger log = LoggerFactory.getLogger(OpenIDRestrictedToInjectable.class);

    private final Authenticator<OpenIDCredentials, T> authenticator;
    private final String realm;
    private final Set<Authority> requiredAuthorities;

    /**
     * @param authenticator The Authenticator that will compare credentials
     * @param realm The authentication realm
     * @param requiredAuthorities The required authorities as provided by the
     * RestrictedTo annotation
     */
    OpenIDRestrictedToInjectable(
            Authenticator<OpenIDCredentials, T> authenticator,
            String realm,
            Authority[] requiredAuthorities) {
        this.authenticator = authenticator;
        this.realm = realm;
        this.requiredAuthorities = Sets.newHashSet(Arrays.asList(requiredAuthorities));
    }

    public Authenticator<OpenIDCredentials, T> getAuthenticator() {
        return authenticator;
    }

    public String getRealm() {
        return realm;
    }

    public Set<Authority> getRequiredAuthorities() {
        return requiredAuthorities;
    }

    @Override
    public T getValue(HttpContext httpContext) {
        try {
            // Get the Authorization header
            final Map<String, Cookie> cookieMap = httpContext.getRequest().getCookies();
            if (!cookieMap.containsKey(AwesomeConfiguration.SESSION_TOKEN_NAME)) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            UUID sessionToken = UUID.fromString(cookieMap.get(AwesomeConfiguration.SESSION_TOKEN_NAME).getValue());

            if (sessionToken != null) {
 
               final OpenIDCredentials credentials = new OpenIDCredentials(sessionToken, requiredAuthorities);

                final Optional<T> result = authenticator.authenticate(credentials);
                if (result.isPresent()) {
                    return result.get();
                }
            }
        } catch (IllegalArgumentException e) {
            log.debug("Error decoding credentials", e);
        } catch (AuthenticationException e) {
            log.warn("Error authenticating credentials", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        // Must have failed to be here
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
}

//    try {
//
//      // Get the Authorization header
//      final Map<String,Cookie> cookieMap = httpContext.getRequest().getCookies();
//      if (!cookieMap.containsKey(AppConfiguration.SESSION_TOKEN_NAME)) {
//        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
//      }
//
//      UUID sessionToken = UUID.fromString(cookieMap.get(AppConfiguration.SESSION_TOKEN_NAME).getValue());
//
//      if (sessionToken != null) {
//
//        final OAuthCredentialsResponse credentials = new OAuthCredentialsResponse(sessionToken, requiredAuthorities);
//
//        final Optional<T> result = authenticator.authenticate(credentials);
//        if (result.isPresent()) {
//          return result.get();
//        }
//      }
//    } catch (IllegalArgumentException e) {
//      log.debug("Error decoding credentials",e);
//    } catch (AuthenticationException e) {
//      log.warn("Error authenticating credentials",e);
//      throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
//    }
//
//    // Must have failed to be here
//    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
//  }
