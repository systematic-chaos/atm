package corp.katet.atm.dao;

import java.util.Set;

import com.google.android.gms.maps.model.LatLngBounds;

import corp.katet.atm.domain.Atm;

public interface AtmDAO {
	boolean insertAtm(Atm newAtm);

	boolean updateAtm(Atm atm);
	
	boolean deleteAtm(Atm atm);

	Atm getAtmFromId(String idAtm);

	Set<Atm> queryAllAtms();

	Set<Atm> queryAtmsInBounds(LatLngBounds bounds);
}
