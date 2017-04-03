package com.example.administrator.dapclone.fragmentdownload;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.example.administrator.dapclone.fragmentdownload.IDownloadFragment.*;

/**
 * Created by Administrator on 03/31/2017.
 */

@Module
public class DownloadModule {
	private ProvidedPresenter providedPresenter;

	public DownloadModule() {
		providedPresenter = new DownloadPresenter();
		ProvidedModel providedModel = new DownloadModel((RequiredPresenter) providedPresenter);
		providedPresenter.setModel(providedModel);
	}

	@Provides
	@Singleton
	ProvidedPresenter provideDownloadPresenter() {
		return providedPresenter;
	}
}
