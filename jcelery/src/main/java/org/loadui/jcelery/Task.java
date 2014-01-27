package org.loadui.jcelery;

import java.io.IOException;

/**
 * @author renato
 */
public interface Task
{

	void complete( Status status ) throws IOException;

	void complete( Status status, Object response ) throws IOException;

}
