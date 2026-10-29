package id.kiyyofficial.cybersafe;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(7, 13, 28));
        getWindow().setNavigationBarColor(Color.rgb(7, 13, 28));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(7, 13, 28));

        TextView brand = text("CYBERSAFE ID", 28, Color.rgb(65, 190, 255), true);
        TextView subtitle = text("BELAJAR • CEK • LINDUNGI", 12, Color.LTGRAY, true);
        subtitle.setPadding(0, dp(8), 0, dp(28));
        root.addView(brand);
        root.addView(subtitle);

        String[][] cards = {
            {"KEAMANAN DIGITAL", "Pelajari cara mengenali phishing, malware, dan izin aplikasi yang mencurigakan."},
            {"PEMERIKSAAN APK", "Versi starter ini belum memindai APK. Fitur pemeriksaan harus ditambahkan dan diuji sebelum digunakan."},
            {"AKADEMI CODING", "Mulai dari dasar pemrograman, Android, jaringan, hingga praktik pengembangan aplikasi."},
            {"SIMULASI INSIDEN", "Latihan skenario aman tanpa menjalankan malware sungguhan."}
        };
        for (String[] card : cards) {
            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);
            box.setPadding(dp(16), dp(14), dp(16), dp(14));
            box.setBackgroundColor(Color.rgb(17, 29, 51));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = dp(12);
            root.addView(box, lp);
            box.addView(text(card[0], 16, Color.WHITE, true));
            TextView desc = text(card[1], 13, Color.rgb(190, 205, 225), false);
            desc.setPadding(0, dp(7), 0, 0);
            box.addView(desc);
        }
        TextView note = text("Build otomatis melalui GitHub Actions • Starter build", 11, Color.GRAY, false);
        note.setPadding(0, dp(12), 0, 0);
        root.addView(note);
        setContentView(root);
    }
}
