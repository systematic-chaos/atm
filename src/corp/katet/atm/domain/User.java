package corp.katet.atm.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

	private Long idUser;
	private String accountNumber;
	private int pin;
	private float currentBalance;

	public User(Long idUser, String accountNumber, int pin, float currentBalance) {
		this(accountNumber, pin, currentBalance);
		this.idUser = idUser;
	}

	public User(String accountNumber, int pin) {
		this(accountNumber, pin, 0f);
	}

	public User(String accountNumber, int pin, float currentBalance) {
		this.accountNumber = accountNumber;
		this.pin = pin;
		this.currentBalance = currentBalance;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public float getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(float currentBalance) {
		this.currentBalance = currentBalance;
	}

	@Override
	public boolean equals(Object o) {
		boolean eq = false;
		if (o != null && o instanceof User) {
			eq = ((User) o).getAccountNumber().equals(accountNumber);
		}
		return eq;
	}

	@Override
	public String toString() {
		return accountNumber + "\t" + currentBalance;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(idUser);
		dest.writeString(accountNumber);
		dest.writeInt(pin);
		dest.writeFloat(currentBalance);
	}

	private User(Parcel in) {
		idUser = in.readLong();
		accountNumber = in.readString();
		pin = in.readInt();
		currentBalance = in.readFloat();
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};
}
