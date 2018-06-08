package corp.katet.atm.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.User;

public class ModifyPinFragment extends Fragment {

	private View mView;
	private User mUser;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = inflater.inflate(R.layout.modify_pin, container, false);
		if (getArguments() != null && getArguments().containsKey(AuthActivity.USER_ID)) {
		    mUser = DAOFactory.getInstance(getActivity()).getUserDAO()
                    .getUserFromId(getArguments().getLong(AuthActivity.USER_ID));
        }
		
		mView.findViewById(R.id.buttonModifyPin)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						modifyPin();
					}
				});
		
		return mView;
	}

	public void modifyPin() {
		String oldPin = ((TextView) mView.findViewById(R.id.editTextOldPin))
				.getText().toString().trim();
		String newPin1 = ((TextView) mView.findViewById(R.id.editTextNewPin1))
				.getText().toString().trim();
		String newPin2 = ((TextView) mView.findViewById(R.id.editTextNewPin2))
				.getText().toString().trim();
		boolean updateOk = true;
		if (oldPin.length() > 0 && newPin1.length() > 0 && newPin2.length() > 0) {
			if (Integer.parseInt(oldPin) != mUser.getPin()) {
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
