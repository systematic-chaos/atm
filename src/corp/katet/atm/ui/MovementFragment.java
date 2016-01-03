package corp.katet.atm.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.dao.MovementDAO;
import corp.katet.atm.dao.MovementDAOImpl;
import corp.katet.atm.domain.Movement;
import corp.katet.atm.domain.User;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.CompoundButton;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MovementFragment extends Fragment {
	private View mView;
	private User mUser;
	private MovementAdapter mMovementListAdapter;

	private Date mFromDate, mToDate;
	private boolean mFilterDate, mInMovements, mOutMovements;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = inflater.inflate(R.layout.movement_list, container, false);
		mUser = DAOFactory.getInstance(getActivity()).getUserDAO()
				.getUserFromId(getArguments().getLong(Constants.USER_ID));
		initializeWidgetChangeListeners();
		return mView;
	}

	private void initializeWidgetChangeListeners() {
		Calendar now = Calendar.getInstance();
		final DatePicker fromCalendar = (DatePicker) mView
				.findViewById(R.id.calendar_movement_from);
		fromCalendar.setMaxDate(now.getTimeInMillis());
		fromCalendar.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
					public void onDateChanged(DatePicker view, int year,
							int month, int dayOfMonth) {
						Calendar newFromDate = Calendar.getInstance();
						newFromDate.set(year, month, dayOfMonth);
						mFromDate = newFromDate.getTime();
						if (mFromDate.before(mToDate)) {
							search();
						} else {
							Toast.makeText(getActivity(),
									R.string.wrong_date_interval,
									Toast.LENGTH_SHORT).show();
							mMovementListAdapter.clear();
						}
					}
				});
		mFromDate = now.getTime();

		final DatePicker toCalendar = (DatePicker) mView
				.findViewById(R.id.calendar_movement_to);
		toCalendar.setMaxDate(now.getTimeInMillis());
		toCalendar.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
					public void onDateChanged(DatePicker view, int year,
							int month, int dayOfMonth) {
						Calendar newToDate = Calendar.getInstance();
						newToDate = Calendar.getInstance();
						newToDate.set(year, month, dayOfMonth);
						newToDate.add(Calendar.DATE, 1);
						newToDate.add(Calendar.MINUTE, -5);
						mToDate = newToDate.getTime();
						if (mToDate.after(mFromDate)) {
							search();
						} else {
							Toast.makeText(getActivity(),
									R.string.wrong_date_interval,
									Toast.LENGTH_SHORT).show();
							mMovementListAdapter.clear();
						}
					}
				});
		mToDate = now.getTime();

		CheckBox checkFilterDate = ((CheckBox) mView
				.findViewById(R.id.checkbox_date_movements));
		checkFilterDate
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mFilterDate = isChecked;
						fromCalendar.setEnabled(mFilterDate);
						toCalendar.setEnabled(mFilterDate);
						search();
					}
				});
		mFilterDate = checkFilterDate.isChecked();

		CheckBox checkIn = ((CheckBox) mView
				.findViewById(R.id.checkbox_in_movements));
		checkIn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mInMovements = isChecked;
				search();
			}
		});
		mInMovements = checkIn.isChecked();

		CheckBox checkOut = ((CheckBox) mView
				.findViewById(R.id.checkbox_out_movements));
		checkOut.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mOutMovements = isChecked;
				search();
			}
		});
		mOutMovements = checkOut.isChecked();

		mMovementListAdapter = new MovementAdapter(getActivity(),
				R.layout.movement);
		((ListView) mView.findViewById(R.id.list_movements))
				.setAdapter(mMovementListAdapter);

		search();
	}

	private void search() {
		List<Movement> movResult;
		MovementDAO movDao = DAOFactory.getInstance(getActivity())
				.getMovementDAO();
		if (mInMovements && mOutMovements) {
			if (mFilterDate) {
				movResult = movDao.queryMovements(mUser, mFromDate, mToDate);
			} else {
				movResult = movDao.queryMovements(mUser);
			}
		} else {
			if (mInMovements) {
				if (mFilterDate) {
					movResult = movDao.queryInMovements(mUser, mFromDate,
							mToDate);
				} else {
					movResult = movDao.queryInMovements(mUser);
				}
			} else {
				if (mFilterDate) {
					movResult = movDao.queryOutMovements(mUser, mFromDate,
							mToDate);
				} else {
					movResult = movDao.queryOutMovements(mUser);
				}
			}
		}

		mMovementListAdapter.clear();
		mMovementListAdapter.addAll(movResult);
	}

	private class MovementAdapter extends ArrayAdapter<Movement> {

		private Context context;
		private List<Movement> data = null;

		public MovementAdapter(Context context, int resource) {
			super(context, resource);
			this.context = context;
			data = new ArrayList<Movement>();
		}

		public MovementAdapter(Context context, int resource,
				List<Movement> objects) {
			super(context, resource, objects);
			this.context = context;
			data = objects;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			if (position < 0 || position >= data.size()) {
				return null;
			}
			View view = convertView == null ? ((LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.movement, parent, false) : convertView;
			Movement mov = data.get(position);
			((TextView) view.findViewById(R.id.textViewMovementAmount))
					.setText(String.format("%.2f", mov.getAmount()));
			((TextView) view.findViewById(R.id.textViewMovementDate))
					.setText(MovementDAOImpl.dateToString(mov.getDate()));
			return view;
		}

		@Override
		public void clear() {
			super.clear();
			data.clear();
			notifyDataSetChanged();
		}

		@Override
		public void add(Movement object) {
			super.add(object);
			data.add(object);
			notifyDataSetChanged();
		}

		@Override
		public void addAll(Collection<? extends Movement> collection) {
			super.addAll(collection);
			data.addAll(collection);
			notifyDataSetChanged();
		}

		@Override
		public void addAll(Movement... items) {
			super.addAll(items);
			data.addAll(Arrays.asList(items));
			notifyDataSetChanged();
		}
	}
}
