package no.ktw.awesome;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Configuration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ktw
 */
public class AwesomeConfiguration extends Configuration{
    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }
}
