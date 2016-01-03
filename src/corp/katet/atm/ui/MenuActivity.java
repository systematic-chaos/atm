package corp.katet.atm.ui;

import java.util.Arrays;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import corp.katet.atm.R;
import corp.katet.atm.util.BalanceService;

public class MenuActivity extends AppCompatActivity implements
		ActivityCompat.OnRequestPermissionsResultCallback {
	String[] mOptionTitles;
	private Fragment[] mOptionFragments;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private int mCurrentOptionIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_menu);

		mOptionTitles = getResources().getStringArray(R.array.menu_options);
		mOptionFragments = new Fragment[mOptionTitles.length];
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mTitle = mDrawerTitle = getTitle();

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mOptionTitles));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Start balance check service
		Intent serviceIntent = new Intent(this, BalanceService.class);
		serviceIntent.putExtra(Constants.USER_ID,
				getIntent().getLongExtra(Constants.USER_ID, 0));
		serviceIntent.putExtra(Constants.SERVICE_PERIOD, getResources()
				.getInteger(R.integer.service_period));
		startService(serviceIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Sync the toggle state after onRestoreInstanceState has occurred
		selectItem(0);
		mDrawerToggle.syncState();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.atm);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		toolbar, R.string.open_drawer_content_desc, /* "open drawer" description */
		R.string.close_drawer_content_desc /* "close drawer" description */) {

			/** Called when a drawer has settled in a completely closed state */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	void selectItem(int position) {
		// Avoid error resulting of replacing a fragment by itself
		if (mCurrentOptionIndex == position) {
			mDrawerLayout.closeDrawer(mDrawerList);
			return;
		}

		// Create a new fragment and specify the menu option to show based on
		// position
		Fragment fragment = getOptionFragment(position);
		if (fragment != null) {
			Bundle args = new Bundle();
			args.putLong(Constants.USER_ID,
					getIntent().getLongExtra(Constants.USER_ID, 0));
			fragment.setArguments(args);

			// Insert the fragment by replacing any existing fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
		}

		// Highlight the selected item, update the title, and close the drawer
		mCurrentOptionIndex = position;
		mDrawerList.setItemChecked(position, true);
		setTitle(mOptionTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	private Fragment getOptionFragment(int position) {
		if (mOptionTitles[position].equals(getString(R.string.check_balance))
				&& mOptionFragments[position] == null) {
			mOptionFragments[position] = new CheckBalanceFragment();
		} else if (mOptionTitles[position]
				.equals(getString(R.string.change_pin))
				&& mOptionFragments[position] == null) {
			mOptionFragments[position] = new ModifyPinFragment();
		} else if (mOptionTitles[position].equals(getString(R.string.transfer))
				&& mOptionFragments[position] == null) {
			mOptionFragments[position] = new TransferFragment();
		} else if (mOptionTitles[position]
				.equals(getString(R.string.retrieve_movements))
				&& mOptionFragments[position] == null) {
			mOptionFragments[position] = new MovementFragment();
		} else if (mOptionTitles[position]
				.equals(getString(R.string.retrieve_near_atms))
				&& mOptionFragments[position] == null) {
			mOptionFragments[position] = new NearAtmMapFragment();
		}
		if (mOptionFragments[position] == null) {
			finish();
		}
		return mOptionFragments[position];
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		if (requestCode == Constants.MY_LOCATION_REQUEST_CODE) {
			if (permissions.length == 1
					&& permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				NearAtmMapFragment mapFragment = (NearAtmMapFragment) mOptionFragments[Arrays
						.asList(mOptionTitles).indexOf(
								getString(R.string.retrieve_near_atms))];
				if (mapFragment != null) {
					mapFragment.requestLastLocation();
				}
			} else {
				// Permission was denied. Display an error message.
				Toast.makeText(this, R.string.locationPermissionsNotGranted,
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
