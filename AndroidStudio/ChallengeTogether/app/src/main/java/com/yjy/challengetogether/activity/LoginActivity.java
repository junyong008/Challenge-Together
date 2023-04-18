package com.yjy.challengetogether.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import io.github.muddz.styleabletoast.StyleableToast;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    private String TAG  = "LoginActivity";


    private EditText edit_email;
    private EditText edit_pw;
    private Button button_login;
    private ImageButton ibutton_kakaologin, ibutton_googlelogin, ibutton_naverlogin;
    private TextView tbutton_signup;
    private TextView tbutton_findpwd;
    private GoogleSignInClient mGoogleSignInClient;
    private com.yjy.challengetogether.util.Util util = new Util(LoginActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_email = findViewById(R.id.edit_email);
        edit_pw = findViewById(R.id.edit_pw);
        button_login = findViewById(R.id.button_login);
        ibutton_kakaologin = findViewById(R.id.ibutton_kakaologin);
        ibutton_googlelogin = findViewById(R.id.ibutton_googlelogin);
        ibutton_naverlogin = findViewById(R.id.ibutton_naverlogin);
        tbutton_signup = findViewById(R.id.tbutton_signup);
        tbutton_findpwd = findViewById(R.id.tbutton_findpwd);

        // 스토리지에서 세션키가 있으면 바로 메인 페이지 진입. 메인 페이지에서 세션키 유효성을 검사하기에 여기서 매번 검사할 필요 없음.
        String SessionKeyFromStorage = util.getSessionKey();
        if (SessionKeyFromStorage != null && !SessionKeyFromStorage.isEmpty()) { // SessionKeyFromStorage가 있는경우
            Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
            startActivity(intent);
            finish();
        }

        // 로그인 버튼 클릭
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = edit_email.getText().toString().trim();
                String pwd = edit_pw.getText().toString().trim();

                // 예외 처리
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pwd)) {
                    StyleableToast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", R.style.errorToast).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(id).matches()) {
                    StyleableToast.makeText(LoginActivity.this, "아이디는 이메일 형식으로 작성해주세요.", R.style.errorToast).show();
                    return;
                }

                // 아래 비동기 작업이 완료된 후 실행
                OnTaskCompleted onLoginTaskCompleted = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {
                        Boolean isSessionKey = util.isSessionKey(result);

                        if (isSessionKey) {
                            // 로그인 성공.
                            util.saveSessionKey(result);
                            StyleableToast.makeText(LoginActivity.this, "로그인 성공", R.style.successToast).show();

                            Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (result.indexOf("USER NOT FOUND") != -1) {
                            StyleableToast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", R.style.errorToast).show();
                            return;
                        } else {
                            util.checkHttpResult(result);
                        }
                    }
                };

                HttpAsyncTask loginTask = new HttpAsyncTask(LoginActivity.this, onLoginTaskCompleted);
                String phpFile = "login.php";
                String postParameters = "userid=" + id + "&userpw=" + util.getHash(pwd);

                loginTask.execute(phpFile, postParameters);
            }
        });

        // 회원가입 버튼 클릭
        tbutton_signup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        });

        // 비밀번호 찾기 버튼 클릭
        tbutton_findpwd.setOnClickListener(v -> {
            Intent intent = new Intent(this, FindpwdActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        });





        // 카카오 로그인 callback
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {

                if (throwable != null) {
                    Log.d(TAG, "로그인 실패", throwable);
                } else if (oAuthToken != null) {
                    Log.d(TAG, "로그인 성공 " + oAuthToken.getAccessToken());

                    UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(User user, Throwable throwable) {

                            if (user != null) {
                                // 유저의 아이디를 받아와서 DB에 닉네임이 존재하면 바로 로그인, 아니라면 닉네임 결정창으로 이동해 회원가입 절차 진행

                                String kakaoUserId = String.valueOf(user.getId());

                                OnTaskCompleted onKakaoLoginTaskCompleted = new OnTaskCompleted() {
                                    @Override
                                    public void onTaskCompleted(String result) {
                                        Boolean isSessionKey = util.isSessionKey(result);

                                        if (isSessionKey) {
                                            // 로그인 성공. 기존에 회원가입을 했던 유저.
                                            util.saveSessionKey(result);
                                            StyleableToast.makeText(LoginActivity.this, "로그인 성공", R.style.successToast).show();

                                            Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (result.indexOf("USER NOT FOUND") != -1) {
                                            // 회원가입을 하지 않은 신규 유저. 닉네임을 정해서 회원가입 하도록 유도
                                            Intent intent = new Intent(LoginActivity.this, SignupActivity2.class);
                                            intent.putExtra("email", "");
                                            intent.putExtra("password", "");
                                            intent.putExtra("kakaoid", kakaoUserId);
                                            intent.putExtra("googleid", "");
                                            intent.putExtra("naverid", "");
                                            startActivity(intent);
                                        } else {
                                            util.checkHttpResult(result);
                                        }
                                    }
                                };

                                HttpAsyncTask kakaoLoginTask = new HttpAsyncTask(LoginActivity.this, onKakaoLoginTaskCompleted);
                                String phpFile = "kakaologin.php";
                                String postParameters = "kakaoid=" + kakaoUserId;

                                kakaoLoginTask.execute(phpFile, postParameters);
                            }

                            return null;
                        }
                    });

                }

                return null;
            }
        };

        // 카카오 로그인 버튼 클릭
        ibutton_kakaologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    // 카카오톡으로 로그인
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                } else {
                    // 카카오계정으로 로그인
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }

            }
        });



        // 네이버 로그인 callback
        OAuthLoginCallback oauthLoginCallback = new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 해당 유저의 id를 가져옴
                NidOAuthLogin nidOAuthLogin = new NidOAuthLogin();
                nidOAuthLogin.callProfileApi(new NidProfileCallback<NidProfileResponse>() {
                    @Override
                    public void onSuccess(NidProfileResponse result) {
                        // 유저 정보 가져오기 성공 시 수행할 코드 추가
                        String id = result.getProfile().getId();

                        OnTaskCompleted onNaverLoginTaskCompleted = new OnTaskCompleted() {
                            @Override
                            public void onTaskCompleted(String result) {
                                Boolean isSessionKey = util.isSessionKey(result);

                                if (isSessionKey) {
                                    // 로그인 성공. 기존에 회원가입을 했던 유저.
                                    util.saveSessionKey(result);
                                    StyleableToast.makeText(LoginActivity.this, "로그인 성공", R.style.successToast).show();

                                    Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (result.indexOf("USER NOT FOUND") != -1) {
                                    // 회원가입을 하지 않은 신규 유저. 닉네임을 정해서 회원가입 하도록 유도
                                    Intent intent = new Intent(LoginActivity.this, SignupActivity2.class);
                                    intent.putExtra("email", "");
                                    intent.putExtra("password", "");
                                    intent.putExtra("kakaoid", "");
                                    intent.putExtra("googleid", "");
                                    intent.putExtra("naverid", id);
                                    startActivity(intent);
                                } else {
                                    util.checkHttpResult(result);
                                }
                            }
                        };

                        HttpAsyncTask naverLoginTask = new HttpAsyncTask(LoginActivity.this, onNaverLoginTaskCompleted);
                        String phpFile = "naverlogin.php";
                        String postParameters = "naverid=" + id;

                        naverLoginTask.execute(phpFile, postParameters);
                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        // 유저 정보 가져오기 실패 시 수행할 코드 추가
                        Log.d(TAG, "Failed to get user profile. ErrorCode: " + errorCode + ", ErrorMessage: " + message);
                    }

                    @Override
                    public void onFailure(int httpStatus, String message) {
                        // 유저 정보 가져오기 요청 실패 시 수행할 코드 추가
                        Log.d(TAG, "Failed to request user profile. HttpStatus: " + httpStatus + ", ErrorMessage: " + message);
                    }
                });
            }

            @Override
            public void onFailure(int httpStatus, String message) {
                String errorCode = NaverIdLoginSDK.INSTANCE.getLastErrorCode().getCode();
                String errorDescription = NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
                Log.d(TAG, "Naver Login Falied - " + errorCode + " / " + errorDescription);
            }

            @Override
            public void onError(int errorCode, String message) {
                onFailure(errorCode, message);
            }
        };

        // 네아버 로그인 버튼 클릭
        ibutton_naverlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaverIdLoginSDK.INSTANCE.authenticate(LoginActivity.this, oauthLoginCallback);
            }
        });



        // 구글 로그인 객체 초기화
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 구글 로그인 버튼 클릭
        ibutton_googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Google 로그인 창을 연다. 이미 로그인 돼 있으면 계정 선택 없이 바로 로그인
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });

    }


    // 구글 로그인 후 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    String id = account.getId();
                    /*String givenName = account.getGivenName();
                    String familyName = account.getFamilyName();
                    String email = account.getEmail();*/

                    OnTaskCompleted onGoogleLoginTaskCompleted = new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {
                            Boolean isSessionKey = util.isSessionKey(result);

                            if (isSessionKey) {
                                // 로그인 성공. 기존에 회원가입을 했던 유저.
                                util.saveSessionKey(result);
                                StyleableToast.makeText(LoginActivity.this, "로그인 성공", R.style.successToast).show();

                                Intent intent = new Intent(LoginActivity.this, MainpageActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (result.indexOf("USER NOT FOUND") != -1) {
                                // 회원가입을 하지 않은 신규 유저. 닉네임을 정해서 회원가입 하도록 유도
                                Intent intent = new Intent(LoginActivity.this, SignupActivity2.class);
                                intent.putExtra("email", "");
                                intent.putExtra("password", "");
                                intent.putExtra("kakaoid", "");
                                intent.putExtra("googleid", id);
                                intent.putExtra("naverid", "");
                                startActivity(intent);
                            } else {
                                util.checkHttpResult(result);
                            }
                        }
                    };

                    HttpAsyncTask googleLoginTask = new HttpAsyncTask(LoginActivity.this, onGoogleLoginTaskCompleted);
                    String phpFile = "googlelogin.php";
                    String postParameters = "googleid=" + id;

                    googleLoginTask.execute(phpFile, postParameters);


                } catch (ApiException e) {
                    Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
                }

            }
        }
    }
}