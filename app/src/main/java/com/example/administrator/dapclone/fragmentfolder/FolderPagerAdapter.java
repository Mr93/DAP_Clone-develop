package com.example.administrator.dapclone.fragmentfolder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.administrator.dapclone.FragmentSettings;
import com.example.administrator.dapclone.fragmentdownload.DownloadFragment;
import com.example.administrator.dapclone.fragmentfolder.folderdownload.FolderDownloadFragment;

/**
 * Created by Administrator on 03/28/2017.
 */

public class FolderPagerAdapter extends FragmentStatePagerAdapter {
	int numberTab = 2;

	public FolderPagerAdapter(FragmentManager fm, int numberTab) {
		super(fm);
		this.numberTab = numberTab;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment;
		switch (position) {
			case 0:
				fragment = new FolderDownloadFragment();
				break;
			case 1:
				fragment = new DownloadFragment();
				break;
			default:
				fragment = new FragmentSettings();
				break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return numberTab;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "Download";
			case 1:
				return "Upload";
			default:
				return "Download";
		}
	}
}
