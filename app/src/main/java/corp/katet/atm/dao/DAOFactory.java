package corp.katet.atm.dao;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import corp.katet.atm.domain.Atm;
import corp.katet.atm.domain.Movement;
import corp.katet.atm.domain.User;

@SuppressWarnings("rawtypes")
public class DAOFactory {

	private static DAOFactory factory;
	private Map<Class, DAOImpl> domainDaoMapping;

	private DAOFactory(Context context) {
		initializeMapping(context);
	}

	public static DAOFactory getInstance(Context context) {
		if (factory == null) {
			factory = new DAOFactory(context);
		}
		return factory;
	}

	private void initializeMapping(Context context) {
		domainDaoMapping = new HashMap<Class, DAOImpl>();
		domainDaoMapping.put(User.class, UserDAOImpl.getInstance(context));
		domainDaoMapping.put(Movement.class,
				MovementDAOImpl.getInstance(context));
		domainDaoMapping.put(Atm.class, AtmDAOImpl.getInstance(context));
	}

	public DAOImpl getDAO(Class domainClass) {
		return domainDaoMapping.get(domainClass);
	}

	public UserDAO getUserDAO() {
		return (UserDAOImpl) domainDaoMapping.get(User.class);
	}

	public MovementDAO getMovementDAO() {
		return (MovementDAOImpl) domainDaoMapping.get(Movement.class);
	}

	public AtmDAO getAtmDAO() {
		return (AtmDAOImpl) domainDaoMapping.get(Atm.class);
	}
}
