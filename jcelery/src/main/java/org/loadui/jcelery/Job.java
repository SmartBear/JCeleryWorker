package org.loadui.jcelery;

import java.io.IOException;
import java.util.List;

/**
 * @author renato
 */
public interface Job
{
	void complete( Status status, Object response ) throws IOException;

	void complete( Status status ) throws IOException;

	String getId();

	String getMethod();

	List getArgs();

	public enum Status
	{
		PENDING, STARTED, RETRY, FAILURE, SUCCESS, REVOKED
	}
}
