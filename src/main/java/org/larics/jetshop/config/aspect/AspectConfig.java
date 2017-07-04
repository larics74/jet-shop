package org.larics.jetshop.config.aspect;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/*
 * Aspect configuration.
 * 
 * @author Igor Laryukhin
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages="org.larics.jetshop")
public class AspectConfig {

}
