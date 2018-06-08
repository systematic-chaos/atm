package corp.katet.atm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import corp.katet.atm.R;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.dao.UserDAO;
import corp.katet.atm.domain.User;

public class AuthActivity extends Activity {

	public static final String USER_ID = "userID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void tryAccess(View v) {
		EditText accountEdit = findViewById(R.id.editTextAccountAuth);
		EditText pinEdit = findViewById(R.id.editTextPinAuth);
		String account = accountEdit.getText().toString();
		String pinText = pinEdit.getText().toString().trim();
		Integer pin = pinText.length() > 0 ? Integer.valueOf(pinText) : null;
		UserDAO userDao = DAOFactory.getInstance(this).getUserDAO();
		User user = userDao.getUserFromAccount(account);
		if (user != null && Integer.valueOf(user.getPin()).equals(pin)) {
			Intent intent = new Intent(this, MenuActivity.class);
			intent.putExtra(USER_ID, user.getIdUser());
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
