package corp.katet.atm.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Date;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.Movement;
import corp.katet.atm.domain.User;

public class TransferFragment extends Fragment {

	private View mView;
	private User mUser;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = inflater.inflate(R.layout.transfer, container, false);
		if (getArguments() != null && getArguments().containsKey(AuthActivity.USER_ID)) {
		    mUser = DAOFactory.getInstance(getActivity()).getUserDAO()
                    .getUserFromId(getArguments().getLong(AuthActivity.USER_ID));
        }

		mView.findViewById(R.id.buttonTransfer)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						makeTransfer();
					}
				});

		mView.findViewById(R.id.buttonCheckBalance)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						checkBalance();
					}
				});

		return mView;
	}

	public void makeTransfer() {
		EditText transferAmountEdit = mView
				.findViewById(R.id.editTextTransferAmount);
		String transferAmountText = transferAmountEdit.getText().toString();
		if (transferAmountText.trim().length() > 0) {
			Movement mov = new Movement(null, mUser, new Date(),
					Float.valueOf(transferAmountText.trim()));
			DAOFactory.getInstance(getActivity()).getMovementDAO()
					.insertMovement(mov);
			mView.findViewById(R.id.buttonCheckBalance)
					.setVisibility(View.VISIBLE);
			transferAmountEdit.setText("");
		}
	}

	public void checkBalance() {
		MenuActivity menu = (MenuActivity) getActivity();
		if (menu != null) {
            menu.selectItem(Arrays.asList(menu.mOptionTitles).indexOf(
                    getString(R.string.check_balance)));
        }
	}
}
