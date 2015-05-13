package net.typeblog.git.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.ArrayList;
import java.util.List;

import net.typeblog.git.R;
import net.typeblog.git.support.GitProvider;
import static net.typeblog.git.support.Utility.*;

public class GitCommitDialog extends ToolbarDialog
{
	private List<String> mUncommitted = new ArrayList<>();
	private GitProvider mProvider;
	private ListView mList;
	private CheckBox mAll, mAmend;
	private EditText mMessage;
	
	public GitCommitDialog(Context context, GitProvider provider) {
		super(context);
		mProvider = provider;
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.commit;
	}

	@Override
	protected void onInitView() {
		setTitle(R.string.git_commit);
		
		mAll = $(this, R.id.commit_all);
		mAmend = $(this, R.id.commit_amend);
		mMessage = $(this, R.id.commit_message);
		mList = $(this, R.id.commit_file);
		
		try {
			mUncommitted.addAll(mProvider.git().status().call().getUncommittedChanges());
		} catch (GitAPIException e) {
			
		}
		
		if (mUncommitted.size() == 0) {
			Toast.makeText(getContext(), R.string.no_modify, Toast.LENGTH_SHORT).show();
			mList.setVisibility(View.GONE);
			mAll.setVisibility(View.GONE);
			mAmend.setChecked(true);
		}
		
		mList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mList.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.item_with_check_multiline, R.id.item_name, mUncommitted));
		
		mAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				mList.setVisibility(checked ? View.GONE : View.VISIBLE);
			}
		});
	}

	@Override
	protected void onConfirm() {
		new CommitTask().execute();
	}
	
	private class CommitTask extends AsyncTask<Void, Void, Void> {
		String message;
		boolean exit = false;
		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progress = new ProgressDialog(getContext());
			progress.setMessage(getContext().getString(R.string.wait));
			progress.setCancelable(false);
			progress.show();
			
			message = mMessage.getText().toString().trim();

			if (message.equals("")) {
				Toast.makeText(getContext(), R.string.no_message, Toast.LENGTH_SHORT).show();
				exit = true;
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (!exit) {
				CommitCommand commit = mProvider.git().commit();
				commit.setAmend(mAmend.isChecked());
				commit.setMessage(message);

				boolean all = mAll.isChecked();

				if (all) {
					commit.setAll(true);
				} else {
					commit.setAll(false);

					SparseBooleanArray a = mList.getCheckedItemPositions();
					for (int i = 0; i < a.size(); i++) {
						int pos = a.keyAt(i);
						if (a.valueAt(i)) {
							commit.setOnly(mUncommitted.get(pos));
						}
					}

				}

				// TODO: Remove this. Do not use root as committer.
				commit.setCommitter("root", "root@localhost");

				try {
					commit.call();
				} catch (GitAPIException e) {
					
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progress.dismiss();
			dismiss();
		}
	}
}
