package com.example.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //디비에 저장된 메시지들을 화면에 뿌리기 위한 메인 어뎁터 만들기
    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter; //<객체, 객체>




    //파이어베이스 디비를 사용하기 위한 객체
    private DatabaseReference mFirebaseDatabaseReference;
    //editText
    private EditText mMessageEditText;

    public static final String MESSAGES_CHILD ="messages";//constant로 상수로 정의




    //인증코드 작성하기
    private FirebaseAuth mFirebaseAuth;//인증객체 및 변수 필요
    private FirebaseUser mFirebaseUser;//

    private String mUsername;
    private String mPhotoUrl;

    //로그아웃기능을 위해 추가하기
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //뷰홀더는 메인 엑테비티 안에 내부클래스로 만들기
    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;
        TextView messageTextView;
        ImageView messageImageView;
        CircleImageView photoImageView;

        //생성자 만들어주기 (ctrl+O)
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
    //리싸이클러뷰 가져오기
    private RecyclerView mMessageRecylerView; //선언하기



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //채팅기능 넣기
        //시작지점을 가리키는 레퍼런
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mMessageEditText = findViewById(R.id.message_edit);

        //보내기 버튼 기능 넣기
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //파이어베이스 디비에 작성하는 코드 넣기
                ChatMessage chatMessage = new ChatMessage(mMessageEditText.getText().toString(),
                        mUsername, mPhotoUrl, null);
                //child는 데이터 집합을 가리키는 뿌리, 디렉토리
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                        .push()//푸쉬를 하면서 새로운 데이터를 넣으면서 챗 메시지에 자동으로 아이디가 생성이 된다.
                        .setValue(chatMessage);
                //데이터 보냈으니 edittext는 비워줘야한다.
                mMessageEditText.setText("");
                }
        });


        mMessageRecylerView = findViewById(R.id.message_recycler_view);

        //로그아웃 기능 만들기
        //mGoogleApiClient 초기화하기!!!!!
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)//뒤에것 this 인터페이스 지정하기(alt+enter 헤서 make선택하기)
                .addApi(Auth.GOOGLE_SIGN_IN_API)//로그아웃만 필요
                .build();

        //초기화하기
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();//로그인이 되어 있다면 유저 객체를 얻을 수 있다.
        if(mFirebaseUser==null){
            startActivity(new Intent(this, SignInActivity.class));//아이디가 없다면 로그인 페이지로 이동
            //현재 화면 닫기
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if(mFirebaseUser.getPhotoUrl()!=null){
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        //쿼리 수행하기, => 메시지 전체에 대한 내용을 얻겠다는 것 (옵션 설정하기 위해서)
        Query query = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();


        //파이어베이스 어댑터 초기화하기 + 위에서 정의한 쿼리의 옵션을 전달받기
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(options) { // ()괄호안에 옵션 추가하기
            @Override
            protected void onBindViewHolder(MessageViewHolder holder, int position, ChatMessage model) {
                holder.messageTextView.setText(model.getText());
                holder.nameTextView.setText(model.getName());
                if(model.getPhotoUrl()==null){
                    holder.photoImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                            R.drawable.ic_baseline_account_box_24));
                } else{
                    //이미지가 있는경우 글라이드를 사용해야한다.
                    Glide.with(MainActivity.this)
                            .load(model.getPhotoUrl())
                            .into(holder.photoImageView);
                }
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message,parent,false);
                return new MessageViewHolder(view);
            }
        };

        //리사이클러뷰에 메시지 세팅하기
        mMessageRecylerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecylerView.setAdapter(mFirebaseAdapter);
    }
    //FirebaseAdapter는 액티비티 생명주기에 따라서 상태를 모니터링 해야한다.


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }

    //메뉴 붙이기 작업하기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);//R.menu.main를 menu에 붙이기
       return true;
    }

    //메뉴 이벤트 처리하기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //메뉴 아이템 있을때 사용
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                //파이어페이스 먼저 로그아웃 시키고
                mFirebaseAuth.signOut();
                //구글의 계정도 같이 로그아웃 시켜줘야함
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                //로그아웃 햇으니까 아이디도 초기화 시켜줘야함
                mUsername = "";
                //다시 돌아가기
                startActivity(new Intent(this, SignInActivity.class));
                finish();//현재 액티비티 종료
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}