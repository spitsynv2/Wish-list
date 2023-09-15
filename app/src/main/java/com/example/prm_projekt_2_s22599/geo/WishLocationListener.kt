import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.prm_projekt_2_s22599.model.Product


private const val RADIUS = 300.0

class WishLocationListener(private val products: ArrayList<Product>) : LocationListener {

    private val productsInRadius: MutableList<Product> = mutableListOf()
    private lateinit var context: Context

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        productsInRadius.clear()

        for (product in products) {
            if (isWithinRadius(latitude, longitude, product.latitude, product.longitude)) {
                productsInRadius.add(product)
            }
        }

        if (productsInRadius.isNotEmpty()) {
            sendNotification(getCombinedProductNames())
        }
    }

    private fun isWithinRadius(latitude: Double, longitude: Double, productLatitude: Double, productLongitude: Double): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(latitude, longitude, productLatitude, productLongitude, results)
        return results[0] <= RADIUS
    }

    private fun getCombinedProductNames(): String {
        val builder = StringBuilder()
        for (product in productsInRadius) {
            builder.append(product.name).append(", ")
        }
        return builder.toString().trimEnd(',', ' ')
    }

    private fun sendNotification(productNames: String) {
        val notificationId = 1

        val notificationBuilder = NotificationCompat.Builder(context, "WishListId")
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Products from wishlist are in Radius")
            .setContentText("The products: $productNames from wishlist are near")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("WishListId", "WishList", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun setContext(context: Context) {
        this.context = context
    }

}
