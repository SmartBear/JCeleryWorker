package org.loadui.jcelery;

import com.google.common.util.concurrent.Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.loadui.jcelery.test.OsgiTestUtils.assertAllBundlesActive;
import static org.loadui.jcelery.test.OsgiTestUtils.springDmBundles;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * @author renato
 */
@RunWith( PaxExam.class )
@ExamReactorStrategy( PerClass.class )
public class JCeleryServiceTest
{

	@Inject
	BundleContext context;

	@Inject
	CeleryService celeryService;

	@Configuration
	public Option[] config()
	{
		return options(
				systemPackages(
						"com.sun.crypto.provider",
						"com.sun.net.ssl",
						"com.sun.net.ssl.internal.ssl",
						"org.w3c.dom.traversal",
						"javax.transaction.xa",
						"sun.io",
						"sun.misc" ),
				springDmBundles(),
				junitBundles(),
				mavenBundle( "com.google.guava", "guava", "16.0" ),
				mavenBundle( "org.loadui", "jcelery", "0.1.0" )
		);
	}

	@Test
	public void allBundlesAreActive()
	{
		assertAllBundlesActive( context );
	}

	@Test
	public void celeryServiceIsExposed()
	{
		assertThat( celeryService, notNullValue() );
		assertThat( celeryService.state(), is( Service.State.NEW ) );
	}

}
