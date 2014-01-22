package org.loadui.jcelery.test;

import org.ops4j.pax.exam.Option;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;

/**
 * @author renato
 */
public class OsgiTestUtils
{

	public static Option springDmBundles()
	{
		final String springVersion = "3.1.1.RELEASE";
		final String osgiSpringVersion = "1.2.1";
		return provision(
				mavenBundle( "org.aopalliance", "com.springsource.org.aopalliance", "1.0.0" ),
				mavenBundle( "org.slf4j", "jcl-over-slf4j", "1.6.1" ),
				mavenBundle( "org.springframework", "spring-context", springVersion ),
				mavenBundle( "org.springframework", "spring-expression", springVersion ),
				mavenBundle( "org.springframework", "spring-beans", springVersion ),
				mavenBundle( "org.springframework", "spring-aop", springVersion ),
				mavenBundle( "org.springframework", "spring-asm", springVersion ),
				mavenBundle( "org.springframework", "spring-core", springVersion ),
				mavenBundle( "org.springframework.osgi", "spring-osgi-io", osgiSpringVersion ),
				mavenBundle( "org.springframework.osgi", "spring-osgi-core", osgiSpringVersion ),
				mavenBundle( "org.springframework.osgi", "spring-osgi-extender", osgiSpringVersion )
		);
	}

	public static void assertAllBundlesActive( BundleContext context )
	{
		assertNotNull( context );
		for( Bundle bundle : context.getBundles() )
		{
			assertSame( Bundle.ACTIVE, bundle.getState() );
		}
	}

}
