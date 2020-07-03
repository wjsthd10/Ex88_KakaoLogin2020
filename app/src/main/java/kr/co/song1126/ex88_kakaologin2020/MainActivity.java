package kr.co.song1126.ex88_kakaologin2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.exception.KakaoException;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity {

    TextView name;
    TextView email;
    CircleImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name=findViewById(R.id.tv_name);
        email=findViewById(R.id.tv_email);
        iv=findViewById(R.id.iv);

        //키해시값 얻어와서 Logcat창에 출력
        String keyHash=getKeyHash(this);
        Log.d("TAG",keyHash);


        //카카오의 로그인 버튼은 별도의 클릭이벤트 처리없이도 자동으로
        //웹뷰를 실행하여 로그인 웹페이지를 보여준다.
        //그 웹페이지의 로그인 응답결과를 받기위한 세션(카카오 서버와 연결되는 통로[Stream과 유사])을 연결하기!!
        Session.getCurrentSession().addCallback(sessionCallback);

    }

    //+===================================================================================
    //todo 카카오 로그인 서버와 연결을 시도하는 세션작업의 결과를 듣는 리스너 객체 생성
    ISessionCallback sessionCallback=new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Toast.makeText(MainActivity.this, "로그인 세션연결 성공", Toast.LENGTH_SHORT).show();

            //로그인된 사용자의 정보들 얻어오기
            requestUserInfo();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
    };

    //앱이 종료되면 자동으로 발동하는 메소드
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //세션연결을 종료하는 코드
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    //+===================================================================================
    //로그인 사용자 정보받기
    void requestUserInfo(){
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onSuccess(MeV2Response result) {
                //todo 사용자 계정 정보 객체
                UserAccount userAccount =result.getKakaoAccount();
                if (userAccount==null) return;
                //todo 1. 이메일 정보
                email.setText(userAccount.getEmail());

                //todo 2. 프로필정보(닉네임, 이미지, 섬네일 이미지)
                Profile profile=userAccount.getProfile();

                if (profile==null) return;

                String nickName=profile.getNickname();
                String imgUrl=profile.getProfileImageUrl();
                String thumbnailImgUrl=profile.getThumbnailImageUrl();

                name.setText(nickName);
                Glide.with(MainActivity.this).load(imgUrl).into(iv);
                
            }
        });
    }


    //+===================================================================================

    //todo 카카오 키 해시
    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("TAG", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }


    public void clickLogout(View view) {

        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Toast.makeText(MainActivity.this, "로그아웃 완료", Toast.LENGTH_SHORT).show();
            }
        });
        
    }
}
