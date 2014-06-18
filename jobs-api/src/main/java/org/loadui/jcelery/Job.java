package org.loadui.jcelery;

import java.io.IOException;
import java.util.List;

/**
 * @author renato
 */
public interface Job
{
	void complete();
	void complete( Object message );

	void revoke();

	void start();

	void fail( String reason );

	boolean isInProgress();

	String getId();

	String getMethod();

	List getArgs();

	public enum Status
	{
		PENDING, STARTED, RETRY, FAILURE, SUCCESS, REVOKED
	}
}
