package cz.drekorian.android.linuxcontributors;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter that inflates list row with contributor's avatar and login.
 * 
 * @author Marek Osvald
 * @version 2015.1401
 */
public class ContributorAdapter extends ArrayAdapter<Contributor> {

	/**
	 * Parametric constructor.
	 * 
	 * @param context application context
	 * @param objects list of contributors to inflate
	 */
	public ContributorAdapter(Context context, List<Contributor> objects) {
		super(context, R.layout.list_row_contributor, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Contributor contributor = getItem(position);

		View view = convertView;

		if (null == convertView) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.list_row_contributor, parent, false);
		}

		TextView tvwLogin = (TextView) view.findViewById(R.id.list_row_contributor_tvwLogin);
		ImageView ivwAvatar = (ImageView) view.findViewById(R.id.list_row_contributor_ivwAvatar);

		LoadImageTask loadImageTask = new LoadImageTask(ivwAvatar, contributor.getAvatarUrl());
		AsyncEngine.execute(loadImageTask);

		tvwLogin.setText(contributor.getLogin());
		view.setTag(contributor);

		return view;
	}

	/**
	 * Asynchronous task that loads an avatar image from the Internet.
	 * 
	 * @author Marek Osvald
	 * @version 2015.0114
	 */
	class LoadImageTask extends AsyncTask<Void, Void, Boolean> {

		private static final String	TAG	= "LinuxContributors ContributorAdapter LoadImageTask";

		private ImageView			imageView;
		private String				avatarUrl;
		private Bitmap				bitmap;

		/**
		 * Parametric constructor. Sets imageView to display the bitmap in and the contributor's URL.
		 * 
		 * @param imageView ImageView to show the bitmap in
		 * @param avatarUrl contributor's avatar URL
		 */
		public LoadImageTask(ImageView imageView, String avatarUrl) {
			this.imageView = imageView;
			this.avatarUrl = avatarUrl;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			imageView.setImageBitmap(null); // resets the avatar ImageView before it loads the new one 
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(avatarUrl);
			HttpResponse response = null;
			try {
				response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 10; // shrinks the bitmap ten times
					bitmap = BitmapFactory.decodeStream(response.getEntity().getContent(), null, options);
					return Boolean.TRUE;
				}
			} catch (IOException ex) {
				Log.e(TAG, ex.getMessage());
				ex.printStackTrace();
			}

			return Boolean.FALSE;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result.equals(Boolean.TRUE)) {
				imageView.setImageBitmap(bitmap);
			}
		}

	}

}
