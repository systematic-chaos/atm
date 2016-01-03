package corp.katet.atm.ui;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.User;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CheckBalanceFragment extends Fragment {

	private View mView;
	private User mUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = inflater.inflate(R.layout.check_balance, container, false);
		mUser = DAOFactory.getInstance(getActivity()).getUserDAO()
				.getUserFromId(getArguments().getLong(Constants.USER_ID));

		displayCurrentBalance();

		return mView;
	}

	private void displayCurrentBalance() {
		((TextView) mView.findViewById(R.id.textViewBalance))
				.setText(getActivity().getString(R.string.balance_msg,
						mUser.getCurrentBalance()));
	}
}
