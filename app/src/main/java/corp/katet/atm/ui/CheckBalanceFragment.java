package corp.katet.atm.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.User;

public class CheckBalanceFragment extends Fragment {

	private View mView;
	private User mUser;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = inflater.inflate(R.layout.check_balance, container, false);
		if (getArguments() != null
				&& getArguments().containsKey(AuthActivity.USER_ID)) {
		    mUser = DAOFactory.getInstance(getActivity()).getUserDAO()
                    .getUserFromId(getArguments().getLong(AuthActivity.USER_ID));
        }

		displayCurrentBalance();

		return mView;
	}

	private void displayCurrentBalance() {
	    if (getActivity() != null) {
            ((TextView) mView.findViewById(R.id.textViewBalance))
                    .setText(getActivity().getString(R.string.balance_msg,
                            mUser.getCurrentBalance()));
        }
	}
}
