package kr.co.song1126.ex88_kakaologin2020;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //카카오 SDK와 앱의 Application연결
        //KakaoAdapter : 카카오와 앱을 연결하는 객체

        //카카오 초기화
        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {

                //Aplication이 가지고 있는 정보를 얻기 위한 인터페이스 객체를 리턴
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return MyApplication.this;
                    }
                };
            }
        });//앱과 연결됨

    }
}
