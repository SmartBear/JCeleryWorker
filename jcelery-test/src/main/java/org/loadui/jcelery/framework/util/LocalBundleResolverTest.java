package org.loadui.jcelery.framework.util;

import org.junit.Test;
import org.ops4j.pax.exam.options.UrlProvisionOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.loadui.jcelery.framework.util.LocalBundleResolver.findTargetDirectory;

public class LocalBundleResolverTest
{

	@Test
	public void shouldResolveFileUnderExpectedTargetDirectory() throws Exception
	{
		Path tempDirectory = createTempDirectory();
		Path tempFile = tempDirectory.resolve( "myBundle-1.0-SNAPSHOT.jar" );
		boolean createOk = tempFile.toFile().createNewFile();
		assertThat( createOk, is( true ) );

		UrlProvisionOption options = LocalBundleResolver.resolveJar( tempDirectory, "myBundle" );

		assertNotNull( options );
	}

	@Test( expected = RuntimeException.class )
	public void throwsRuntimeExceptionIfCannotResolveJar() throws Exception
	{
		Path tempDirectory = createTempDirectory();
		Path tempFile = tempDirectory.resolve( "myBundle-1.0-SNAPSHOT.jar" );
		boolean createOk = tempFile.toFile().createNewFile();
		assertThat( createOk, is( true ) );

		LocalBundleResolver.resolveJar( tempDirectory, "wrong" );
	}

	private Path createTempDirectory() throws IOException
	{
		Path tempDirectory = Files.createTempDirectory(
				findTargetDirectory( "jcelery-test" ), "MavenBundleResolverTest" );
		assertThat( tempDirectory.toFile().exists(), is( true ) );
		return tempDirectory;
	}

}
