import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azure.maps.control.AzureMap
import com.azure.maps.control.AzureMapView
import com.azure.maps.control.layer.SymbolLayer
import com.azure.maps.control.source.DataSource
import com.azure.maps.control.options.SymbolOptions
import com.azure.android.maps.control.options.CameraOptions
import com.azure.android.maps.control.data.Position
import com.azure.maps.control.data.Feature

import com.azure.maps.control.data.Geometry
import org.json.JSONObject

class PoliceDepartmentActivty : AppCompatActivity() {
    private lateinit var mapView: AzureMapView
    private lateinit var azureMap: AzureMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_police_department_activty)

        // Initialize Azure MapView
        mapView = findViewById(R.id.azureMapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            azureMap = map
            azureMap.setCamera(CameraOptions().center(Position(36.817223, -1.286389)).zoom(10.0))
            fetchPoliceStationsAndDisplayMarkers()
        }
    }

    private fun fetchPoliceStationsAndDisplayMarkers() {
        // Example: Hardcoded police stations (replace with API integration)
        val policeStations = listOf(
            Position(36.81667, -1.28333), // Station 1
            Position(36.82000, -1.29000), // Station 2
            Position(36.81000, -1.29500)  // Station 3
        )

        val dataSource = DataSource()
        azureMap.sources.add(dataSource)

        for (position in policeStations) {
            val feature = Feature(Geometry.fromPosition(position))
            dataSource.add(feature)

            // Add marker
            val symbolLayer = SymbolLayer(dataSource)
            symbolLayer.add(SymbolOptions().icon("marker-blue").position(position))
            azureMap.layers.add(symbolLayer)
        }

        Toast.makeText(this, "Police stations displayed on the map.", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
