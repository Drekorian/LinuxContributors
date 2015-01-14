package cz.drekorian.android.linuxcontributors;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

/**
 * @author Marek Osvald
 * @version 2015.0114
 */
public class AsyncEngine {

	/**
	 * Default constructor. Private in order to prevent instantiation.
	 */
	private AsyncEngine() {
	}
	
	/**
	 * Runs AsyncTask in parallel execution thread. 
	 * 
	 * @param asyncTask AsyncTask to be run
	 * @param params AsyncTask parameter to be set
	 */
	@SuppressWarnings("unchecked")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <T> void execute(AsyncTask<T, ?, ?> asyncTask, T... params) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			asyncTask.execute(params);
		}
	}
	
}
