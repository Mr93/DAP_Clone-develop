package com.example.administrator.dapclone;

import com.example.administrator.dapclone.fragmentdownload.DownloadFragment;
import com.example.administrator.dapclone.fragmentdownload.DownloadModule;
import com.example.administrator.dapclone.fragmentfolder.folderdownload.FolderDownloadFragment;
import com.example.administrator.dapclone.fragmentfolder.folderdownload.FolderDownloadModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Administrator on 03/31/2017.
 */

@Singleton
@Component(modules = {DownloadModule.class, FolderDownloadModule.class})
public interface NetComponent {
	void inject(DownloadFragment downloadFragment);

	void inject(FolderDownloadFragment folderDownloadFragment);
}
