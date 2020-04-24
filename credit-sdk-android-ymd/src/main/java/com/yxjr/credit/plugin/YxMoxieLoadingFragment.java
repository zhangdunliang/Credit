package com.yxjr.credit.plugin;

import org.json.JSONObject;

import com.moxie.client.manager.StatusViewListener;
import com.yxjr.credit.ui.view.DialogLoading;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class YxMoxieLoadingFragment extends Fragment implements StatusViewListener {

	DialogLoading mDialogLoading;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		RelativeLayout topLayout = new RelativeLayout(this.getActivity());
		topLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

		mDialogLoading = new DialogLoading(this.getActivity());

		mDialogLoading.show();
		return topLayout;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub 
		mDialogLoading.dismiss();
		super.onDestroy();
	}

	@Override
	public void updateProgress(JSONObject arg0) {
		// TODO Auto-generated method stub

	}
}
