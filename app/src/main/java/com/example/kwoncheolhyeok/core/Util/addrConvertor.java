package com.example.kwoncheolhyeok.core.Util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by juyeol on 2017. 6. 26
 * GeoCoder를 이용한 주소변환
 * 위도,경도 -> 주소 / 주소 -> 위도,경도 변환가능
 */

public class addrConvertor {

    public static String getAddress(Context mContext, LatLng latlng) {
        String nowAddress = "현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);

            if (address != null && address.size() > 0) {
                // 주소 받아오기
                nowAddress = "";
                if (address.get(0).getAdminArea() != null) {
                    nowAddress += address.get(0).getAdminArea() + " ";
                }
                if (address.get(0).getLocality() != null && !address.get(0).getLocality().equals(address.get(0).getAdminArea())) {
                    nowAddress += address.get(0).getLocality() + " ";
                }
                if (address.get(0).getThoroughfare() != null) {
                    nowAddress += address.get(0).getThoroughfare();
                }
            }

        } catch (IOException e) {
            Toast.makeText(mContext, "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return nowAddress;
    }


    public static Location findGeoPoint(Context mcontext, String address) {
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mcontext);
        // 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정
        List<Address> addr = null;

        try {
            addr = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            for (int i = 0; i < addr.size(); i++) {
                Address lating = addr.get(i);
                double lat = lating.getLatitude(); // 위도가져오기
                double lon = lating.getLongitude(); // 경도가져오기
                loc.setLatitude(lat);
                loc.setLongitude(lon);
            }
        }
        return loc;
    }

}
