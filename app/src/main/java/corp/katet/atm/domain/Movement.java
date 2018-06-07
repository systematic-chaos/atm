package corp.katet.atm.domain;

import java.util.Date;

public class Movement {
	private Long idMovement;
	private User userFrom;
	private User userTo;
	private Date date;
	private float amount;

	public Movement(Long idMovement, User userFrom, User userTo, Date date,
			float amount) {
		this(userFrom, userTo, date, amount);
		this.idMovement = idMovement;
	}

	public Movement(User userFrom, User userTo, Date date, float amount) {
		this.userFrom = userFrom;
		this.userTo = userTo;
		this.date = date;
		this.amount = amount;
	}

	public Long getIdMovement() {
		return idMovement;
	}

	public void setIdMovement(Long idMovement) {
		this.idMovement = idMovement;
	}

	public User getUserFrom() {
		return userFrom;
	}

	public void setUserFrom(User userFrom) {
		this.userFrom = userFrom;
	}

	public User getUserTo() {
		return userTo;
	}

	public void setUserTo(User userTo) {
		this.userTo = userTo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	@Override
	public boolean equals(Object o) {
		boolean eq = false;
		if (o != null && o instanceof Movement) {
			eq = ((Movement) o).getIdMovement().longValue() == idMovement
					.longValue();
		}
		return eq;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (userTo != null) {
			sb.append(userTo.getAccountNumber() + " --> ");
		}
		sb.append(userFrom.getAccountNumber());
		sb.append("\t" + amount);
		sb.append("\t" + date);
		return sb.toString();
	}
}
