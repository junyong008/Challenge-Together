package com.yjy.challengetogether.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.kakao.sdk.user.UserApiClient;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.yjy.challengetogether.BuildConfig;
import com.yjy.challengetogether.R;
import com.yjy.challengetogether.activity.ChangepwdActivity;
import com.yjy.challengetogether.activity.LoginActivity;
import com.yjy.challengetogether.activity.PushSettingActivity;
import com.yjy.challengetogether.activity.RecordActivity;
import com.yjy.challengetogether.etc.OnTaskCompleted;
import com.yjy.challengetogether.util.Const;
import com.yjy.challengetogether.util.HttpAsyncTask;
import com.yjy.challengetogether.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MyFragment extends Fragment {

    private View view;
    private String TAG  = "MY 프레그먼트";

    private CardView cardView_grade;
    private TextView textView_nickname, textView_grade, textView_version;
    private RoundCornerProgressBar progress_nextgrade;
    private ImageView imageView_nextgrade;
    private Button ibutton_pushsetting, ibutton_inquire, ibutton_privacypolicy, ibutton_license, ibutton_changepwd, ibutton_logout, ibutton_deleteaccount;
    private GoogleSignInClient mGoogleSignInClient;

    private Util util;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_my, container, false);

        util = new Util(requireActivity());

        textView_nickname = view.findViewById(R.id.textView_nickname);
        cardView_grade = view.findViewById(R.id.cardView_grade);
        textView_grade = view.findViewById(R.id.textView_grade);
        progress_nextgrade = view.findViewById(R.id.progress_nextgrade);
        imageView_nextgrade = view.findViewById(R.id.imageView_nextgrade);
        ibutton_pushsetting = view.findViewById(R.id.ibutton_pushsetting);
        ibutton_inquire = view.findViewById(R.id.ibutton_inquire);
        ibutton_privacypolicy = view.findViewById(R.id.ibutton_privacypolicy);
        ibutton_license = view.findViewById(R.id.ibutton_license);
        textView_version = view.findViewById(R.id.textView_version);
        ibutton_changepwd = view.findViewById(R.id.ibutton_changepwd);
        ibutton_logout = view.findViewById(R.id.ibutton_logout);
        ibutton_deleteaccount = view.findViewById(R.id.ibutton_deleteaccount);

        // 카드뷰(등급 안내) 클릭
        cardView_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                startActivity(intent);
            }
        });

        // 알림 설정 버튼 클릭
        ibutton_pushsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PushSettingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });

        // 1대1 문의 버튼 클릭
        ibutton_inquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    String[] address = {"junyong008@gmail.com"};
                    email.putExtra(Intent.EXTRA_EMAIL, address);
                    email.putExtra(Intent.EXTRA_SUBJECT, "챌린지 투게더 -  1:1 문의");
                    email.putExtra(Intent.EXTRA_TEXT, "문의 내용 : \n");
                    startActivity(Intent.createChooser(email, "이메일 어플 선택"));
                } catch (Exception e) {
                    Log.d("MyFragment", "NO EMAIL APP");
                }
            }
        });

        // 개인정보 처리방침 버튼 클릭
        ibutton_privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://sites.google.com/view/challenge-together/%ED%99%88"));
                startActivity(intent);
            }
        });

        // 오픈소스 라이선스 버튼 클릭
        ibutton_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OssLicensesMenuActivity.class);
                OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스");
                startActivity(intent);
            }
        });

        // 버전 텍스트 설정
        String versionName = BuildConfig.VERSION_NAME;
        textView_version.setText(versionName);

        // 비밀번호 변경 버튼 클릭
        ibutton_changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                util.showCustomDialog(new Util.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isConfirmed, String msg) {
                        if (isConfirmed) {
                            Intent intent = new Intent(getActivity(), ChangepwdActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }, "비밀번호를 변경하시겠습니까?", "confirm");

            }
        });

        // 로그아웃 버튼 클릭
        ibutton_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showCustomDialog(new Util.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isConfirmed, String msg) {
                        if (isConfirmed) {
                            StyleableToast.makeText(getActivity().getApplicationContext(), "로그아웃되었습니다.", R.style.successToast).show();

                            // 카카오 로그아웃
                            try {
                                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                                    @Override
                                    public Unit invoke(Throwable throwable) {
                                        if (throwable != null) {
                                            Log.d(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", throwable);
                                        } else {
                                            Log.d(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨");
                                        }
                                        return null;
                                    }
                                });
                            } catch (Exception e) {}

                            // 구글 로그아웃
                            try {
                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                                mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                                mGoogleSignInClient.signOut();
                            } catch (Exception e) {}

                            // 네이버 로그아웃
                            try {
                                NaverIdLoginSDK.INSTANCE.logout();
                            } catch (Exception e) {}

                            // 기기에 저장된 설정값 초기화
                            util.initSettings();

                            // 로그인 액티비티 실행 후 그 외 모두 삭제
                            Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }, "로그아웃하시겠습니까?", "confirm");
            }
        });

        // 회원탈퇴 버튼 클릭 : 2차로 확인
        ibutton_deleteaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showCustomDialog(new Util.OnConfirmListener() {
                        @Override
                    public void onConfirm(boolean isConfirmed, String msg) {
                        if (isConfirmed) {

                            util.showCustomDialog(new Util.OnConfirmListener() {
                                @Override
                                public void onConfirm(boolean isConfirmed, String msg) {
                                    if (isConfirmed) {
                                        OnTaskCompleted onDeleteAccountTaskCompleted = new OnTaskCompleted() {
                                            @Override
                                            public void onTaskCompleted(String result) {
                                                if (result.indexOf("DELETE ACCOUNT SUCCESS") != -1) {
                                                    StyleableToast.makeText(getActivity().getApplicationContext(), "탈퇴되었습니다.", R.style.successToast).show();

                                                    // 카카오 연결끊기
                                                    try {
                                                        UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
                                                            @Override
                                                            public Unit invoke(Throwable throwable) {
                                                                if (throwable != null) {
                                                                    Log.d(TAG, "연결끊기 실패.", throwable);
                                                                } else {
                                                                    Log.d(TAG, "연결끊기 성공. SDK에서 토큰 삭제됨");
                                                                }
                                                                return null;
                                                            }
                                                        });
                                                    } catch (Exception e) {}

                                                    // 구글 연결끊기
                                                    try {
                                                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                                                        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                                                        mGoogleSignInClient.revokeAccess();
                                                    } catch (Exception e) {}

                                                    // 네이버 연결끊기
                                                    try {
                                                        NaverIdLoginSDK.INSTANCE.logout();
                                                        NidOAuthLogin nidOAuthLogin = new NidOAuthLogin();
                                                        nidOAuthLogin.callDeleteTokenApi(getActivity(), null);
                                                    } catch (Exception e) {}

                                                    // 기기에 저장된 설정값 초기화
                                                    util.initSettings();

                                                    // 로그인 액티비티 실행 후 그 외 모두 삭제
                                                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    getActivity().finish();
                                                } else {
                                                    util.checkHttpResult(result);
                                                }
                                            }
                                        };

                                        HttpAsyncTask deleteAccountTask = new HttpAsyncTask(getActivity(), onDeleteAccountTaskCompleted);
                                        String phpFile = "service 1.1.0.php";
                                        String postParameters = "service=deleteaccount";

                                        deleteAccountTask.execute(phpFile, postParameters, util.getSessionKey());
                                    }
                                }
                            }, "\uD83D\uDE25 정말로 탈퇴하시겠습니까?", "confirm");


                        }
                    }
                }, "탈퇴하기", "deleteaccount");
            }
        });





        OnTaskCompleted onLoadRecordTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                Boolean isJSON = util.isJson(result);

                if (isJSON) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String username = jsonObject.getString("NAME");
                        long record_bestTime = jsonObject.getLong("BESTTIME");

                        textView_nickname.setText(username);

                        if (record_bestTime >= Const.MASTER_SECONDS) {
                            textView_grade.setText("마스터");
                            textView_grade.setTextColor(Color.parseColor("#FF7FF7"));
                            imageView_nextgrade.setImageResource(R.drawable.ic_master);
                            progress_nextgrade.setProgress(100);
                        } else if (record_bestTime >= Const.DIAMOND_SECONDS) {
                            textView_grade.setText("다이아");
                            textView_grade.setTextColor(Color.parseColor("#70D1F4"));
                            imageView_nextgrade.setImageResource(R.drawable.ic_master);
                            progress_nextgrade.setProgress((int)((double)(record_bestTime - Const.DIAMOND_SECONDS) / (Const.MASTER_SECONDS - Const.DIAMOND_SECONDS) * 100));
                        } else if (record_bestTime >= Const.PLATINUM_SECONDS) {
                            textView_grade.setText("플래티넘");
                            textView_grade.setTextColor(Color.parseColor("#00C9C9"));
                            imageView_nextgrade.setImageResource(R.drawable.ic_diamond);
                            progress_nextgrade.setProgress((int)((double)(record_bestTime - Const.PLATINUM_SECONDS) / (Const.DIAMOND_SECONDS-Const.PLATINUM_SECONDS) * 100));
                        } else if (record_bestTime >= Const.GOLD_SECONDS) {
                            textView_grade.setText("골드");
                            textView_grade.setTextColor(Color.parseColor("#FFD700"));
                            imageView_nextgrade.setImageResource(R.drawable.ic_platinum);
                            progress_nextgrade.setProgress((int)((double)(record_bestTime - Const.GOLD_SECONDS) / (Const.PLATINUM_SECONDS - Const.GOLD_SECONDS) * 100));
                        } else if (record_bestTime >= Const.SILVER_SECONDS) {
                            textView_grade.setText("실버");
                            textView_grade.setTextColor(Color.parseColor("#E1E1E1"));
                            imageView_nextgrade.setImageResource(R.drawable.ic_gold);
                            progress_nextgrade.setProgress((int)((double)(record_bestTime - Const.SILVER_SECONDS) / (Const.GOLD_SECONDS - Const.SILVER_SECONDS) * 100));
                        } else {
                            textView_grade.setText("브론즈");
                            textView_grade.setTextColor(Color.parseColor("#E6A05A"));
                            imageView_nextgrade.setImageResource(R.drawable.ic_silver);
                            progress_nextgrade.setProgress((int)((double)record_bestTime / Const.SILVER_SECONDS * 100));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }






                } else {
                    util.checkHttpResult(result);
                }
            }
        };

        HttpAsyncTask loadRecordTask = new HttpAsyncTask(getActivity(), onLoadRecordTaskCompleted);
        String phpFile = "service 1.1.0.php";
        String postParameters = "service=getrecordactivityinfos";

        loadRecordTask.execute(phpFile, postParameters, util.getSessionKey());

        return view;
    }
}