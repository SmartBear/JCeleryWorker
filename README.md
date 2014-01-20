JCeleryWorker
=============

A very simple [Celery][1] worker for the JVM.

Currently only supports RabbitMQ and JSON.


## Usage
```java
CeleryConsumer celeryConsumer = new CeleryConsumer();
celeryConsumer.setTaskHandler( new TaskHandler() {
	@Override
	public void handle(CeleryTask e) {
		System.out.println("Received task: " + e);
	}
} );

celeryConsumer.startAsync();
```

[1]: http://www.celeryproject.org/
