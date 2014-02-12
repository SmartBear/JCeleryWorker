package org.loadui.jcelery;

import com.google.common.util.concurrent.Service;

/**
 * @author renato
 */
public interface JobService extends Service
{

	void setTaskHandler( TaskHandler<?> handler );

}
