package com.example.administrator.dapclone.fragmentfolder.folderdownload;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.example.administrator.dapclone.fragmentfolder.folderdownload.IFolderDownloadFragment.*;

/**
 * Created by Administrator on 03/31/2017.
 */

@Module
public class FolderDownloadModule {
	private ProvidedPresenter providedPresenter;

	public FolderDownloadModule() {
		providedPresenter = new FolderDownloadPresenter();
		ProvidedModel providedModel = new FolderDownloadFragmentModel(((RequiredPresenter) providedPresenter));
		providedPresenter.setModel(providedModel);
	}

	@Provides
	@Singleton
	ProvidedPresenter providePresenter() {
		return providedPresenter;
	}
}
