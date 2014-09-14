/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package no.ktw.awesome.security;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import no.ktw.awesome.AwesomeConfiguration;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author KellyATWhiteley
 */
/**
 * <p>
 * Resource to provide the following to application:
 * </p>
 * <ul>
 * <li>OAuth authentication handling</li>
 * </ul>
 * 
 * @since 0.0.1
 */
@Path("/oauth")
@Produces(MediaType.TEXT_HTML)
public class PublicOAuthResource  {

    private static final Logger log = LoggerFactory
            .getLogger(PublicOAuthResource.class);

    private final AwesomeConfiguration config;
    
    /**
     * Default constructor
     */
    public PublicOAuthResource(AwesomeConfiguration config) {
        this.config = config;
    }

    @GET
    @Timed
    @Path("/request")
    public Response requestOAuth(@Context HttpServletRequest request,
            @QueryParam("provider") String provider) throws URISyntaxException {
        if (provider == null) {
            log.debug("Missing provider ID");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        // instantiate SocialAuth for this provider type and tuck into session
//        List<OAuthCfgClass> oauthCfg = OpenIDDemoService.getConfig()
//                .getOAuthCfg();
//        if (oauthCfg != null) {
            // get the authentication URL for this provider
            try {
                SocialAuthManager manager = getSocialAuthManager();
                request.getSession().setAttribute("authManager", manager);

                java.net.URI url = new URI(manager.getAuthenticationUrl(
                        provider, "http://localhost:8080/oauth/verify"));
                log.debug("OAuth Auth URL: {}", url);
                return Response.temporaryRedirect(url).build();
            } catch (Exception e) {
                log.error("SocialAuth error: {}", e);
            }
//        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    /**
     * Handles the OAuth server response to the earlier AuthRequest
     * 
     * @return The OAuth identifier for this user if verification was
     *         successful
     */
    @GET
    @Timed
    @Path("/verify")
    public Response verifyOAuthServerResponse(
            @Context HttpServletRequest request) {

        // this was placed in the session in the /request resource        
        SocialAuthManager manager = (SocialAuthManager) request.getSession()
                .getAttribute("authManager");

        if (manager != null) {
            try {
                // call connect method of manager which returns the provider
                // object
                Map<String, String> params = SocialAuthUtil
                        .getRequestParametersMap(request);
                AuthProvider provider = manager.connect(params);

                // get profile
                Profile p = provider.getUserProfile();

                log.info("Logging in user '{}'", p);

                // at this point, we've been validated, so save off this user's
                // info
                User tempUser = new User(null, UUID.randomUUID());
                tempUser.setOpenIDIdentifier(p.getValidatedId());
                tempUser.setOAuthInfo(provider.getAccessGrant());

                tempUser.setEmailAddress(p.getEmail());
                if ((p.getFirstName() == null) && (p.getLastName() == null)) {
                    // Twitter doesn't return first/last name fields but does include
                    // a fullname property we can use to generate them
                    if (p.getFullName() != null) {
                        String[] parts = p.getFullName().split("-");
                        if (parts.length > 1) {
                            tempUser.setFirstName(parts[0]);
                            tempUser.setLastName(parts[parts.length-1]);
                        } else {
                            tempUser.setFirstName(parts[0]);
                            tempUser.setLastName(parts[0]);
                        }
                    }
                } else {
                    tempUser.setFirstName(p.getFirstName());
                    tempUser.setLastName(p.getLastName());
                }
                tempUser.setUserName(p.getFullName());
                
                // Provide a basic authority in light of successful
                // authentication
                tempUser.getAuthorities().add(Authority.ROLE_PUBLIC);
                // see if this is an admin user (match email addr and provider)
//                if ((OpenIDDemoService.getConfig().getAdminUsers() != null)
//                        && (tempUser.getEmailAddress() != null)) {
//                    Map<String, String> adminUsers = OpenIDDemoService
//                            .getConfig().getAdminUsers();
//                    if (adminUsers.containsKey(tempUser.getEmailAddress())
//                            && (adminUsers.get(tempUser.getEmailAddress())
//                                    .equals(provider.getProviderId()))) {
//                        tempUser.getAuthorities().add(Authority.ROLE_ADMIN);
//                    }
//                }
                if("buddeh@gmail.com".equals(tempUser.getEmailAddress())){
                    tempUser.getAuthorities().add(Authority.ROLE_ADMIN);
                }

                // Search for a pre-existing User matching the temp User
                Optional<User> userOptional = InMemoryUserCache.INSTANCE
                        .getByOpenIDIdentifier(tempUser.getOpenIDIdentifier());
                if (!userOptional.isPresent()) {
                    // Persist the user with the generated session token
                    InMemoryUserCache.INSTANCE.put(tempUser.getSessionToken(),
                            tempUser);

                } else {
                    tempUser = userOptional.get();
                }

                return Response
                        .temporaryRedirect(new URI("/game/admin"))
                        .header("Authorization", "Bearer " + tempUser.getSessionToken())
                        .cookie(replaceSessionTokenCookie(Optional.of(tempUser)))
                        .build();
            } catch (Exception e1) {
                log.error("Error reading profile info: {}, {}", e1.getMessage(), e1.getCause().getMessage());
            }
        }

        // Must have failed to be here
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    /**
     * Gets an initialized SocialAuthManager
     * @return gets an initialized SocialAuthManager
     */
    private SocialAuthManager getSocialAuthManager() {
        SocialAuthConfig config = SocialAuthConfig.getDefault();
        try {
            config.load(this.config.getOAuthCfgProperties());
            SocialAuthManager manager = new SocialAuthManager();
            manager.setSocialAuthConfig(config);
            return manager;
        } catch (Exception e) {
            log.error("SocialAuth error: " + e);
        }
        return null;
    }

//      /**
//   * @param user A user with a session token. If absent then the cookie will be removed.
//   *
//   * @return A cookie with a long term expiry date suitable for use as a session token for OpenID
//   */
  private NewCookie replaceSessionTokenCookie(Optional<User> user) {

    if (user.isPresent()) {

      String value = user.get().getSessionToken().toString();

      log.debug("Replacing session token with {}", value);

      return new NewCookie(
        AwesomeConfiguration.SESSION_TOKEN_NAME,
        value,   // Value
        "/",     // Path
        null,    // Domain
        null,    // Comment
        86400 * 30, // 30 days
        false);
    } else {
      // Remove the session token cookie
      log.debug("Removing session token");

      return new NewCookie(
        AwesomeConfiguration.SESSION_TOKEN_NAME,
        null,   // Value
        null,    // Path
        null,   // Domain
        null,   // Comment
        0,      // Expire immediately
        false);
    }
  }
    
}