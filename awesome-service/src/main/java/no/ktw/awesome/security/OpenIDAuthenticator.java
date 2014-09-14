/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package no.ktw.awesome.security;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;

/**
 *
 * @author KellyATWhiteley
 */
public class OpenIDAuthenticator implements Authenticator<OpenIDCredentials, User> {

  @Override
  public Optional<User> authenticate(OpenIDCredentials credentials) throws AuthenticationException {

    // Get the User referred to by the API key
    Optional<User> user = InMemoryUserCache
      .INSTANCE
      .getBySessionToken(credentials.getSessionToken());
    if (!user.isPresent()) {
      return Optional.absent();
    }

    // Check that their authorities match their credentials
    if (!user.get().hasAllAuthorities(credentials.getRequiredAuthorities())) {
      return Optional.absent();
    }

    return user;

  }

}
