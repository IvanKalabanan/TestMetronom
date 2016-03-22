package test.ivacompany.com.testmetronom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends Activity {
    int time = 1000;
    boolean isVibro, isFlash, isSound, isSimple;
    ImageView greenImage;
    EditText editText;
    SeekBar seekBar;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initListener();
        isVibro = isFlash = isSound = isSimple = false;

    }

    private void initViews(){
        setContentView(R.layout.activity_main);
        greenImage = (ImageView) findViewById(R.id.imageGreen);
        button = (Button) findViewById(R.id.button);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        editText = (EditText) findViewById(R.id.editText);
    }

    private void initListener(){
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    time = Integer.parseInt(editText.getText().toString());
                    seekBar.setProgress(time);
                    if(isSimple) {
                        greenImage.clearAnimation();
                        indicator(time);
                        startService(new Intent(getApplication(), MyService.class).putExtra("time", time));
                    }
                    return true;
                }
                return false;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editText.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                time = seekBar.getProgress();
                if(isSimple) {
                    greenImage.clearAnimation();
                    indicator(time);
                    startService(new Intent(getApplication(), MyService.class).putExtra("time", time));
                }
            }
        });
    }

    public void buttonClick(View view) {
        if(!isSimple){
            startService(new Intent(this, MyService.class).putExtra("time", time));
            indicator(time);
            isSimple = true;
            button.setText("Stop");
        } else {
            stopService(new Intent(this, MyService.class));
            greenImage.clearAnimation();
            isVibro = isFlash = isSound = false;
            isSimple = false;
            button.setText("Start");
        }

    }

    public void flashButtonCLick(View v) {
        if(isSimple) {
            if ((isFlash) && (!isVibro) && (!isSound)) {
                startService(new Intent(this, MyService.class).putExtra("type", Code.Simple));
                isFlash = false;
            } else {
                if ((isFlash) && ((isVibro) || (isSound))) {
                    startService(new Intent(this, MyService.class).putExtra("type", Code.FlashOff));
                    isFlash = false;
                } else {
                    greenImage.clearAnimation();
                    indicator(time);
                    startService(new Intent(this, MyService.class).putExtra("type", Code.FlashOn));
                    isFlash = true;
                }
            }
        }
    }

    public void soundButtonClick(View v) {
        if(isSimple) {
            if ((isSound) && (!isFlash) && (!isVibro)) {
                startService(new Intent(this, MyService.class).putExtra("type", Code.Simple));
                isSound = false;
            } else {
                if ((isSound) && ((isFlash) || (isVibro))) {
                    startService(new Intent(this, MyService.class).putExtra("type", Code.SoundOff));
                    isSound = false;
                } else {
                    greenImage.clearAnimation();
                    indicator(time);
                    startService(new Intent(this, MyService.class).putExtra("type", Code.SoundOn));
                    isSound = true;
                }
            }
        }
    }

    public void vibroButtonClick(View v) {
        if(isSimple) {
            if ((isVibro) && (!isFlash) && (!isSound)) {
                startService(new Intent(this, MyService.class).putExtra("type", Code.Simple));
                isVibro = false;
            } else {
                if ((isVibro) && ((isFlash) || (isSound))) {
                    startService(new Intent(this, MyService.class).putExtra("type", Code.VibroOff));
                    isVibro = false;
                } else {
                    greenImage.clearAnimation();
                    indicator(time);
                    startService(new Intent(this, MyService.class).putExtra("type", Code.VibroOn));
                    isVibro = true;
                }
            }
        }
    }

    public void indicator(final int time) {

        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(time); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        greenImage.startAnimation(animation);


    }


}
