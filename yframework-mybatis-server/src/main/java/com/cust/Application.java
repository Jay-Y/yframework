package com.cust;

import com.becypress.toolkit.Becypress;
import com.cust.common.config.ApplicationProperties;
import com.cust.common.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricsDropwizardAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

@ComponentScan({
        "com.becypress", "${application.biz.package-name}.security", "${application.biz.package-name}.common", "${application.biz.package-name}.system", "${application.biz.package-name}.biz", "${application.biz.package-name}"
})
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class, MetricsDropwizardAutoConfiguration.class})
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
//@EnableDiscoveryClient
public class Application
{
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Environment env;

    public Application(Environment env)
    {
        this.env = env;
    }

    /**
     * Initializes server.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="http://jhipster.github.io/profiles/">http://jhipster.github.io/profiles/</a>.
     */
    @PostConstruct
    public void init()
    {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION))
        {
            log.error("You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(Constants.SPRING_PROFILE_CLOUD))
        {
            log.error("You have misconfigured your application! It should not" + "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved into an address
     */
    public static void main(String[] args) throws UnknownHostException
    {
        Becypress.UTIL.springboot().startup(Application.class, args);
    }
}