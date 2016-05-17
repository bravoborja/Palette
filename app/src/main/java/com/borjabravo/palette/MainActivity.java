package com.borjabravo.palette;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

public class MainActivity extends AppCompatActivity
        implements NumberPicker.OnValueChangeListener, Palette.PaletteAsyncListener {

    private static final int PALETTE_NUM_COLORS = 24;
    private static final int MIN_VALUE_IMAGES = 1;
    private static final int MAX_VALUE_IMAGES = 7;

    @BindString(R.string.name_image)
    String nameImage;
    @BindString(R.string.path_drawable)
    String pathDrawable;
    @BindString(R.string.vibrant)
    String vibrant;
    @BindString(R.string.dark_vibrant)
    String darkVibrant;
    @BindString(R.string.light_vibrant)
    String lightVibrant;
    @BindString(R.string.muted)
    String muted;
    @BindString(R.string.dark_muted)
    String darkMuted;
    @BindString(R.string.light_muted)
    String lightMuted;
    @BindString(R.string.population_count)
    String populationCount;
    @BindString(R.string.population_swatch_undefined)
    String populationSwatchUndefined;
    @BindString(R.string.population_all_swatches)
    String populationAllSwatches;
    @BindString(R.string.image_url)
    String imageUrl;
    @BindString(R.string.error_load_image)
    String errorLoadImage;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.imgImagen)
    ImageView imgImagen;
    @BindView(R.id.txtVibrant)
    TextView txtVibrant;
    @BindView(R.id.txtDarkVibrant)
    TextView txtDarkVibrant;
    @BindView(R.id.txtLightVibrant)
    TextView txtLightVibrant;
    @BindView(R.id.txtMuted)
    TextView txtMuted;
    @BindView(R.id.txtDarkMuted)
    TextView txtDarkMuted;
    @BindView(R.id.txtLightMuted)
    TextView txtLightMuted;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.number_picker)
    NumberPicker numberPicker;
    @BindView(R.id.load_image)
    Button loadImage;
    @BindView(R.id.layout_swatches)
    LinearLayout layoutSwatches;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        if (numberPicker != null) {
            numberPicker.setMinValue(MIN_VALUE_IMAGES);
            numberPicker.setMaxValue(MAX_VALUE_IMAGES);
            numberPicker.setWrapSelectorWheel(true);
            numberPicker.setOnValueChangedListener(this);
        }
        setSupportActionBar(toolbar);
        changeLocalDrawable();
    }

    private void changeLocalDrawable() {
        imgImagen.setImageDrawable(getDrawableImage(this));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getDrawableIdentifier());
        Palette.from(bitmap).maximumColorCount(PALETTE_NUM_COLORS).generate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public Drawable getDrawableImage(Context context) {
        return ContextCompat.getDrawable(context,
                getResources().getIdentifier(nameImage + numberPicker.getValue(), pathDrawable, getPackageName()));
    }

    public int getDrawableIdentifier() {
        return getResources().getIdentifier(getPackageName() + ":drawable/" + nameImage + numberPicker.getValue(), null,
                null);
    }

    public void setStatusBarColor(Activity context, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    private void updateTextViewInfoColor(String type, TextView textView, Palette.Swatch swatch) {
        if (swatch != null) {
            textView.setBackgroundColor(swatch.getRgb());
            textView.setTextColor(swatch.getBodyTextColor());
            textView.setText(String.format(populationCount, type, swatch.getPopulation()));
        } else {
            textView.setBackgroundColor(Color.BLACK);
            textView.setTextColor(Color.WHITE);
            textView.setText(String.format(populationSwatchUndefined, type));
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        changeLocalDrawable();
    }

    @Override
    public void onGenerated(Palette palette) {
        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
        Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
        updateTextViewInfoColor(vibrant, txtVibrant, palette.getVibrantSwatch());
        updateTextViewInfoColor(darkVibrant, txtDarkVibrant, palette.getDarkVibrantSwatch());
        updateTextViewInfoColor(lightVibrant, txtLightVibrant, palette.getLightVibrantSwatch());
        updateTextViewInfoColor(muted, txtMuted, palette.getMutedSwatch());
        updateTextViewInfoColor(darkMuted, txtDarkMuted, palette.getDarkMutedSwatch());
        updateTextViewInfoColor(lightMuted, txtLightMuted, palette.getLightMutedSwatch());
        updateSwatches(palette, vibrantSwatch, darkVibrantSwatch, lightVibrantSwatch);
        updateBtnLoadImage(vibrantSwatch, darkVibrantSwatch, lightVibrantSwatch);
    }

    private void updateSwatches(Palette palette, Palette.Swatch vibrantSwatch, Palette.Swatch darkVibrantSwatch,
            Palette.Swatch lightVibrantSwatch) {
        if (darkVibrantSwatch != null) {
            toolbar.setBackgroundColor(darkVibrantSwatch.getRgb());
            toolbar.setTitleTextColor(darkVibrantSwatch.getTitleTextColor());
            setStatusBarColor(MainActivity.this, darkVibrantSwatch.getRgb());
        } else if (vibrantSwatch != null) {
            toolbar.setBackgroundColor(vibrantSwatch.getRgb());
            toolbar.setTitleTextColor(vibrantSwatch.getTitleTextColor());
            setStatusBarColor(MainActivity.this, vibrantSwatch.getRgb());
        } else if (lightVibrantSwatch != null) {
            toolbar.setBackgroundColor(lightVibrantSwatch.getRgb());
            toolbar.setTitleTextColor(lightVibrantSwatch.getTitleTextColor());
            setStatusBarColor(MainActivity.this, lightVibrantSwatch.getRgb());
        } else {
            toolbar.setBackgroundColor(Color.BLACK);
            toolbar.setTitleTextColor(Color.WHITE);
            setStatusBarColor(MainActivity.this, Color.BLACK);
        }
        Log.i("All Swatches", "All Swatches");
        for (Palette.Swatch sw : palette.getSwatches()) {
            Log.i("Palette",
                    String.format(populationAllSwatches, Integer.toHexString(sw.getRgb()).toUpperCase(), sw.getPopulation()));
        }
    }

    private void updateBtnLoadImage(Palette.Swatch vibrantSwatch, Palette.Swatch darkVibrantSwatch,
            Palette.Swatch lightVibrantSwatch) {
        if (vibrantSwatch != null) {
            loadImage.setBackgroundColor(vibrantSwatch.getRgb());
            loadImage.setTextColor(vibrantSwatch.getTitleTextColor());
        } else if (lightVibrantSwatch != null) {
            loadImage.setBackgroundColor(lightVibrantSwatch.getRgb());
            loadImage.setTextColor(lightVibrantSwatch.getTitleTextColor());
        } else if (darkVibrantSwatch != null) {
            loadImage.setBackgroundColor(darkVibrantSwatch.getRgb());
            loadImage.setTextColor(darkVibrantSwatch.getTitleTextColor());
        } else {
            loadImage.setBackgroundColor(Color.BLACK);
            loadImage.setTextColor(Color.WHITE);
        }
    }

    private String generateRandomImage() {
        int randomWidth = 200 + (int) (Math.random() * 500);
        int randomHeight = 200 + (int) (Math.random() * 500);
        StringBuilder url = new StringBuilder();
        url.append(imageUrl).append(String.valueOf(randomWidth)).append("/").append(String.valueOf(randomHeight));
        return String.valueOf(url);
    }

    @OnClick(R.id.load_image)
    public void onClickLoadImage() {
        progressBar.setVisibility(View.VISIBLE);
        layoutSwatches.setVisibility(View.INVISIBLE);
        Glide.with(this).load(generateRandomImage()).asBitmap().into(new BitmapImageViewTarget(imgImagen) {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);
                Palette palette = Palette.from(bitmap).generate();
                progressBar.setVisibility(View.GONE);
                layoutSwatches.setVisibility(View.VISIBLE);
                imgImagen.setImageBitmap(bitmap);
                updateTextViewInfoColor(vibrant, txtVibrant, palette.getVibrantSwatch());
                updateTextViewInfoColor(darkVibrant, txtDarkVibrant, palette.getDarkVibrantSwatch());
                updateTextViewInfoColor(lightVibrant, txtLightVibrant, palette.getLightVibrantSwatch());
                updateTextViewInfoColor(muted, txtMuted, palette.getMutedSwatch());
                updateTextViewInfoColor(darkMuted, txtDarkMuted, palette.getDarkMutedSwatch());
                updateTextViewInfoColor(lightMuted, txtLightMuted, palette.getLightMutedSwatch());
                updateSwatches(palette, palette.getVibrantSwatch(), palette.getDarkVibrantSwatch(),
                        palette.getLightVibrantSwatch());
                updateBtnLoadImage(palette.getVibrantSwatch(), palette.getDarkVibrantSwatch(),
                        palette.getLightVibrantSwatch());
            }
        });
    }
}