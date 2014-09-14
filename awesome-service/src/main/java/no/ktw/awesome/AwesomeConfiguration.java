package no.ktw.awesome;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Configuration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ktw
 */
public class AwesomeConfiguration extends Configuration {

    /**
   * The cookie name for the session token
   */
  public static final String SESSION_TOKEN_NAME ="OpenIDDemo-Session";
    
    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }

    public Properties getOAuthCfgProperties() {
        Properties properties = new Properties();
        for (OAuthCfgClass oauth : oauthCfg) {
            properties.put(oauth.getPrefix() + ".consumer_key",
                    oauth.getKey());
            properties.put(oauth.getPrefix() + ".consumer_secret",
                    oauth.getSecret());
            if (oauth.getPermissions() != null) {
                properties.put(oauth.getPrefix() + ".custom_permissions",
                        oauth.getPermissions());
            }
        }
        if (oauthCustomCfg != null) {
            // add any custom config strings
            properties.putAll(oauthCustomCfg);
        }
        return properties;
    }

    public static class OAuthCfgClass {

        @JsonProperty
        private String url;
        @JsonProperty
        private String name;
        @JsonProperty
        private String prefix;
        @JsonProperty
        private String key;
        @JsonProperty
        private String secret;
        @JsonProperty
        private String permissions;

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getKey() {
            return key;
        }

        public String getSecret() {
            return secret;
        }

        public String getPermissions() {
            return permissions;
        }
    }
    
    @JsonProperty
    @JsonDeserialize(contentAs = OAuthCfgClass.class)
    private List<OAuthCfgClass> oauthCfg;

    public List<OAuthCfgClass> getOAuthCfg() {
        return oauthCfg;
    }

    @JsonProperty
    private HashMap<String, String> oauthCustomCfg = null;

    public Map<String, String> OAuthCustomCfg() {
        return oauthCustomCfg;
    }
}
