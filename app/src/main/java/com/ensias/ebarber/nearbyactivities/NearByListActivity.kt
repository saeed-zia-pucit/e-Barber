package com.ensias.ebarber.nearbyactivities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ensias.ebarber.BaseActivity
import com.ensias.ebarber.Common.Common
import com.ensias.ebarber.DoctorAdapterFiltred
import com.ensias.ebarber.Interface.NearByListener
import com.ensias.ebarber.MyServicesActivity
import com.ensias.ebarber.R
import com.ensias.ebarber.adapter.NearByListAdapter
import com.ensias.ebarber.model.MarkerData
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_near_by_list.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class NearByListActivity : BaseActivity(), NearByListener {
    private val db = FirebaseFirestore.getInstance()
    var nearByLocations: ArrayList<MarkerData> = ArrayList()
    private val defaultRadiusInM = (15.0 * 1000)
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 101
    lateinit  var currentLocation:Location
    lateinit var adapter: NearByListAdapter;

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_by_list)
        setRecyclerView()
        setViewBeforeApiCall()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()

        val mToolbar: Toolbar = findViewById<View>(R.id.activity_main_toolbar) as Toolbar
        setSupportActionBar(mToolbar)

        mToolbar.setNavigationOnClickListener(View.OnClickListener {
         finish()
        })
    }

    fun setRecyclerView() {
        val recyclerview = findViewById<RecyclerView>(R.id.showAllSalonRecycle)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = NearByListAdapter(nearByLocations, this)
        recyclerview.adapter = adapter
    }

    fun setViewBeforeApiCall() {
        loading_bar.visibility = View.VISIBLE
        showAllSalonRecycle.visibility = View.GONE
    }

    fun setViewsAfterApiCall() {
        showAllSalonRecycle.visibility = View.VISIBLE
        loading_bar.visibility = View.GONE
    }

    fun getNearByList(location: Location,radiusInM:Double) {
        nearByLocations.clear()
        val center = GeoLocation(location.latitude, location.longitude)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = java.util.ArrayList()
        for (b in bounds) {
            val q = db.collection("saloons")
                .orderBy("geohash")
                .startAt(b.startHash)
                .endAt(b.endHash)
            tasks.add(q.get())
        }

        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: List<DocumentSnapshot> =
                    java.util.ArrayList()
                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            //matchingDocs.add(doc);
                            nearByLocations.add(
                                MarkerData(
                                    lat,
                                    lng,
                                    distanceInM,
                                    doc.getString("title"),
                                    doc.getString("email")
                                )
                            )
                        }
                    }
                }
                Collections.sort(nearByLocations,
                    Comparator<MarkerData> { o1: MarkerData, o2: MarkerData ->
                        o1.distance.compareTo(
                            o2.distance
                        )
                    })
                adapter.notifyDataSetChanged()
                setViewsAfterApiCall()
            }
    }

    protected fun startLocationUpdates() {
        if(! isLocationEnabled()){
            requestDeviceLocationSettings()
        }
        val mLocationRequest: LocationRequest
        val UPDATE_INTERVAL = (10 * 1000).toLong() /* 10 secs */
        val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = UPDATE_INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE )

            return
        }
        Looper.myLooper()?.let {
            fusedLocationProviderClient?.requestLocationUpdates(
                mLocationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        // do work here
                        if (locationResult.lastLocation != null)
                            currentLocation = locationResult.lastLocation
                            getNearByList(locationResult.lastLocation,defaultRadiusInM)
                        fusedLocationProviderClient?.removeLocationUpdates(this)
                    }
                },
                it
            )
        }
    }

    override fun onMapButtonClick(position: Int) {
        val i = Intent(this, ShowSalonOnMapActivity::class.java)
        i.putExtra("email", nearByLocations.get(position).gmail)
        Common.CurrentDoctorName = nearByLocations.get(position).title
        Common.latLng =LatLng(nearByLocations.get(position).latitude, nearByLocations.get(position).longitude)
        startActivity(i)
    }


    override fun onBookButton(position: Int) {
        Common.CurrentDoctorName = nearByLocations.get(position).title
        val i = Intent(this, MyServicesActivity::class.java)
        i.putExtra("userType", "Customer")
        i.putExtra("email", nearByLocations.get(position).gmail)
        startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.near_by_salon_bar, menu)
        val r = resources.getDrawable(R.drawable.ic_local_hospital_black_24dp)
        r.setBounds(0, 0, r.intrinsicWidth, r.intrinsicHeight)
        val sb = SpannableString("Distance")
        val imageSpan = ImageSpan(r, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        menu.findItem(R.id.empty).setTitle(sb);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.five_km -> {
                setViewBeforeApiCall()
                getNearByList(currentLocation,5.0*1000)
                true
            }
            R.id.ten_km -> {
                setViewBeforeApiCall()
                getNearByList(currentLocation,10.0*1000)
                true
            }
            R.id.fifteen_km -> {
                setViewBeforeApiCall()
                getNearByList(currentLocation,15.0*1000)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                    return
                }
                startLocationUpdates();

            }
        }
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            val state = locationSettingsResponse.locationSettingsStates

            val label =
                "GPS >> (Present: ${state?.isGpsPresent}  | Usable: ${state?.isGpsUsable} ) \n\n" +
                        "Network >> ( Present: ${state?.isNetworkLocationPresent} | Usable: ${state?.isNetworkLocationUsable} ) \n\n" +
                        "Location >> ( Present: ${state?.isLocationPresent} | Usable: ${state?.isLocationUsable} )"
           startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this,
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }
}