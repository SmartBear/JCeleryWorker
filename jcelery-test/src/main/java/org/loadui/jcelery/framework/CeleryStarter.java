package org.loadui.jcelery.framework;

import java.net.URL;

public interface CeleryStarter
{
	/**
	 * Emulates a start job
	 * @param id by any id.
	 */
	public void startJob( String id );

	public void startJob( String id, URL json );
}
