package corp.katet.atm.ui;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.User;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyPinFragment extends Fragment {

	private View mView;
	private User mUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = inflater.inflate(R.layout.modify_pin, container, false);
		mUser = DAOFactory.getInstance(getActivity()).getUserDAO()
				.getUserFromId(getArguments().getLong(Constants.USER_ID));
		
		((Button) mView.findViewById(R.id.buttonModifyPin))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						modifyPin(v);
					}
				});
		
		return mView;
	}

	public void modifyPin(View v) {
		String oldPin = ((TextView) mView.findViewById(R.id.editTextOldPin))
				.getText().toString().trim();
		String newPin1 = ((TextView) mView.findViewById(R.id.editTextNewPin1))
				.getText().toString().trim();
		String newPin2 = ((TextView) mView.findViewById(R.id.editTextNewPin2))
				.getText().toString().trim();
		boolean updateOk = true;
		if (oldPin.length() > 0 && newPin1.length() > 0 && newPin2.length() > 0) {
			if (Integer.valueOf(oldPin).intValue() != mUser.getPin()) {
				Toast.makeText(getActivity(), R.string.wrong_old_pin,
						Toast.LENGTH_SHORT).show();
				updateOk = false;
			}
			if (!newPin1.equals(newPin2)) {
				Toast.makeText(getActivity(), R.string.wrong_pin_match,
						Toast.LENGTH_SHORT).show();
				updateOk = false;
			}
			if (updateOk) {
				mUser.setPin(Integer.valueOf(newPin1));
				DAOFactory.getInstance(getActivity()).getUserDAO()
						.updatePin(mUser);
			}
		}
	}
}
