package cn.dreamchase.android.six.position;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Locale;

import cn.dreamchase.android.six.R;

/**
 * 定位
 */
public class MainActivity_Position extends AppCompatActivity {

    private LocationManager locationManager;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_position);


        textView = findViewById(R.id.tv_location);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        startRequestLocation();
    }

    private void startRequestLocation() {
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gps) {
            Toast.makeText(this, "请先设置界面开启GPS定位服务", Toast.LENGTH_LONG).show();
            return;
        }

        /**
         * provider 是 GPS定位 WIFI定位 基站定位
         * android.permission.ACCESS_FINE_LOCATION
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);// 通过GPS获取设置


        if (location != null) {
            showLocation(location);
        }

        /**
         * -监听位置变化
         * -参数1：定位方式，主要有GPS_PROVIDER 和 NETWORK_PROVIDER  ,前者是GPS ，后者是 GPRS 以及 WIFI 定位
         * -参数2：位置信息更新周期，单位是毫秒
         * -参数3：位置变化最小距离，当位置距离变化超过此值时，将更新位置信息
         * -参数4：监听
         * -备注：参数2和3，如果参数3不为0，则以参数3为准，参数3为0，则通过时间来定时更新；两者为0，则随时更新
         */
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10,locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        // 当位置发送改变时自动调用
        @Override
        public void onLocationChanged(@NonNull Location location) {
            showLocation(location);
        }

        // 当位置提供者的状态发送改变
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            LocationListener.super.onStatusChanged(provider, status, extras);
        }

        // 当位置提供者可用时，自动调用
        @Override
        public void onProviderEnabled(@NonNull String provider) {
            LocationListener.super.onProviderEnabled(provider);
        }

        // 当位置提供者不可用时，自动调用
        @Override
        public void onProviderDisabled(@NonNull String provider) {
            LocationListener.super.onProviderDisabled(provider);
        }
    };

    private void showLocation(Location location) {
        if (location != null) {
            textView.setText("经度:"  + location.getLongitude() + "\n纬度:" + location.getLatitude());

            // 反编码
            Geocoder geocoder = new Geocoder(this, Locale.CHINA);
            try {
                // 参数1：纬度 参数2：经度  参数3：返回地址的数目，该参数取1~5
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(),1);
                for (Address address: addressList) {
                    textView.setText(address.getCountryName() + " " + address.getLocality());
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }
}