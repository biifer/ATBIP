import java.util.LinkedList;


public class PoolOfTasks {
	private LinkedList<byte[]> taskPool = new LinkedList<byte[]>();



	public synchronized void addTask(byte[] task){

		taskPool.addLast(task);
		notify();

	}
	public synchronized byte[] getTask() throws InterruptedException{
		
		if(taskPool.isEmpty()){
			this.wait();
		}
		byte[] task = taskPool.getFirst();
		taskPool.removeFirst();
		
		return task;
	}

}