package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.administrator.dapclone.ConstantValues;
import com.example.administrator.dapclone.MyApplication;
import com.example.administrator.dapclone.PreviewActivity;
import com.example.administrator.dapclone.R;
import com.example.administrator.dapclone.TaskInfo;

import java.io.File;

import javax.inject.Inject;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/29/2017.
 */

public class FolderDownloadFragment extends Fragment implements RequiredView {
	private static final String TAG = FolderDownloadFragment.class.getSimpleName();
	private RecyclerView recyclerView;
	private DownloadListAdapter downloadListAdapter;
	@Inject
	ProvidedPresenter providedPresenter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MyApplication) getActivity().getApplication()).getNetComponent().inject(this);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.folder_download_fragment, container, false);
		initRecyclerView(view);
		createTouchCallBack();
		return view;
	}

	private void initRecyclerView(View view) {
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
	}

	private void createTouchCallBack() {
		ItemTouchHelper.SimpleCallback simpleCallback = createCallBackSwipe();
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
		itemTouchHelper.attachToRecyclerView(recyclerView);
	}

	@NonNull
	private ItemTouchHelper.SimpleCallback createCallBackSwipe() {
		ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
				ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
				providedPresenter.getTaskInfoList().remove(viewHolder.getAdapterPosition());
				downloadListAdapter.notifyDataSetChanged();
			}
		};
		return simpleCallback;
	}

	@Override
	public void onStart() {
		super.onStart();
		providedPresenter.setView(this);
		providedPresenter.getDownloadDataFromDB();
	}

	@Override
	public void onStop() {
		super.onStop();
		providedPresenter.unRegisterBroadcast();
	}

	@Override
	public void fetchDataRecycleView() {
		if (recyclerView != null) {
			downloadListAdapter = new DownloadListAdapter(providedPresenter.getTaskInfoList(), providedPresenter);
			recyclerView.setAdapter(downloadListAdapter);
			downloadListAdapter.notifyDataSetChanged();
			providedPresenter.registerBroadCast();
		}
	}

	@Override
	public void updateDataRecycleView() {
		if (recyclerView != null && downloadListAdapter != null) {
			downloadListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void preViewMedia(TaskInfo taskInfo, String type) {
		Log.d(TAG, "preViewMedia: " + type);
		Intent intent = new Intent(getActivity(), PreviewActivity.class);
		intent.putExtra(ConstantValues.FILE_INFO, taskInfo);
		intent.putExtra(ConstantValues.CONTENT_TYPE, type);
		startActivity(intent);
	}
}
