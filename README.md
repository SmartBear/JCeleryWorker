JCeleryWorker
=============

A very simple [Celery][1] worker for the JVM.

Currently only supports RabbitMQ and JSON.


## Usage
This replaces the worker from the [First Steps with Celery][2] tutoral:

```java
public class DemoApp {
	public static void main(String[] _) throws Exception {

		CeleryService celeryService = new CeleryService("localhost");
		celeryService.setTaskHandler( new TaskHandler() {
			@Override
			public void handle(CeleryTask t) throws IOException
			{
				switch(t.task) {
					case "tasks.add": t.complete(SUCCESS, add(t));
				}
			}
		} );

		celeryService.startAsync();
		celeryService.awaitRunning();

		Thread.sleep(300000);

		celeryService.stopAsync();
		celeryService.awaitTerminated();
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
