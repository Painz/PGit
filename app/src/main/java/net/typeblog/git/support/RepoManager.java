package net.typeblog.git.support;

import android.content.Context;
import android.content.SharedPreferences;

import org.eclipse.jgit.lib.PersonIdent;

import java.io.File;

import net.typeblog.git.R;

public class RepoManager
{
	private static final String PREF = "repos";
	private static final String LOCATIONS = "locations";
	private static final String URL = "url";
	private static final String AUTH_PASS = "auth_pass";
	private static final String COMMITTER_NAME = "name";
	private static final String COMMITTER_EMAIL = "email";
	private static final String SEPERATOR = ",";
	
	private static RepoManager sInstance;
	
	public static RepoManager getInstance() {
		if (sInstance == null) {
			sInstance = new RepoManager();
		}
		
		return sInstance;
	}
	
	public static String checkRepo(String location) {
		File f = new File(location);
		File git = new File(location + "/.git");
		
		if (f.exists() && (!git.exists() || !git.isDirectory())) {
			return GlobalContext.get().getString(R.string.exist_no_git);
		} else {
			return null;
		}
	}
	
	private SharedPreferences mPref;
	
	private RepoManager() {
		mPref = GlobalContext.get().getSharedPreferences(PREF, Context.MODE_WORLD_READABLE);
	}
	
	public void addRepo(String location, String cloneUrl, String username, String password) {
		String origLocations = mPref.getString(LOCATIONS, "");
		mPref.edit()
			.putString(LOCATIONS, origLocations + SEPERATOR + location)
			.putString(location + SEPERATOR + URL, cloneUrl)
			.putString(location + SEPERATOR + AUTH_PASS, username + SEPERATOR + password)
			.commit();
	}
	
	public String[] getRepoLocationList() {
		return mPref.getString(LOCATIONS, "").replaceFirst(SEPERATOR, "").split(SEPERATOR);
	}
	
	public String getUrl(String location) {
		return mPref.getString(location + SEPERATOR + URL, "");
	}
	
	public String[] getAuthPass(String location) {
		return mPref.getString(location + SEPERATOR + AUTH_PASS, "").split(SEPERATOR);
	}
	
	public void setCommitterIdentity(String name, String email) {
		mPref.edit().putString(COMMITTER_NAME, name)
					.putString(COMMITTER_EMAIL, email)
					.commit();
	}
	
	public String getCommitterName() {
		String ret = mPref.getString(COMMITTER_NAME, "");
		if (ret.equals("")) {
			return "root";
		} else {
			return ret;
		}
	}
	
	public String getCommitterEmail() {
		String ret = mPref.getString(COMMITTER_EMAIL, "");
		if (ret.equals("")) {
			return "root@localhost";
		} else {
			return ret;
		}
	}
	
	public PersonIdent getCommitterIdent() {
		return new PersonIdent(getCommitterName(), getCommitterEmail());
	}
}
