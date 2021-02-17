package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.function.LongConsumer;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    public static final int RC_SIGN_IN=1000;//constant로 만들기
    //현재 로그인 되어 있는지 객체 생성하기
    private FirebaseAuth mFirebaseAuth;
    //구글 인증을 위한 코드 작성하기
    private GoogleApiClient mGoogleApiClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //구글로 인증
        mFirebaseAuth = FirebaseAuth.getInstance();

        //구글 사인인 옵션 객체 생성하기
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("363575431655-am756ns3f7p3l618pci0pu150kkfc51c.apps.googleusercontent.com")
                .requestEmail()
                .build();

        //mGoogleApiClient 초기화하기!!!!!
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)//뒤에것 this 인터페이스 지정하기(alt+enter 헤서 make선택하기)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)//gso은 사인인 옵션을 주는것
                .build();

        //버튼을 눌렀을때 클릭이벤트 연결하기
        findViewById(R.id.sign_in_button).setOnClickListener(this); // 액티비티에서 처리한다.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {//인터페이스 생성

    }

    @Override
    public void onClick(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //여기에서 intent가 실행된다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{
                Toast.makeText(this, "Google Sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //credential라는 객체 생성하기
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(SignInActivity.this,"인증 실패",Toast.LENGTH_SHORT).show();
                        }else{
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }
}