package cz.drekorian.android.linuxcontributors;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Main application activity.
 * 
 * @author Marek Osvald
 * @version 2015.0114
 */
public class MainActivity extends Activity {

	private LoadDataTask		loadDataTask;
	private Dialog				loadingDialog	= null;
	private List<Contributor>	contributors;
	private ListView			lvwContributors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lvwContributors = (ListView) findViewById(R.id.main_activity_lvwContributors);
		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				loadData();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void loadData() {
		if (null != loadDataTask) {
			loadDataTask.cancel(true);
		}

		loadDataTask = new LoadDataTask();
		AsyncEngine.execute(loadDataTask);
	}

	class LoadDataTask extends AsyncTask<Void, Void, Boolean> {

		private static final String	TAG						= "LinuxContributors MainActivity LoadDataTask";
		private static final String	URL_GITHUB_CONTRIBUTORS	= "https://api.github.com/repos/torvalds/linux/contributors";
		private static final String	JSON_KEY_LOGIN			= "login";
		private static final String	JSON_KEY_URL			= "html_url";
		private static final String	JSON_KEY_AVATAR_URL		= "avatar_url";

		private JSONArray			dataArray;

		@Override
		protected void onPreExecute() {
			if (null != loadingDialog) {
				loadingDialog.dismiss();
			}

			loadingDialog = ProgressDialog.show(
				MainActivity.this,
				getString(R.string.activity_main_loadingDialog_title),
				getString(R.string.activity_main_loadingDialog_message),
				true,
				true);

			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(URL_GITHUB_CONTRIBUTORS);
			HttpResponse response = null;
			try {
				response = client.execute(get);

				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONParser jsonParser = new JSONParser();
					InputStreamReader inputStreamReader = new InputStreamReader(response.getEntity().getContent());
					dataArray = (JSONArray) jsonParser.parse(inputStreamReader);
					return Boolean.TRUE;
				}

			} catch (IOException | ParseException ex) {
				Log.e(TAG, ex.getMessage());
				ex.printStackTrace();
			}

			return Boolean.FALSE;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			loadingDialog.dismiss();

			if (result.equals(Boolean.TRUE)) {
				contributors = new LinkedList<Contributor>();

				for (Object object: dataArray) {
					JSONObject jsonObject = (JSONObject) object;

					String login = (String) jsonObject.get(JSON_KEY_LOGIN);
					String url = (String) jsonObject.get(JSON_KEY_URL);
					String avatarUrl = (String) jsonObject.get(JSON_KEY_AVATAR_URL);

					Contributor contributor = new Contributor(login, url, avatarUrl);
					contributors.add(contributor);
					Log.d(TAG, "Parsed: " + contributor);
				}

				lvwContributors.setAdapter(new ContributorAdapter(MainActivity.this, contributors));
				lvwContributors.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Contributor contributor = (Contributor) view.getTag();
						String url = contributor.getUrl();
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(intent);
					}

				});

			} else {
				AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.activity_main_failureDialog_title)
					.setMessage(R.string.activity_main_failureDialog_message)
					.setCancelable(true)
					.setPositiveButton(android.R.string.ok, null)
					.create();

				dialog.show();
			}
		}

	}

}
