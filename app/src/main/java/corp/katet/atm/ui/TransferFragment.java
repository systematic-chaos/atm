package corp.katet.atm.ui;

import java.util.Arrays;
import java.util.Date;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.Movement;
import corp.katet.atm.domain.User;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class TransferFragment extends Fragment {

	private View mView;
	private User mUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = inflater.inflate(R.layout.transfer, container, false);
		mUser = DAOFactory.getInstance(getActivity()).getUserDAO()
				.getUserFromId(getArguments().getLong(Constants.USER_ID));

		((Button) mView.findViewById(R.id.buttonTransfer))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						makeTransfer(v);
					}
				});

		((Button) mView.findViewById(R.id.buttonCheckBalance))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						checkBalance(v);
					}
				});

		return mView;
	}

	public void makeTransfer(View v) {
		EditText transferAmountEdit = ((EditText) mView
				.findViewById(R.id.editTextTransferAmount));
		String transferAmountText = transferAmountEdit.getText().toString();
		if (transferAmountText.trim().length() > 0) {
			Movement mov = new Movement(null, mUser, new Date(),
					Float.valueOf(transferAmountText.trim()));
			DAOFactory.getInstance(getActivity()).getMovementDAO()
					.insertMovement(mov);
			((Button) mView.findViewById(R.id.buttonCheckBalance))
					.setVisibility(View.VISIBLE);
			transferAmountEdit.setText("");
		}
	}

	public void checkBalance(View v) {
		MenuActivity menu = (MenuActivity) getActivity();
		menu.selectItem(Arrays.asList(menu.mOptionTitles).indexOf(
				getString(R.string.check_balance)));
	}
}
