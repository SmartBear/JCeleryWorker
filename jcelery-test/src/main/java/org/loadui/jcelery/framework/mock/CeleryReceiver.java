package org.loadui.jcelery.framework.mock;

public interface CeleryReceiver
{
	/**
	 * Will wait for the next message for three seconds by default, otherwise return null..
	 * If other timeout is needed use nextMessage( Integer X ).
	 *
	 * @return
	 */
	public String nextDataMessage();

	/**
	 * Will await message for @timeoutInSeconds seconds, otherwise return null.
	 *
	 * @param timeoutInSeconds
	 * @return
	 */
	public String nextDataMessage( int timeoutInSeconds );

	/**
	 * Waits for the next control message that contains the given String
	 *
	 * @param timeoutInSeconds
	 * @param containingString
	 */
	public void waitForControlMessage( int timeoutInSeconds, String containingString );

	public boolean hasControlMessage();

	public boolean hasDataMessage();

	public void clearControl();

	public void clearData();
}
