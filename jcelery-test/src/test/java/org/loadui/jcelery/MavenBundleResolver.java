package org.loadui.jcelery;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.nio.channels.UnresolvedAddressException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * This class is only necessary here because Pax Exam does not seem to be able to resolve bundles in our CI server
 * (Jenkins), where the Maven repo is kept private and makes use of Maven profiles to isolate builds
 */
public class MavenBundleResolver
{
	static final Logger log = LoggerFactory.getLogger( MavenBundleResolver.class );

	public static UrlProvisionOption jCeleryCore()
	{
		Path targetFolder = Paths.get( "jcelery", "target" );
		return resolveJar( targetFolder, "jcelery" );
	}

	public static UrlProvisionOption jobsApi()
	{
		Path targetFolder = Paths.get( "jobs-api", "target" );
		return resolveJar( targetFolder, "jobs-api" );
	}

	protected static UrlProvisionOption resolveJar( Path targetFolder, final String nameStart )
	{
		System.out.println( "Target folder: " + targetFolder.toAbsolutePath() );
		File[] candidateFiles = targetFolder.toFile().listFiles( new FileFilter( nameStart ) );
		System.out.println( "Found files: " + Arrays.toString( candidateFiles ) );
		if( candidateFiles != null && candidateFiles.length > 0 )
		{
			try
			{
				return CoreOptions.bundle( candidateFiles[0].toURI().toURL().toString() );
			}
			catch( MalformedURLException e )
			{
				System.out.println("Shit happended: " + e);
				log.warn( "Problem resolving bundle " + nameStart, e );
			}
		}
		throw new UnresolvedAddressException();
	}


	private static class FileFilter implements FilenameFilter
	{
		private final String nameStart;

		FileFilter( String nameStart )
		{
			this.nameStart = nameStart;
		}

		@Override
		public boolean accept( File dir, String name )
		{
			return name.startsWith( nameStart ) && name.endsWith( ".jar" );
		}

	}

}
