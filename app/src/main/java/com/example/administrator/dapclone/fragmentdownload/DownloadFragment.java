package com.example.administrator.dapclone.fragmentdownload;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.dapclone.MyApplication;
import com.example.administrator.dapclone.R;

import javax.inject.Inject;

import static com.example.administrator.dapclone.fragmentdownload.IDownloadFragment.*;

/**
 * Created by Administrator on 03/21/2017.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener, RequiredView {

	private static final String TAG = DownloadFragment.class.getSimpleName();
	private EditText editText;
	private Button button;
	private TextView errorTextView;
	@Inject
	ProvidedPresenter providedPresenter;
	String[] string = new String[]{
			"http://f9.stream.nixcdn.com/e84f4e60632efacd40ffb77169129682/58e27393/PreNCT13/NoMoreSadSongs-LittleMixMachineGunKelly" +
					"-4856470.mp4?t=1491235983130",
			"http://f9.stream.nixcdn.com/2c220f444a86c7a6d3646d625eb4a482/58e27393/PreNCT13/LivingOutLoud-BrookeCandySia-4854582.mp4?t" +
					"=1491235946107",
			"http://f9.stream.nixcdn.com/5a8dff032011fc82abaf37f8fbf09eee/58e27393/PreNCT13/BeautyAndTheBeast" +
					"-ArianaGrandeJohnLegend-4814984.mp4?t=1491235950086",
			"http://f9.stream.nixcdn.com/13ea348de037d1ce96f2f2254e746a01/58e27393/PreNCT13/ThatsWhatILike-BrunoMars-4795965.mp4" +
					"?t=1491235951088",
			"http://s1---vt.nixcdn.com/f45e5d712890adb70e0833d3915dce12/58e27393/PreNCT12/WeDonTTalkAnymore" +
					"-CharliePuthSelenaGomez-4528788.mp4?t=1491235952747",
			"http://f9.stream.nixcdn.com/97cc963fea098f0748974b5c39f58e58/58e27393/PreNCT13/ChainedToTheRhythm" +
					"-KatyPerrySkipMarley-4776764.mp4?t=1491235953435",
			"http://f9.stream.nixcdn.com/f900884be14f30846e6e6ec0d77380fb/58e27393/PreNCT13/Cold--4772978.mp4?t=1491235956415",
			"http://s72.stream.nixcdn.com/df7ba2bc0129e87b9fea35750fd380bd/58e27393/PreNCT12/MarysBoyChildOhMyLord-P336Band" +
					"-4712487.mp4?t=1491235968532",
			"http://f9.stream.nixcdn.com/7662c83ff4f670b80d5b37d31896eb38/58e27393/PreNCT13/KeepMeCrazy-Sheppard-4855166.mp4?t" +
					"=1491235970831",
			"http://f9.stream.nixcdn.com/e4f79e69e5c409705f0d43c2426a2323/58e27393/PreNCT13/JohnWayne-LadyGaga-4767137.mp4?t=1491235969980"
	};

	private  int current = 0;
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.download_fragment, container, false);
		editText = (EditText) view.findViewById(R.id.edit_text_download);
		button = (Button) view.findViewById(R.id.button_download);
		errorTextView = (TextView) view.findViewById(R.id.error_text_view);
		button.setOnClickListener(this);
		editText.setText(string[0]);
		((MyApplication) getActivity().getApplication()).getNetComponent().inject(this);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		providedPresenter.setView(this);
		/*for (int i = 0; i < string.length; i++){
			providedPresenter.download(string[i]);
		}*/
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_download:
				errorTextView.setVisibility(View.GONE);
				providedPresenter.download(editText.getText().toString().trim());
				if((current+1) < string.length){
					current++;
					editText.setText(string[current]);
				}else {
					editText.setText(string[current]);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void errorDownload(String message) {
		errorTextView.setText(message);
		errorTextView.setVisibility(View.VISIBLE);
	}

	@Override
	public Context getFragmentContext() {
		return getContext();
	}
}
