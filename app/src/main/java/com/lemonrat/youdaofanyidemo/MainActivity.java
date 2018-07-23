package com.lemonrat.youdaofanyidemo;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lemonrat.youdaofanyidemo.model.Word;
import com.lemonrat.youdaofanyidemo.mvp.MvpMainView;
import com.lemonrat.youdaofanyidemo.mvp.impl.MainPresenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, MvpMainView {

    private static final String TAG = "MainActivity";
    private Toolbar mToolbar;
    private EditText mWord_et;
    private ImageButton mDelete_btn;
    private ImageButton mCamera_btn;
    private ImageButton mLookup_btn;
    private FloatingActionButtonContainerView mFabContainer;
    private FloatingActionButton mSwitchFab;
    private FloatingActionButton mClearFab;
    private FloatingActionButton mSettingFab;
    private MainPresenter mMainPresenter;
    private ProgressDialog mProgressDialog;
    private TextView mTranslation;
    private TextView mTrans_title;
    private RelativeLayout mResult_layout;
    private TextView mUs_phonetic_tv;
    private TextView mExplains_tv;
    private TextView mWeb_tv;
    private ImageButton mUs_phonetic_btn;
    private TextView mUk_phonetic_tv;
    private ImageButton mUk_phonetic_btn;
    private AnimationDrawable mAnimationDrawable;
    private MediaPlayer mMediaPlayer;
    private ImageButton mCopy_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lemon 翻译");
        mToolbar.inflateMenu(R.menu.menu_about);

        initListener();

        mMainPresenter = new MainPresenter(this);
        mMainPresenter.attach(this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        mToolbar = (Toolbar)findViewById(R.id.title_toolbar);
        mWord_et = (EditText)findViewById(R.id.word_et);
        mDelete_btn = (ImageButton)findViewById(R.id.delete_btn);
        mCamera_btn = (ImageButton)findViewById(R.id.camera_btn);
        mLookup_btn = (ImageButton)findViewById(R.id.lookup_btn);
        mFabContainer = (FloatingActionButtonContainerView) findViewById(R.id.fab_container);
        mSwitchFab =(FloatingActionButton)mFabContainer.getChildAt(1);
        mClearFab = (FloatingActionButton)mFabContainer.getChildAt(2);
        mSettingFab = (FloatingActionButton)mFabContainer.getChildAt(3);
        mTrans_title = (TextView)findViewById(R.id.translation_title_tv);
        mTranslation = (TextView)findViewById(R.id.translation_tv);
        mTrans_title.setVisibility(View.INVISIBLE);
        mResult_layout = (RelativeLayout)findViewById(R.id.result_layout);
        mResult_layout.setVisibility(View.INVISIBLE);
        mUs_phonetic_tv = (TextView)findViewById(R.id.us_phonetic_tv);
        mUs_phonetic_btn = (ImageButton)findViewById(R.id.us_phonetic_btn);
        mUk_phonetic_tv = (TextView)findViewById(R.id.uk_phonetic_tv);
        mUk_phonetic_btn = (ImageButton)findViewById(R.id.uk_phonetic_btn);

        mExplains_tv = (TextView)findViewById(R.id.explains_tv);
        mWeb_tv = (TextView)findViewById(R.id.web_tv);

        mCopy_btn = (ImageButton)findViewById(R.id.copy_btn);
        mCopy_btn.setVisibility(View.INVISIBLE);
    }


    private void initListener() {
        mDelete_btn.setOnClickListener(this);
        mCamera_btn.setOnClickListener(this);
        mLookup_btn.setOnClickListener(this);

        mSwitchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击了切换",Toast.LENGTH_SHORT).show();
            }
        });
        mClearFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击了清除",Toast.LENGTH_SHORT).show();
            }
        });
        mSettingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "点击了设置",Toast.LENGTH_SHORT).show();
            }
        });


    }



    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.delete_btn:
                mWord_et.setText("");
                break;
            case R.id.camera_btn:

                break;

            case R.id.lookup_btn:
                String input = mWord_et.getText().toString().trim();
                mMainPresenter.searchWordInfo(input);

                break;



            default: break;
        }



    }

    private void copyTheTranslationText(String str) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Label",str);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
    }

    private void hideResult() {
        mTrans_title.setVisibility(View.INVISIBLE);
        mCopy_btn.setVisibility(View.INVISIBLE);
        mResult_layout.setVisibility(View.INVISIBLE);
    }

    private void showResult() {
        mTrans_title.setVisibility(View.VISIBLE);

        mResult_layout.setVisibility(View.VISIBLE);
    }


    //MvpMainView接口的方法
    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateView() {
        final Word word = mMainPresenter.getWordInfo();
        if(word != null) {
            String[] value;
            String us_phonetic;
            String uk_phonetic;
            final String translation;


            value = word.getTranslation();
            translation = putStringTogetherWithBlank(value);
            mTranslation.setText(translation);

            mCopy_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyTheTranslationText(translation);
                }
            });

            if (!word.getIsChinese()) {
                us_phonetic = word.getUs_phonetic();
                uk_phonetic = word.getUk_phonetic();
                mUs_phonetic_tv.setText("美："+us_phonetic);
                mUk_phonetic_tv.setText("英："+uk_phonetic);
                showPhoneticButton();
            } else {
                if(word.getChs_phonetic() != null) {
                    mUs_phonetic_tv.setText("拼音："+word.getChs_phonetic());
                    mUs_phonetic_btn.setVisibility(View.VISIBLE);
                    mUs_phonetic_btn.setClickable(true);
                    mUk_phonetic_tv.setText("");
                    mUk_phonetic_btn.setVisibility(View.INVISIBLE);
                    mUk_phonetic_btn.setClickable(false);

                } else {
                    mUs_phonetic_tv.setText("");
                    mUk_phonetic_tv.setText("");
                    hidePhoneticButton();
                }
            }

            mUs_phonetic_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVoiceAnimation(mUs_phonetic_btn);
                    try {
                        if(word.getUs_phonetic_url() != null) {

                            playPhonetic(word.getUs_phonetic_url());
                        }

                        if(word.getChs_phonetic_url() != null) {
                            playPhonetic(word.getChs_phonetic_url());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            mUk_phonetic_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVoiceAnimation(mUk_phonetic_btn);
                    try {
                        if(word.getUk_phonetic_url() != null) {

                            playPhonetic(word.getUk_phonetic_url());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });




            value = word.getExplains();
            mExplains_tv.setText(putStringTogetherWithLine(value));


            mWeb_tv.setText(putMapStringTogether(word.getWeb()));
            mResult_layout.setVisibility(View.VISIBLE);
            mCopy_btn.setVisibility(View.VISIBLE);


        } else {
            mTranslation.setText("未查询到相关释义");
            mResult_layout.setVisibility(View.INVISIBLE);
        }



    }

    private void playPhonetic(String url) throws IOException {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.prepareAsync();

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer.start();
            }
        });
    }

    private void playVoiceAnimation(ImageButton imageButton) {
        imageButton.setImageResource(R.drawable.animation);
        mAnimationDrawable = (AnimationDrawable)imageButton.getDrawable();
        mAnimationDrawable.start();
    }

    private void hidePhoneticButton() {
        mUs_phonetic_btn.setVisibility(View.INVISIBLE);
        mUs_phonetic_btn.setClickable(false);
        mUk_phonetic_btn.setVisibility(View.INVISIBLE);
        mUk_phonetic_btn.setClickable(false);
    }

    private void showPhoneticButton() {
        mUs_phonetic_btn.setVisibility(View.VISIBLE);
        mUs_phonetic_btn.setClickable(true);
        mUk_phonetic_btn.setVisibility(View.VISIBLE);
        mUk_phonetic_btn.setClickable(true);
    }

    private String putMapStringTogether(Map<String, ArrayList<String>> map) {
        if (map == null) return null;
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, ArrayList<String>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = iterator.next();
            sb.append(entry.getKey());
            sb.append('\n');
            ArrayList<String> values = entry.getValue();
            for (String value : values) {
                sb.append(value);
                sb.append(", ");
            }
            sb = new StringBuilder(sb.subSequence(0,sb.length()-2));
            sb.append('\n');
            sb.append('\n');
        }
        Log.d(TAG, "web: "+sb.toString());
        return sb.toString();
    }

    private String putStringTogetherWithLine(String[] value) {
        if (value != null) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < value.length; i++ )
            {
                if(value[i] == null)
                {
                    sb.append('\n');
                    break;
                }
                sb.append(value[i]);
                sb.append('\n');

            }
           return sb.toString();
        }
        return null;
    }

    private String putStringTogetherWithBlank(String[] value) {
        if (value != null) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < value.length; i++ )
            {
                if(value[i] == null)
                {
                    sb.append('\n');
                    break;
                }
                sb.append(value[i]);
                sb.append("  ");
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = mProgressDialog.show(this,"","正在加载...",true,false);
            hideResult();
        } else if(mProgressDialog.isShowing()){
            mProgressDialog.setTitle("");
            mProgressDialog.setMessage("正在加载...");
            hideResult();
        }
    }

    @Override
    public void hidenLoading() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            showResult();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
