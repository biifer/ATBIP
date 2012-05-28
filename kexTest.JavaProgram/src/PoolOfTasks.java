import java.util.Date;
import java.util.LinkedList;


public class PoolOfTasks {
	private LinkedList<byte[]> taskPool = new LinkedList<byte[]>();
	private int ittr = 0;
	private long time = new Date().getTime();

	public synchronized void addTask(byte[] task){

		taskPool.addLast(task);
		notify();

	}
	public synchronized byte[] getTask() throws InterruptedException{
		
		if(taskPool.isEmpty()){
			if(ittr > 0){
			System.out.println("[" + Thread.currentThread().getId() + "], Time to finish:"  + (new Date().getTime() - time));
			}
			this.wait();
		}
		byte[] task = taskPool.getFirst();
		taskPool.removeFirst();
		if(ittr==0){
			time = new Date().getTime();
		}
		ittr++;
		return task;
	}

}