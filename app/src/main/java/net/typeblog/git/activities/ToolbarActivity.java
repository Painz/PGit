package net.typeblog.git.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.typeblog.git.R;
import static net.typeblog.git.support.Utility.*;

public abstract class ToolbarActivity extends AppCompatActivity
{
	private Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		
		// Find toolbar
		mToolbar = $(this, R.id.toolbar);
		
		if (mToolbar == null)
			throw new IllegalStateException("No Toolbar");
		
		setSupportActionBar(mToolbar);
		
		if (Build.VERSION.SDK_INT >= 21) {
			findViewById(R.id.toolbar_wrapper).setElevation(15.6f);
		}
		
		onInitView();
	}
	
	protected Toolbar getToolbar() {
		return mToolbar;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	protected abstract int getLayoutResource();
	protected abstract void onInitView();
}
