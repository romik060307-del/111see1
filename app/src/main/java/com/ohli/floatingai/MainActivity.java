package com.ohli.floatingai;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int REQ_OVERLAY = 4101;
    private static final int REQ_NOTIFICATIONS = 4102;
    private SecretStore secrets;
    private EditText geminiInput;
    private EditText deepSeekInput;
    private TextView geminiState;
    private TextView deepSeekState;

    private final int navy = Color.rgb(13, 34, 56);
    private final int teal = Color.rgb(32, 182, 140);
    private final int ink = Color.rgb(28, 41, 54);
    private final int muted = Color.rgb(112, 126, 138);
    private final int pale = Color.rgb(244, 247, 248);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        secrets = new SecretStore(this);
        buildScreen();
        updateKeyStates();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(pale);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22), dp(28), dp(22), dp(28));
        scroll.addView(page);

        TextView eyebrow = text("FLOATING AI ASSISTANT", 11, teal, true);
        eyebrow.setLetterSpacing(0.12f);
        page.addView(eyebrow);
        TextView title = text("AI yang selalu\nsiap membantu.", 30, navy, true);
        title.setPadding(0, dp(8), 0, dp(8));
        page.addView(title);
        TextView intro = text("Buka tombol lingkaran di atas aplikasi apa pun untuk bertanya, berpindah AI, atau menganalisis screenshot layar.", 14, muted, false);
        intro.setLineSpacing(dp(3), 1f);
        page.addView(intro);

        LinearLayout hero = card();
        hero.setBackground(round(navy, 22));
        hero.setPadding(dp(18), dp(18), dp(18), dp(18));
        LinearLayout orbRow = new LinearLayout(this);
        orbRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView orb = text("✦", 27, Color.WHITE, true);
        orb.setGravity(Gravity.CENTER);
        orb.setBackground(round(teal, 100));
        LinearLayout.LayoutParams orbLp = new LinearLayout.LayoutParams(dp(58), dp(58));
        orbRow.addView(orb, orbLp);
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(14), 0, 0, 0);
        labels.addView(text("AI Assist Bubble", 17, Color.WHITE, true));
        labels.addView(text("Ketuk sekali untuk membuka panel", 12, Color.rgb(209, 223, 232), false));
        orbRow.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
        hero.addView(orbRow);
        TextView heroNote = text("Panel ringkas • Gemini • DeepSeek • Analisis layar", 12, Color.rgb(209, 223, 232), false);
        heroNote.setPadding(0, dp(15), 0, 0);
        hero.addView(heroNote);
        LinearLayout.LayoutParams heroLp = new LinearLayout.LayoutParams(-1, -2);
        heroLp.topMargin = dp(22);
        page.addView(hero, heroLp);

        TextView keysTitle = text("Koneksi AI", 19, navy, true);
        LinearLayout.LayoutParams sectionLp = new LinearLayout.LayoutParams(-1, -2);
        sectionLp.topMargin = dp(25);
        page.addView(keysTitle, sectionLp);
        page.addView(text("Masukkan API key milikmu. Kunci disimpan terenkripsi di perangkat ini.", 12, muted, false));

        LinearLayout geminiCard = card();
        geminiCard.addView(providerHeader("Gemini", "Google AI", true));
        geminiInput = secretInput("Tempel API key Gemini di sini");
        geminiCard.addView(geminiInput);
        geminiState = text("Belum terhubung", 11, muted, false);
        geminiState.setPadding(0, dp(7), 0, 0);
        geminiCard.addView(geminiState);
        LinearLayout.LayoutParams providerLp = new LinearLayout.LayoutParams(-1, -2);
        providerLp.topMargin = dp(12);
        page.addView(geminiCard, providerLp);

        LinearLayout deepCard = card();
        deepCard.addView(providerHeader("DeepSeek", "DeepSeek AI", false));
        deepSeekInput = secretInput("Tempel API key DeepSeek di sini");
        deepCard.addView(deepSeekInput);
        deepSeekState = text("Belum terhubung", 11, muted, false);
        deepSeekState.setPadding(0, dp(7), 0, 0);
        deepCard.addView(deepSeekState);
        LinearLayout.LayoutParams deepLp = new LinearLayout.LayoutParams(-1, -2);
        deepLp.topMargin = dp(12);
        page.addView(deepCard, deepLp);

        Button save = button("Simpan API key", teal, Color.WHITE);
        save.setOnClickListener(v -> saveKeys());
        LinearLayout.LayoutParams saveLp = new LinearLayout.LayoutParams(-1, dp(50));
        saveLp.topMargin = dp(14);
        page.addView(save, saveLp);

        TextView warning = text("Catatan keamanan: jangan membagikan APK yang berisi API key. Kunci yang tersimpan di perangkat tetap dapat berisiko pada perangkat yang sudah di-root atau dimodifikasi. Penggunaan API dapat dikenai biaya atau batas kuota dari penyedia.", 11, muted, false);
        warning.setLineSpacing(dp(3), 1f);
        warning.setPadding(dp(2), dp(12), dp(2), dp(2));
        page.addView(warning);

        TextView controlTitle = text("Kontrol tombol melayang", 19, navy, true);
        LinearLayout.LayoutParams controlTitleLp = new LinearLayout.LayoutParams(-1, -2);
        controlTitleLp.topMargin = dp(25);
        page.addView(controlTitle, controlTitleLp);
        Button start = button("Aktifkan Assistive AI", navy, Color.WHITE);
        start.setOnClickListener(v -> startFloating());
        LinearLayout.LayoutParams startLp = new LinearLayout.LayoutParams(-1, dp(52));
        startLp.topMargin = dp(12);
        page.addView(start, startLp);
        Button stop = button("Matikan tombol melayang", Color.WHITE, navy);
        stop.setOnClickListener(v -> {
            stopService(new Intent(this, OverlayService.class));
            toast("Tombol melayang dimatikan.");
        });
        LinearLayout.LayoutParams stopLp = new LinearLayout.LayoutParams(-1, dp(48));
        stopLp.topMargin = dp(9);
        page.addView(stop, stopLp);

        TextView small = text("Versi awal 0.1.0 • Nama dan identitas visual masih bisa disesuaikan.", 10, muted, false);
        small.setGravity(Gravity.CENTER);
        small.setPadding(0, dp(24), 0, dp(8));
        page.addView(small);

        setContentView(scroll);
    }

    private View providerHeader(String name, String subtitle, boolean gemini) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView icon = text(gemini ? "✦" : "◈", 19, Color.WHITE, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(round(gemini ? Color.rgb(112, 84, 190) : navy, 14));
        row.addView(icon, new LinearLayout.LayoutParams(dp(40), dp(40)));
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(11), 0, 0, 0);
        labels.addView(text(name, 16, navy, true));
        labels.addView(text(subtitle, 11, muted, false));
        row.addView(labels);
        return row;
    }

    private EditText secretInput(String hint) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setTextSize(13);
        input.setHint(hint);
        input.setInputType(129);
        input.setPadding(dp(12), dp(4), dp(12), dp(4));
        input.setBackground(round(Color.rgb(246, 248, 249), 12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(48));
        lp.topMargin = dp(13);
        input.setLayoutParams(lp);
        return input;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackground(round(Color.WHITE, 18));
        if (Build.VERSION.SDK_INT >= 21) card.setElevation(dp(2));
        return card;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button button(String label, int background, int foreground) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(14);
        b.setTextColor(foreground);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(round(background, 14));
        return b;
    }

    private android.graphics.drawable.GradientDrawable round(int color, int radius) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private void saveKeys() {
        String gemini = geminiInput.getText().toString().trim();
        String deep = deepSeekInput.getText().toString().trim();
        if (gemini.isEmpty() && deep.isEmpty()) {
            toast("Isi minimal satu API key baru untuk disimpan.");
            return;
        }
        try {
            if (!gemini.isEmpty()) secrets.save("gemini", gemini);
            if (!deep.isEmpty()) secrets.save("deepseek", deep);
            geminiInput.setText("");
            deepSeekInput.setText("");
            updateKeyStates();
            toast("API key berhasil disimpan secara terenkripsi di perangkat.");
        } catch (Exception e) {
            toast("Gagal menyimpan key: " + e.getMessage());
        }
    }

    private void updateKeyStates() {
        if (secrets == null) return;
        boolean hasGemini = secrets.has("gemini");
        boolean hasDeep = secrets.has("deepseek");
        if (geminiState != null) geminiState.setText(hasGemini ? "✓ API key tersimpan terenkripsi" : "Belum ada API key tersimpan");
        if (deepSeekState != null) deepSeekState.setText(hasDeep ? "✓ API key tersimpan terenkripsi" : "Belum ada API key tersimpan");
    }

    private void startFloating() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_OVERLAY);
            return;
        }
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIFICATIONS);
        }
        Intent service = new Intent(this, OverlayService.class);
        service.setAction(OverlayService.ACTION_START);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(service); else startService(service);
        toast("Assistive AI aktif. Tombol lingkaran sudah tersedia di layar.");
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_OVERLAY) {
            if (Settings.canDrawOverlays(this)) startFloating();
            else toast("Izin tampil di atas aplikasi belum diberikan.");
        }
    }

    private void toast(String text) { Toast.makeText(this, text, Toast.LENGTH_LONG).show(); }
    private int dp(float value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }
}
