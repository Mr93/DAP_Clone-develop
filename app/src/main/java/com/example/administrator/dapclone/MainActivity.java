package com.example.administrator.dapclone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.administrator.dapclone.fragmentdownload.DownloadFragment;
import com.example.administrator.dapclone.service.NetworkService;
import com.example.administrator.dapclone.view.BottomNavigationViewHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	private ViewPager viewPager;
	private BottomNavigationView bottomNavigationView;
	private static final String POSITION = "POSITION";
	private int[] menuBottomNavigationBarId = {
			R.id.download_bottom_menu,
			R.id.upload_bottom_menu,
			R.id.folder_bottom_menu,
			R.id.setting_bottom_menu
	};
	private static final int MY_PERMISSION_REQUEST_STORAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), this));
		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		bottomNavigationView.setOnNavigationItemSelectedListener(this);
		BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
		viewPager.addOnPageChangeListener(this);
		if (permissionGranted()) {
			startMyService();
		}
	}

	private void startMyService() {
		Intent intent = new Intent(this, NetworkService.class);
		startService(intent);
	}

	private boolean permissionGranted() {
		boolean permissionGranted = false;
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				showPermissionExplainDialog();
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);
			}
		} else {
			permissionGranted = true;
		}
		return permissionGranted;
	}

	private void showPermissionExplainDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Permission")
				.setMessage("Need storage permission, enable in settings")
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
					}
				});
		builder.create().show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSION_REQUEST_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startMyService();
				} else {
					showPermissionExplainDialog();
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.download_bottom_menu:
				viewPager.setCurrentItem(0);
				break;
			case R.id.upload_bottom_menu:
				viewPager.setCurrentItem(1);
				break;
			case R.id.folder_bottom_menu:
				viewPager.setCurrentItem(2);
				break;
			case R.id.setting_bottom_menu:
				viewPager.setCurrentItem(3);
				break;
			default:
				viewPager.setCurrentItem(0);
				break;
		}
		return true;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		bottomNavigationView.setSelectedItemId(menuBottomNavigationBarId[position]);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onBackPressed() {
		List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
		if (fragmentList != null) {
			for (Fragment fragment : fragmentList) {
				if (fragment instanceof DownloadFragment) {
					Log.d(TAG, "onBackPressed: aaaa");
				}
			}
		}
		super.onBackPressed();
	}
}
