package corp.katet.atm.domain;

import com.google.android.gms.maps.model.LatLng;

public class Atm {
	private String id;
	private String name;
	private String address;
	private LatLng coords;

	public Atm(String id, String name, String address, LatLng coords) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.coords = coords;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LatLng getCoords() {
		return coords;
	}

	public void setCoords(LatLng coords) {
		this.coords = coords;
	}

	@Override
	public boolean equals(Object o) {
		boolean eq = false;
		if (o != null && o instanceof Atm) {
			eq = ((Atm) o).getId().equals(id);
		}
		return eq;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (name != null) {
		    sb.append(name).append('\t');
		}
		if (address != null) {
		    sb.append(address).append('\t');
		}
		sb.append('[').append(coords.latitude).append(',')
                .append(coords.longitude).append(']');
		return sb.toString();
	}
}
