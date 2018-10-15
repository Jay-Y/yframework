package org.yframework.ddd.template.si.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.yframework.ddd.template.si.springboot.config.logging.LoggingAspect;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration
{
    @Bean
    @Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
    public LoggingAspect loggingAspect(Environment env)
    {
        return new LoggingAspect(env);
    }
}
