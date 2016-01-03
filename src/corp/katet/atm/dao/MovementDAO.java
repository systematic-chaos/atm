package corp.katet.atm.dao;

import java.util.Date;
import java.util.List;

import corp.katet.atm.domain.Movement;
import corp.katet.atm.domain.User;

public interface MovementDAO {
	boolean insertMovement(Movement newMovement);

	boolean updateMovement(Movement movement);

	boolean deleteMovement(Movement movement);

	Movement getMovementFromId(long idMovement);

	List<Movement> queryMovements(User user);

	List<Movement> queryMovements(User user, Date startDate, Date endDate);

	List<Movement> queryOutMovements(User outUser);

	List<Movement> queryOutMovements(User outUser, Date startDate, Date endDate);

	List<Movement> queryInMovements(User inUser);

	List<Movement> queryInMovements(User inUser, Date startDate, Date endDate);

	List<Movement> queryAllMovements();
}
