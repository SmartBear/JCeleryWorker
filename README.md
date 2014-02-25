JCeleryWorker
=============

A very simple [Celery][1] worker for Java.

Currently only supports RabbitMQ and JSON.


## Usage
This replaces the worker from the [First Steps with Celery][2] tutoral:

```java
public class DemoApp {
	public static void main(String[] _) throws Exception {
		JobService celeryService = new CeleryService();
      		celeryService.setInvokeHandler( new TaskHandler<InvokeJob>()
      		{
      			@Override
      			public void handle( InvokeJob t ) throws IOException
      			{
      				switch( t.getMethod() )
      				{
      					case "tasks.add":
      						t.complete( Job.Status.SUCCESS, add( t ) );
      				}
      			}
      		} );

      		celeryService.setRevokeHandler( new TaskHandler<InvokeJob>()
      		{
      			@Override
      			public void handle( InvokeJob t ) throws IOException
      			{
      				switch( t.getMethod() )
      				{
      					case "revoke":
      						t.complete( Job.Status.REVOKED );
      				}
      			}
      		} );

      		celeryService.startService();

		// ...
	}

	private static long add(CeleryTask t) {
		long x = (long) t.args.get(0);
		long y = (long) t.args.get(1);
		return x + y;
	}
}

```

[1]: http://www.celeryproject.org/
[2]: http://docs.celeryproject.org/en/latest/getting-started/first-steps-with-celery.html#application
