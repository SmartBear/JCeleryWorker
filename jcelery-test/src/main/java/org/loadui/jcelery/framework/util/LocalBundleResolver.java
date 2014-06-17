package org.loadui.jcelery.framework.util;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is only necessary here because Pax Exam does not seem to be able to resolve bundles in our CI server
 * (Jenkins), where the Maven repo is kept private and makes use of Maven profiles to isolate builds
 */
public class LocalBundleResolver
{
	static final Logger log = LoggerFactory.getLogger( LocalBundleResolver.class );

	public static UrlProvisionOption jCeleryCore()
	{
		return resolveJar( findTargetDirectory( "jcelery" ), "jcelery" );
	}

	public static UrlProvisionOption jobsApi()
	{
		return resolveJar( findTargetDirectory( "jobs-api" ), "jobs-api" );
	}

	protected static Path findTargetDirectory( String module )
	{
		Path jCeleryDir = attemptToLocateJCeleryDirectoryFrom( testWorkingDirectory(), true );
		if( jCeleryDir != null )
		{
			return jCeleryDir.resolve( Paths.get( module, "target" ) );
		}
		else
		{
			throw new RuntimeException( "Could not guess where the JCelery working directory is" );
		}
	}

	protected static UrlProvisionOption resolveJar( Path targetFolder, final String nameStart )
	{
		File[] candidateFiles = targetFolder.toFile().listFiles( new FileFilter( nameStart ) );
		if( candidateFiles != null && candidateFiles.length > 0 )
		{
			try
			{
				return CoreOptions.bundle( candidateFiles[0].toURI().toURL().toString() );
			}
			catch( MalformedURLException e )
			{
				log.warn( "Problem resolving bundle " + nameStart, e );
			}
		}
		throw new RuntimeException( "Could not resolve project bundle with name starting with " + nameStart );
	}

	private static Path attemptToLocateJCeleryDirectoryFrom( Path workingDir, boolean lookIntoChildren )
	{
		if( !workingDir.toFile().isDirectory() )
		{
			return null;
		}
		String workingDirName = workingDir.getFileName().toString();

		log.debug( "WorkingDir is {}", workingDirName );

		if( workingDirName.equals( "jcelery-test" ) )
		{
			return workingDir.getParent();
		}
		if( workingDirName.equals( "jcelery" ) )
		{
			return workingDir;
		}
		File[] children = workingDir.toFile().listFiles();
		if( children != null )
		{
			for( File child : children )
			{
				Path found = attemptToLocateJCeleryDirectoryFrom( child.toPath(), false );
				if( found != null )
					return found;
			}
		}
		return null;
	}

	private static Path testWorkingDirectory()
	{
		return Paths.get( "" ).toAbsolutePath();
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
