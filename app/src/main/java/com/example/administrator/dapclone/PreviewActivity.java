package com.example.administrator.dapclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.administrator.dapclone.utils.Validator;

import java.io.File;

/**
 * Created by Administrator on 03/31/2017.
 */

public class PreviewActivity extends AppCompatActivity {

	private TaskInfo taskInfo;
	private String type;
	private VideoView videoPreview;
	private ImageView imagePreview;
	private MediaController mediaController;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_preview);
		initView();
		Intent intent = getIntent();
		if (intent != null) {
			this.type = intent.getStringExtra(ConstantValues.CONTENT_TYPE);
			this.taskInfo = intent.getParcelableExtra(ConstantValues.FILE_INFO);
			setTitle(taskInfo.name);
			if (Validator.IMAGE.equalsIgnoreCase(type)) {
				showImagePreview();
			} else {
				showMusicOrVideoPreview();
			}
		}
	}

	private void showMusicOrVideoPreview() {
		videoPreview.setVisibility(View.VISIBLE);
		videoPreview.setVideoURI(Uri.parse(taskInfo.path));
		videoPreview.setZOrderOnTop(true);
		if (!Validator.MUSIC.equalsIgnoreCase(type)) {
			videoPreview.setBackgroundDrawable(mediaController.getBackground());
			videoPreview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
		}
		videoPreview.start();
	}

	private void showImagePreview() {
		imagePreview.setVisibility(View.VISIBLE);
		File imgFile = new File(taskInfo.path);
		if (imgFile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			imagePreview.setImageBitmap(bitmap);
		}
	}

	private void initView() {
		imagePreview = (ImageView) findViewById(R.id.image_preview);
		videoPreview = (VideoView) findViewById(R.id.video_preview);
		mediaController = new MediaController(this);
		mediaController = new MediaController(PreviewActivity.this);
		videoPreview.setMediaController(mediaController);
		mediaController.setAnchorView(videoPreview);
		mediaController.setPadding(0, 0, 0, 0);
	}
}
