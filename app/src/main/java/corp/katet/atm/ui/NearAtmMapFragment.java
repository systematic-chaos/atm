package corp.katet.atm.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import corp.katet.atm.R;
import corp.katet.atm.dao.AtmDAO;
import corp.katet.atm.dao.DAOFactory;
import corp.katet.atm.domain.Atm;

public class NearAtmMapFragment extends Fragment implements OnMapReadyCallback,
		ConnectionCallbacks, OnConnectionFailedListener {

	private GoogleMap mMap;
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	private RequestQueue mRequestQueue;
	private MapView mMapView;

	private static final String TAG = "NEAR_PLACES_SEARCH";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.atm_map, container, false);

		mMapView = (MapView) view.findViewById(R.id.map);
		if (mMapView != null) {
			mMapView.onCreate(savedInstanceState);
			mMapView.getMapAsync(this);
		}

		return view;
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.getUiSettings().setMyLocationButtonEnabled(false);
		if (ContextCompat.checkSelfPermission(getContext(),
				Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			requestLastLocation();
		} else {
			// Show rationale and request permission
			ActivityCompat.requestPermissions(getActivity(),
					new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
					Constants.MY_LOCATION_REQUEST_CODE);
		}
	}

	public synchronized void requestLastLocation() {
		if (mMap != null) {
			mMap.setMyLocationEnabled(true);
		}

		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		mGoogleApiClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();
		}
	}

	@Override
	public void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (mMapView != null) {
			mMapView.onDestroy();
		}
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mMapView != null) {
			mMapView.onSaveInstanceState(outState);
		}
	}

	@Override
	public void onLowMemory() {
		if (mMapView != null) {
			mMapView.onLowMemory();
		}
		super.onLowMemory();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15.8f));
		sendRequest();
	}

	@Override
	public void onConnectionSuspended(int cause) {
	}

	private synchronized void sendRequest() {
		if (mLastLocation == null)
			return;

		// Instantiate the RequestQueue
		mRequestQueue = Volley.newRequestQueue(getActivity());
		StringBuilder url = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		url.append("location=" + mLastLocation.getLatitude() + ","
				+ mLastLocation.getLongitude());
		url.append("&radius=500");
		url.append("&types=atm");
		url.append("&key=" + getActivity().getString(R.string.api_key));

		// Request a string response from the provided URL
		JsonObjectRequest jsonRequest = (JsonObjectRequest) new JsonObjectRequest(
				Request.Method.GET, url.toString(), null,
				new Response.Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							if (response.getString("status").equals("OK")) {
								List<Place> searchResults = parseSearchResults(response
										.getJSONArray("results"));
								addMapMarkers(searchResults);
								insertOrUpdatePlaces(searchResults);
							} else {
								throw new JSONException(response
										.getString("status"));
							}
						} catch (JSONException jsone) {
						}
					}
				}, new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
					}
				}).setTag(TAG);

		// Add the request to the RequestQueue
		mRequestQueue.add(jsonRequest);
	}

	private List<Place> parseSearchResults(JSONArray results)
			throws JSONException {
		ArrayList<Place> placeList = new ArrayList<Place>(results.length());
		JSONObject jsonPlace;
		Place place;
		for (int i = 0; i < results.length(); i++) {
			jsonPlace = results.getJSONObject(i);
			place = new Place();
			place.setLatLng(new LatLng(jsonPlace.getJSONObject("geometry")
					.getJSONObject("location").getDouble("lat"), jsonPlace
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng")));
			place.setName(jsonPlace.getString("name"));
			place.setId(jsonPlace.getString("place_id"));
			JSONArray types = jsonPlace.getJSONArray("types");
			List<Integer> typesCodes = new ArrayList<Integer>();
			for (int j = 0; j < types.length(); j++) {
				typesCodes.add(PlaceType.valueFromKey(types.getString(j)));
			}
			place.setAddress(jsonPlace.getString("vicinity"));
			placeList.add(place);
		}
		return placeList;
	}

	private void addMapMarkers(List<Place> placesList) {
		for (Place place : placesList) {
			mMap.addMarker(new MarkerOptions().position(place.getLatLng())
					.title(place.getName().toString())
					.snippet(place.getAddress().toString()));
		}
	}

	private int insertOrUpdatePlaces(List<Place> placesList) {
		AtmDAO dao = DAOFactory.getInstance(getActivity()).getAtmDAO();
		Atm placeAtm;
		int nRows = 0;
		for (Place place : placesList) {
			placeAtm = new Atm(place.getId(), place.getName().toString(), place
					.getAddress().toString(), place.getLatLng());
			if (dao.getAtmFromId(place.getId()) == null) {
				if (dao.insertAtm(placeAtm)) {
					nRows++;
				}
			} else {
				if (dao.updateAtm(placeAtm)) {
					nRows++;
				}
			}
		}
		return nRows;
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(TAG);
		}
	}

	private class Place implements com.google.android.gms.location.places.Place {

		private boolean dataValid = true;
		private CharSequence address;
		private String id;
		private LatLng latLng;
		private Locale locale = Locale.getDefault();
		private CharSequence name;
		private CharSequence phoneNumber = null;
		private List<Integer> placeTypes;
		private int priceLevel = -1;
		private float rating = 5f;
		private LatLngBounds viewport = null;
		private Uri websiteUri = null;

		public Place() {
		}

		public Place(CharSequence address, String id, LatLng latLng,
				CharSequence name, List<Integer> placeTypes) {
			this.address = address;
			this.id = id;
			this.latLng = latLng;
			this.name = name;
			this.placeTypes = placeTypes;
		}

		@Override
		public boolean isDataValid() {
			return dataValid;
		}

		public CharSequence getAddress() {
			return address;
		}

		public void setAddress(CharSequence address) {
			this.address = address;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public LatLng getLatLng() {
			return latLng;
		}

		public void setLatLng(LatLng latLng) {
			this.latLng = latLng;
		}

		public Locale getLocale() {
			return locale;
		}

		public CharSequence getName() {
			return name;
		}

		public void setName(CharSequence name) {
			this.name = name;
		}

		public CharSequence getPhoneNumber() {
			return phoneNumber;
		}

		public List<Integer> getPlaceTypes() {
			return placeTypes;
		}

		public int getPriceLevel() {
			return priceLevel;
		}

		public float getRating() {
			return rating;
		}

		public LatLngBounds getViewport() {
			return viewport;
		}

		public Uri getWebsiteUri() {
			return websiteUri;
		}

		@Override
		public com.google.android.gms.location.places.Place freeze() {
			return new Place(address, id, latLng, name, placeTypes);
		}
	}

	private static class PlaceType {
		static final String[] placeTypes = { "accounting",
				"administrative_area_level_1", "administrative_area_level_2",
				"administrative_area_level_3", "airport", "amusement_park",
				"aquarium", "art_gallery", "atm", "bakery", "bank", "bar",
				"beauty_salon", "bicycle_store", "book_store", "bowling_alley",
				"bus_station", "cafe", "campground", "car_dealer",
				"car_rental", "car_repair", "car_wash", "casino", "cemetery",
				"church", "city_hall", "clothing_store", "colloquial_area",
				"convenience_store", "country", "courthouse", "dentist",
				"department_store", "doctor", "electrician",
				"electronics_store", "embassy", "establishment", "finance",
				"fire_station", "floor", "florist", "food", "funeral_home",
				"furniture_store", "gas_station", "general_contractor",
				"geocode", "grocery_or_supermarket", "gym", "hair_care",
				"hardware_store", "health", "hindu_temple", "home_goods_store",
				"hospital", "insurance_agency", "intersection",
				"jewelry_store", "laundry", "lawyer", "library",
				"liquor_store", "locality", "local_government_office",
				"locksmith", "lodging", "meal_delivery", "meal_takeaway",
				"mosque", "movie_rental", "movie_theater", "moving_company",
				"museum", "natural_feature", "neighborhood", "night_club",
				"other", "painter", "park", "parking", "pet_store", "pharmacy",
				"physiotherapist", "place_of_worship", "plumber",
				"point_of_interest", "police", "political", "postal_code",
				"postal_code_prefix", "postal_town", "post_box", "post_office",
				"premise", "real_estate_agency", "restaurant",
				"roofing_contractor", "room", "route", "rv_park", "school",
				"shoe_store", "shopping_mall", "spa", "stadium", "storage",
				"store", "street_address", "sublocality",
				"sublocality_level_1", "sublocality_level_2",
				"sublocality_level_3", "sublocality_level_4",
				"sublocality_level_5", "subpremise", "subway_station",
				"synagogue", "synthetic_geocode", "taxi_stand",
				"train_station", "transit_station", "travel_agency",
				"university", "veterinary_care", "zoo" };
		private static final int[] placeCodes = {
				com.google.android.gms.location.places.Place.TYPE_ACCOUNTING,
				com.google.android.gms.location.places.Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_1,
				com.google.android.gms.location.places.Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_2,
				com.google.android.gms.location.places.Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_3,
				com.google.android.gms.location.places.Place.TYPE_AIRPORT,
				com.google.android.gms.location.places.Place.TYPE_AMUSEMENT_PARK,
				com.google.android.gms.location.places.Place.TYPE_AQUARIUM,
				com.google.android.gms.location.places.Place.TYPE_ART_GALLERY,
				com.google.android.gms.location.places.Place.TYPE_ATM,
				com.google.android.gms.location.places.Place.TYPE_BAKERY,
				com.google.android.gms.location.places.Place.TYPE_BANK,
				com.google.android.gms.location.places.Place.TYPE_BAR,
				com.google.android.gms.location.places.Place.TYPE_BEAUTY_SALON,
				com.google.android.gms.location.places.Place.TYPE_BICYCLE_STORE,
				com.google.android.gms.location.places.Place.TYPE_BOOK_STORE,
				com.google.android.gms.location.places.Place.TYPE_BOWLING_ALLEY,
				com.google.android.gms.location.places.Place.TYPE_BUS_STATION,
				com.google.android.gms.location.places.Place.TYPE_CAFE,
				com.google.android.gms.location.places.Place.TYPE_CAMPGROUND,
				com.google.android.gms.location.places.Place.TYPE_CAR_DEALER,
				com.google.android.gms.location.places.Place.TYPE_CAR_RENTAL,
				com.google.android.gms.location.places.Place.TYPE_CAR_REPAIR,
				com.google.android.gms.location.places.Place.TYPE_CAR_WASH,
				com.google.android.gms.location.places.Place.TYPE_CASINO,
				com.google.android.gms.location.places.Place.TYPE_CEMETERY,
				com.google.android.gms.location.places.Place.TYPE_CHURCH,
				com.google.android.gms.location.places.Place.TYPE_CITY_HALL,
				com.google.android.gms.location.places.Place.TYPE_CLOTHING_STORE,
				com.google.android.gms.location.places.Place.TYPE_COLLOQUIAL_AREA,
				com.google.android.gms.location.places.Place.TYPE_CONVENIENCE_STORE,
				com.google.android.gms.location.places.Place.TYPE_COUNTRY,
				com.google.android.gms.location.places.Place.TYPE_COURTHOUSE,
				com.google.android.gms.location.places.Place.TYPE_DENTIST,
				com.google.android.gms.location.places.Place.TYPE_DEPARTMENT_STORE,
				com.google.android.gms.location.places.Place.TYPE_DOCTOR,
				com.google.android.gms.location.places.Place.TYPE_ELECTRICIAN,
				com.google.android.gms.location.places.Place.TYPE_ELECTRONICS_STORE,
				com.google.android.gms.location.places.Place.TYPE_EMBASSY,
				com.google.android.gms.location.places.Place.TYPE_ESTABLISHMENT,
				com.google.android.gms.location.places.Place.TYPE_FINANCE,
				com.google.android.gms.location.places.Place.TYPE_FIRE_STATION,
				com.google.android.gms.location.places.Place.TYPE_FLOOR,
				com.google.android.gms.location.places.Place.TYPE_FLORIST,
				com.google.android.gms.location.places.Place.TYPE_FOOD,
				com.google.android.gms.location.places.Place.TYPE_FUNERAL_HOME,
				com.google.android.gms.location.places.Place.TYPE_FURNITURE_STORE,
				com.google.android.gms.location.places.Place.TYPE_GAS_STATION,
				com.google.android.gms.location.places.Place.TYPE_GENERAL_CONTRACTOR,
				com.google.android.gms.location.places.Place.TYPE_GEOCODE,
				com.google.android.gms.location.places.Place.TYPE_GROCERY_OR_SUPERMARKET,
				com.google.android.gms.location.places.Place.TYPE_GYM,
				com.google.android.gms.location.places.Place.TYPE_HAIR_CARE,
				com.google.android.gms.location.places.Place.TYPE_HARDWARE_STORE,
				com.google.android.gms.location.places.Place.TYPE_HEALTH,
				com.google.android.gms.location.places.Place.TYPE_HINDU_TEMPLE,
				com.google.android.gms.location.places.Place.TYPE_HOME_GOODS_STORE,
				com.google.android.gms.location.places.Place.TYPE_HOSPITAL,
				com.google.android.gms.location.places.Place.TYPE_INSURANCE_AGENCY,
				com.google.android.gms.location.places.Place.TYPE_INTERSECTION,
				com.google.android.gms.location.places.Place.TYPE_JEWELRY_STORE,
				com.google.android.gms.location.places.Place.TYPE_LAUNDRY,
				com.google.android.gms.location.places.Place.TYPE_LAWYER,
				com.google.android.gms.location.places.Place.TYPE_LIBRARY,
				com.google.android.gms.location.places.Place.TYPE_LIQUOR_STORE,
				com.google.android.gms.location.places.Place.TYPE_LOCALITY,
				com.google.android.gms.location.places.Place.TYPE_LOCAL_GOVERNMENT_OFFICE,
				com.google.android.gms.location.places.Place.TYPE_LOCKSMITH,
				com.google.android.gms.location.places.Place.TYPE_LODGING,
				com.google.android.gms.location.places.Place.TYPE_MEAL_DELIVERY,
				com.google.android.gms.location.places.Place.TYPE_MEAL_TAKEAWAY,
				com.google.android.gms.location.places.Place.TYPE_MOSQUE,
				com.google.android.gms.location.places.Place.TYPE_MOVIE_RENTAL,
				com.google.android.gms.location.places.Place.TYPE_MOVIE_THEATER,
				com.google.android.gms.location.places.Place.TYPE_MOVING_COMPANY,
				com.google.android.gms.location.places.Place.TYPE_MUSEUM,
				com.google.android.gms.location.places.Place.TYPE_NATURAL_FEATURE,
				com.google.android.gms.location.places.Place.TYPE_NEIGHBORHOOD,
				com.google.android.gms.location.places.Place.TYPE_NIGHT_CLUB,
				com.google.android.gms.location.places.Place.TYPE_OTHER,
				com.google.android.gms.location.places.Place.TYPE_PAINTER,
				com.google.android.gms.location.places.Place.TYPE_PARK,
				com.google.android.gms.location.places.Place.TYPE_PARKING,
				com.google.android.gms.location.places.Place.TYPE_PET_STORE,
				com.google.android.gms.location.places.Place.TYPE_PHARMACY,
				com.google.android.gms.location.places.Place.TYPE_PHYSIOTHERAPIST,
				com.google.android.gms.location.places.Place.TYPE_PLACE_OF_WORSHIP,
				com.google.android.gms.location.places.Place.TYPE_PLUMBER,
				com.google.android.gms.location.places.Place.TYPE_POINT_OF_INTEREST,
				com.google.android.gms.location.places.Place.TYPE_POLICE,
				com.google.android.gms.location.places.Place.TYPE_POLITICAL,
				com.google.android.gms.location.places.Place.TYPE_POSTAL_CODE,
				com.google.android.gms.location.places.Place.TYPE_POSTAL_CODE_PREFIX,
				com.google.android.gms.location.places.Place.TYPE_POSTAL_TOWN,
				com.google.android.gms.location.places.Place.TYPE_POST_BOX,
				com.google.android.gms.location.places.Place.TYPE_POST_OFFICE,
				com.google.android.gms.location.places.Place.TYPE_PREMISE,
				com.google.android.gms.location.places.Place.TYPE_REAL_ESTATE_AGENCY,
				com.google.android.gms.location.places.Place.TYPE_RESTAURANT,
				com.google.android.gms.location.places.Place.TYPE_ROOFING_CONTRACTOR,
				com.google.android.gms.location.places.Place.TYPE_ROOM,
				com.google.android.gms.location.places.Place.TYPE_ROUTE,
				com.google.android.gms.location.places.Place.TYPE_RV_PARK,
				com.google.android.gms.location.places.Place.TYPE_SCHOOL,
				com.google.android.gms.location.places.Place.TYPE_SHOE_STORE,
				com.google.android.gms.location.places.Place.TYPE_SHOPPING_MALL,
				com.google.android.gms.location.places.Place.TYPE_SPA,
				com.google.android.gms.location.places.Place.TYPE_STADIUM,
				com.google.android.gms.location.places.Place.TYPE_STORAGE,
				com.google.android.gms.location.places.Place.TYPE_STORE,
				com.google.android.gms.location.places.Place.TYPE_STREET_ADDRESS,
				com.google.android.gms.location.places.Place.TYPE_SUBLOCALITY,
				com.google.android.gms.location.places.Place.TYPE_SUBLOCALITY_LEVEL_1,
				com.google.android.gms.location.places.Place.TYPE_SUBLOCALITY_LEVEL_2,
				com.google.android.gms.location.places.Place.TYPE_SUBLOCALITY_LEVEL_3,
				com.google.android.gms.location.places.Place.TYPE_SUBLOCALITY_LEVEL_4,
				com.google.android.gms.location.places.Place.TYPE_SUBLOCALITY_LEVEL_5,
				com.google.android.gms.location.places.Place.TYPE_SUBPREMISE,
				com.google.android.gms.location.places.Place.TYPE_SUBWAY_STATION,
				com.google.android.gms.location.places.Place.TYPE_SYNAGOGUE,
				com.google.android.gms.location.places.Place.TYPE_SYNTHETIC_GEOCODE,
				com.google.android.gms.location.places.Place.TYPE_TAXI_STAND,
				com.google.android.gms.location.places.Place.TYPE_TRAIN_STATION,
				com.google.android.gms.location.places.Place.TYPE_TRANSIT_STATION,
				com.google.android.gms.location.places.Place.TYPE_TRAVEL_AGENCY,
				com.google.android.gms.location.places.Place.TYPE_UNIVERSITY,
				com.google.android.gms.location.places.Place.TYPE_VETERINARY_CARE,
				com.google.android.gms.location.places.Place.TYPE_ZOO };

		static final Map<String, Integer> mapping = new HashMap<String, Integer>();
		static {
			for (int n = 0; n < placeTypes.length; n++) {
				mapping.put(placeTypes[n], placeCodes[n]);
			}
		}

		public static int valueFromKey(String key) {
			return mapping.get(key);
		}
	}
}
