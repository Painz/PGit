package net.typeblog.git.dialogs;

import android.content.Context;
import android.view.View;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProvider;

import java.io.IOException;

import net.typeblog.git.R;
import net.typeblog.git.support.GitProvider;
import net.typeblog.git.support.RepoManager;
import static net.typeblog.git.support.Utility.*;

public class GitPullDialog extends BasePullPushDialog
{
	public GitPullDialog(Context context, GitProvider provider) {
		super(context, provider, null);
	}

	@Override
	protected void onInitView() {
		setTitle(R.string.git_pull);
		super.onInitView();
		
		// Disable force for pull (for now)
		mForce.setVisibility(View.GONE);
	}

	@Override
	protected void doTask(ProgressMonitor monitor, String remote, String ref, CredentialsProvider authorization, boolean force) throws GitAPIException, RuntimeException {
		mProvider.git().pull()
				.setRemote(remote)
				.setCredentialsProvider(authorization)
				.setProgressMonitor(monitor)
				.call();
	}

}
