package net.typeblog.git.fragments;

import android.app.Activity;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.util.List;

import net.typeblog.git.R;
import net.typeblog.git.adapters.RefAdapter;
import net.typeblog.git.dialogs.GitPushDialog;
import net.typeblog.git.support.GitProvider;
import net.typeblog.git.tasks.GitTask;
import static net.typeblog.git.support.Utility.*;

public class TagListFragment extends BaseListFragment<RefAdapter, Ref>
{
	private GitProvider mProvider;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mProvider = (GitProvider) activity;
	}

	@Override
	protected RefAdapter createAdapter() {
		return new RefAdapter(mItemList);
	}

	@Override
	protected void onViewInflated() {

	}

	@Override
	protected boolean shouldEnterActionMode(int pos) {
		return true;
	}

	@Override
	protected boolean multiChoice() {
		return false;
	}

	@Override
	protected int getActionModeMenu() {
		return R.menu.action_mode_tags;
	}

	@Override
	protected boolean onActionModeItemSelected(int id) {
		Ref tag = mItemList.get(mList.getCheckedItemPosition());
		String name = tag.getName();
		switch (id) {
			case R.id.tag_push:
				new GitPushDialog(getActivity(), mProvider, name).show();
				return true;
			case R.id.tag_delete:
				showConfirmDialog(
					getActivity(),
					String.format(getString(R.string.delete_confirm), name),
					new DeleteTask(),
					new String[]{name});
				return true;
			default:
				return super.onActionModeItemSelected(id);
		}
	}

	@Override
	protected void doLoad(List<Ref> list) {
		try {
			list.addAll(mProvider.git().tagList().call());
		} catch (GitAPIException e) {

		}
	}
	
	private class DeleteTask extends GitTask<String> {
		DeleteTask() {
			super(getActivity(), mProvider);
		}

		@Override
		protected void doGitTask(GitProvider provider, String... params) throws GitAPIException, RuntimeException {
			provider.git().tagDelete().setTags(params[0]).call();
		}

	}

}
