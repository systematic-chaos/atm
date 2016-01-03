package corp.katet.atm.ui;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.dao.UserDAO;
import corp.katet.atm.domain.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class AuthActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void tryAccess(View v) {
		EditText accountEdit = (EditText) findViewById(R.id.editTextAccountAuth);
		EditText pinEdit = (EditText) findViewById(R.id.editTextPinAuth);
		String account = accountEdit.getText().toString();
		String pinText = pinEdit.getText().toString().trim();
		Integer pin = pinText.length() > 0 ? Integer.valueOf(pinText) : null;
		UserDAO userDao = DAOFactory.getInstance(this).getUserDAO();
		User user = userDao.getUserFromAccount(account);
		if (user != null && Integer.valueOf(user.getPin()).equals(pin)) {
			Intent intent = new Intent(this, MenuActivity.class);
			intent.putExtra(Constants.USER_ID, user.getIdUser());
			startActivity(intent);
		} else {
			Toast.makeText(this, R.string.wrong_auth, Toast.LENGTH_LONG).show();
			if (user == null) {
				accountEdit.setText("");
			}
		}
		pinEdit.setText("");
	}
}
