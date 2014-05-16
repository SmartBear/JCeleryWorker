package org.loadui.jcelery;

import org.junit.Test;
import org.ops4j.pax.exam.options.UrlProvisionOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class MavenBundleResolverTest
{

	@Test
	public void shouldResolveFileUnderExpectedTargetDirectory() throws Exception
	{
		ensureRunningInJCeleryDirectory();

		Path tempDirectory = createTempDirectory();
		Path tempFile = tempDirectory.resolve( "myBundle-1.0-SNAPSHOT.jar" );
		boolean createOk = tempFile.toFile().createNewFile();
		assertThat( createOk, is( true ) );

		UrlProvisionOption options = MavenBundleResolver.resolveJar( tempDirectory, "myBundle" );

		assertNotNull( options );
	}

	private Path createTempDirectory() throws IOException
	{
		Path tempDirectory = Files.createTempDirectory(
				Paths.get( "jcelery-test", "target" ), "MavenBundleResolverTest" );
		assertThat( tempDirectory.toFile().exists(), is( true ) );
		return tempDirectory;
	}

	private void ensureRunningInJCeleryDirectory()
	{
		assertThat( Paths.get( "" ).toAbsolutePath().toString(), endsWith( "jcelery" ) );
	}

}
