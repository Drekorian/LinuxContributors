package cz.drekorian.android.linuxcontributors;

/**
 * Simple POJO class that holds basic contributor information.
 * 
 * @author Marek Osvald
 * @version 2015.0114
 */
public class Contributor {

	private String	login;
	private String	url;
	private String	avatarUrl;

	/**
	 * Parametric constructor. Initializes all attributes.
	 * 
	 * @param login login to be set
	 * @param url URL to be set
	 * @param avatarUrl avatar URL to be set
	 */
	public Contributor(String login, String url, String avatarUrl) {
		setLogin(login);
		setUrl(url);
		setAvatarUrl(avatarUrl);
	}

	/**
	 * Returns contributor's login.
	 * 
	 * @return contributor's login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Sets contributor's login
	 * 
	 * @param login contributor's login to be set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Returns contributor's profile URL.
	 * 
	 * @return contributor's profile URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets contributor's profile URL.
	 * 
	 * @param url contributor's profile URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns contributor's avatar URL.
	 * 
	 * @return
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}

	/**
	 * Sets contributor's avatar URL.
	 * 
	 * @param avatarUrl contributor's avatar URL to be set
	 */
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	@Override
	public String toString() {
		return login;
	}

}
