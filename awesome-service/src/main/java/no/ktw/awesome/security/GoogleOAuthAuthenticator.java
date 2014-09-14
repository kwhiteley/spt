/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package no.ktw.awesome.security;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;

/**
 *
 * @author KellyATWhiteley
 */
public class GoogleOAuthAuthenticator implements Authenticator<OAuthCredentialsResponse, UserInfo>{

    @Override
    public Optional<UserInfo> authenticate(OAuthCredentialsResponse c) throws AuthenticationException {
        
        System.out.println(c.token + " " + c.tokenSecret);
        
        return Optional.of(new UserInfo());
    }

    
    
}
