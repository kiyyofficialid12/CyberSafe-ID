package id.kiyyofficial.cybersafe;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class MainActivity extends Activity {
    final int BG = Color.rgb(3, 8, 20);
    final int PANEL = Color.rgb(8, 19, 39);
    final int PANEL2 = Color.rgb(12, 30, 55);
    final int CYAN = Color.rgb(40, 220, 255);
    final int BLUE = Color.rgb(53, 116, 255);
    final int GREEN = Color.rgb(62, 255, 177);
    final int RED = Color.rgb(255, 78, 111);
    final int MUTED = Color.rgb(137, 163, 195);
    final int WHITE = Color.rgb(237, 247, 255);

    LinearLayout root, content;
    android.content.SharedPreferences prefs;
    String page = "home";
    int completed;
    static final int PICK_APK = 404;

    int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("cybersafe", MODE_PRIVATE);
        completed = prefs.getInt("completed", 0);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(BG);
            getWindow().setNavigationBarColor(BG);
        }
        show("home");
    }

    GradientDrawable bg(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }

    GradientDrawable grad(int a, int b, int radius) {
        GradientDrawable d = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR, new int[]{a, b});
        d.setCornerRadius(dp(radius));
        return d;
    }

    TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setIncludeFontPadding(true);
        if (bold) t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return t;
    }

    LinearLayout col() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    void pad(View v, int a, int b, int c, int d) {
        v.setPadding(dp(a), dp(b), dp(c), dp(d));
    }

    void gap(LinearLayout p, int h) {
        View v = new View(this);
        p.addView(v, new LinearLayout.LayoutParams(1, dp(h)));
    }

    void show(String target) {
        page = target;
        root = col();
        root.setBackgroundColor(BG);
        pad(root, 15, 5, 15, 5);
        setContentView(root);

        header();

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        content = col();
        pad(content, 0, 8, 0, 20);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        if (target.equals("scan")) scanner();
        else if (target.equals("academy")) academy();
        else if (target.equals("lab")) lab();
        else if (target.equals("progress")) progress();
        else home();

        navigation();
    }

    void header() {
        LinearLayout h = row();
        pad(h, 2, 7, 2, 9);

        TextView logo = text("⌬", 31, CYAN, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(bg(PANEL2, 12, Color.rgb(20, 92, 143)));
        h.addView(logo, new LinearLayout.LayoutParams(dp(47), dp(47)));

        LinearLayout brand = col();
        pad(brand, 10, 0, 0, 0);
        brand.addView(text("CYBERSAFE // ID", 15, WHITE, true));
        brand.addView(text("CYBER COMMAND CENTER", 9, CYAN, true));
        h.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        LinearLayout status = col();
        status.setGravity(Gravity.CENTER);
        status.addView(text("● ONLINE", 9, GREEN, true));
        status.addView(text("SEC LAB", 9, MUTED, false));
        h.addView(status);

        root.addView(h);
        View line = new View(this);
        line.setBackgroundColor(Color.rgb(23, 65, 99));
        root.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
    }

    void navigation() {
        LinearLayout n = row();
        n.setBackground(bg(Color.rgb(7, 18, 37), 15,
                           Color.rgb(24, 65, 99)));
        pad(n, 3, 4, 3, 4);

        String[] titles = {"⌂\nBASE", "⌕\nSCAN", "⌘\nCODE", "◎\nLAB"};
        String[] targets = {"home", "scan", "academy", "lab"};

        for (int i = 0; i < targets.length; i++) {
            final String to = targets[i];
            LinearLayout item = col();
            item.setGravity(Gravity.CENTER);
            boolean active = page.equals(to);
            item.setBackground(bg(active ? Color.rgb(10, 53, 82) :
                Color.TRANSPARENT, 10,
                active ? Color.rgb(29, 153, 205) : 0));
            TextView t = text(titles[i], 10, active ? CYAN : MUTED, active);
            t.setGravity(Gravity.CENTER);
            item.addView(t, new LinearLayout.LayoutParams(-1, dp(45)));
            n.addView(item, new LinearLayout.LayoutParams(0, dp(45), 1));
            item.setOnClickListener(v -> show(to));
        }

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(5), 0, dp(2));
        root.addView(n, p);
    }

    void section(String title, String tag) {
        LinearLayout r = row();
        r.addView(text(title, 15, WHITE, true),
            new LinearLayout.LayoutParams(0, -2, 1));
        if (tag != null) r.addView(text(tag, 9, CYAN, true));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(18), 0, dp(8));
        content.addView(r, p);
    }

    void panel(LinearLayout parent, View child) {
        LinearLayout box = col();
        pad(box, 13, 13, 13, 13);
        box.setBackground(bg(PANEL, 15, Color.rgb(24, 58, 89)));
        box.addView(child);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(5), 0, dp(6));
        parent.addView(box, p);
    }

    void module(String icon, String title, String desc,
                String status, Runnable click) {
        LinearLayout box = col();
        pad(box, 12, 12, 12, 12);
        box.setBackground(bg(PANEL, 15, Color.rgb(22, 55, 86)));

        LinearLayout head = row();
        TextView symbol = text(icon, 23, CYAN, true);
        symbol.setGravity(Gravity.CENTER);
        symbol.setBackground(bg(Color.rgb(10, 40, 66), 11, 0));
        head.addView(symbol, new LinearLayout.LayoutParams(dp(44), dp(44)));

        LinearLayout titles = col();
        pad(titles, 10, 0, 0, 0);
        titles.addView(text(title, 12, WHITE, true));
        titles.addView(text(status, 9, GREEN, true));
        head.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));
        head.addView(text("›", 25, CYAN, true));
        box.addView(head);

        TextView d = text(desc, 11, MUTED, false);
        d.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(10), 0, 0);
        box.addView(d, p);

        LinearLayout.LayoutParams outer = new LinearLayout.LayoutParams(-1, -2);
        outer.setMargins(0, dp(4), 0, dp(5));
        content.addView(box, outer);
        box.setOnClickListener(v -> click.run());
    }

    void button(LinearLayout parent, String title, Runnable click) {
        TextView b = text("  " + title + "   ›", 12, WHITE, true);
        b.setGravity(Gravity.CENTER);
        pad(b, 12, 12, 12, 12);
        b.setBackground(grad(Color.rgb(8, 111, 164),
                             Color.rgb(35, 67, 181), 11));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(6), 0, dp(6));
        parent.addView(b, p);
        b.setOnClickListener(v -> click.run());
    }

    void home() {
        content.addView(text("SYSTEM / OVERVIEW", 10, CYAN, true));
        gap(content, 5);
        content.addView(text("ENTER THE\nCYBER ZONE", 29, WHITE, true));
        TextView intro = text(
            "Belajar. Analisis. Bertahan. Tingkatkan kemampuan digitalmu.",
            12, MUTED, false);
        intro.setLineSpacing(dp(2), 1f);
        content.addView(intro);

        gap(content, 13);

        LinearLayout hero = row();
        pad(hero, 13, 14, 10, 14);
        hero.setBackground(grad(Color.rgb(7, 39, 68),
                                Color.rgb(10, 19, 43), 17));

        LinearLayout info = col();
        info.addView(text("◈  COMMAND CENTER", 10, CYAN, true));
        gap(info, 5);
        info.addView(text("DEFENSE MODE", 17, WHITE, true));
        info.addView(text("EDUCATION ENVIRONMENT", 9, GREEN, true));
        gap(info, 8);
        info.addView(text("Local analysis • Safe training", 10, MUTED, false));
        hero.addView(info, new LinearLayout.LayoutParams(0, -2, 1));

        Radar radar = new Radar(this);
        hero.addView(radar, new LinearLayout.LayoutParams(dp(116), dp(116)));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.setMargins(0, dp(13), 0, dp(5));
        content.addView(hero, hp);

        LinearLayout metrics = row();
        metric(metrics, "04", "MODULES");
        metric(metrics, String.format(Locale.US, "%02d", completed), "LESSONS");
        metric(metrics, "LOCAL", "PROCESSING");
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(-1, -2);
        mp.setMargins(0, dp(8), 0, 0);
        content.addView(metrics, mp);

        section("MISSION SELECT", "SELECT MODULE");
        module("⌕", "THREAT ANALYZER",
            "Pilih APK dan periksa hash, metadata, serta izin yang diminta.",
            "FILE SCANNER", () -> show("scan"));
        module("⌘", "CODE TRAINING",
            "Java, Python, Android, jaringan, dan secure coding.",
            "LEARNING PATH", () -> show("academy"));
        module("◎", "SIMULATION LAB",
            "Skenario phishing dan respons insiden tanpa malware sungguhan.",
            "SAFE ENVIRONMENT", () -> show("lab"));
        module("▤", "PLAYER PROGRESS",
            "Pantau materi yang selesai dan ulangi pelajaran.",
            "LOCAL PROFILE", () -> show("progress"));

        gap(content, 8);
        TextView foot = text(
            "CYBERSAFE ID  /  BUILD YOUR DIGITAL DEFENSE",
            9, MUTED, true);
        foot.setGravity(Gravity.CENTER);
        content.addView(foot);
    }

    void metric(LinearLayout parent, String number, String caption) {
        LinearLayout box = col();
        pad(box, 8, 11, 6, 11);
        box.setBackground(bg(Color.rgb(8, 25, 48), 11,
                            Color.rgb(21, 54, 83)));
        box.addView(text(number, 16, CYAN, true));
        box.addView(text(caption, 8, MUTED, true));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1);
        p.setMargins(dp(2), 0, dp(2), 0);
        parent.addView(box, p);
    }

    // Animated radar made with Android Canvas. No external image or library.
    class Radar extends View {
        Paint p = new Paint(3);
        float angle = 0;
        Radar(Context c) { super(c); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f, cy = getHeight() / 2f;
            float r = Math.min(cx, cy) - dp(5);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(dp(1));
            p.setColor(Color.rgb(21, 89, 127));
            for (int i = 1; i <= 3; i++)
                canvas.drawCircle(cx, cy, r * i / 3f, p);

            canvas.drawLine(cx - r, cy, cx + r, cy, p);
            canvas.drawLine(cx, cy - r, cx, cy + r, p);

            p.setShader(new SweepGradient(cx, cy,
                new int[]{Color.TRANSPARENT, Color.TRANSPARENT,
                          Color.argb(180, 18, 211, 255)},
                new float[]{0f, .72f, 1f}));
            canvas.save();
            canvas.rotate(angle, cx, cy);
            canvas.drawCircle(cx, cy, r, p);
            canvas.restore();
            p.setShader(null);

            p.setStyle(Paint.Style.FILL);
            p.setColor(CYAN);
            canvas.drawCircle(cx + r * .36f, cy - r * .25f, dp(3), p);
            p.setColor(GREEN);
            canvas.drawCircle(cx - r * .42f, cy + r * .19f, dp(2), p);
            p.setColor(WHITE);
            canvas.drawCircle(cx, cy, dp(3), p);

            angle = (angle + 3) % 360;
            postInvalidateDelayed(55);
        }
    }

    void scanner() {
        content.addView(text("THREAT ANALYZER", 10, CYAN, true));
        gap(content, 4);
        content.addView(text("APK SCANNER", 27, WHITE, true));
        content.addView(text("Inspect a file before you trust it.", 12, MUTED, false));
        gap(content, 13);

        LinearLayout box = col();
        pad(box, 14, 16, 14, 15);
        box.setBackground(bg(PANEL, 17, Color.rgb(26, 89, 129)));
        TextView symbol = text("⌕", 42, CYAN, true);
        symbol.setGravity(Gravity.CENTER);
        box.addView(symbol, new LinearLayout.LayoutParams(-1, dp(55)));
        TextView title = text("FILE INSPECTION", 15, WHITE, true);
        title.setGravity(Gravity.CENTER);
        box.addView(title);
        TextView desc = text(
            "Choose an APK file to calculate its SHA-256 and inspect available package information.",
            11, MUTED, false);
        desc.setGravity(Gravity.CENTER);
        box.addView(desc);
        gap(box, 8);
        button(box, "SELECT APK FILE", this::pickApk);
        content.addView(box);

        section("ANALYSIS MODULES", null);
        module("01", "HASH CHECK",
            "Menghasilkan hash SHA-256 untuk membandingkan identitas file.",
            "CRYPTOGRAPHIC HASH", () -> msg("SHA-256",
                "Hash file membantu identifikasi dan perbandingan. Hash bukan penentu tunggal malware."));
        module("02", "PACKAGE DETAILS",
            "Membaca nama paket dan versi bila metadata dapat diurai.",
            "APK METADATA", () -> msg("Metadata",
                "File rusak, format tidak didukung, atau APK khusus bisa gagal dibaca."));
        module("03", "PERMISSION REVIEW",
            "Menampilkan izin yang tercantum dalam manifest.",
            "HEURISTIC CHECK", () -> msg("Permission review",
                "Izin sensitif perlu ditinjau berdasarkan konteks. Izin saja tidak membuktikan aplikasi berbahaya."));

        TextView note = text(
            "NOTICE: This scanner is an educational heuristic tool. " +
            "It does not replace an antivirus or professional malware analysis.",
            10, MUTED, false);
        content.addView(note);
    }

    void pickApk() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("*/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        try { startActivityForResult(i, PICK_APK); }
        catch (Exception e) { msg("File picker", e.getMessage()); }
    }

    void academy() {
        content.addView(text("TRAINING DATABASE", 10, CYAN, true));
        content.addView(text("CODE ACADEMY", 27, WHITE, true));
        content.addView(text(
            "Train your skills. Build responsibly. Secure by design.",
            12, MUTED, false));

        gap(content, 12);
        LinearLayout stat = col();
        pad(stat, 15, 15, 15, 15);
        stat.setBackground(grad(Color.rgb(8, 48, 76),
                                Color.rgb(8, 21, 43), 15));
        stat.addView(text("LEARNING PROGRESS", 10, CYAN, true));
        stat.addView(text(completed + " lessons completed", 21, WHITE, true));
        stat.addView(text("Progress is stored locally on this device.", 10, MUTED, false));
        content.addView(stat);

        section("TRAINING MISSIONS", "07 LESSONS");
        lesson("01", "Digital Safety",
            "Gunakan kata sandi unik, autentikasi dua faktor, pembaruan perangkat, " +
            "dan jangan membagikan kode OTP.");
        lesson("02", "Python Fundamentals",
            "Contoh: nama = 'Kiyy' lalu print(nama). Pelajari variabel, tipe data, " +
            "kondisi, perulangan, fungsi, dan exception.");
        lesson("03", "Java Fundamentals",
            "Contoh: System.out.println(\"Halo dunia\");. Pelajari class, object, " +
            "method, koleksi, dan penanganan error.");
        lesson("04", "Android Development",
            "Pelajari Activity, Intent, lifecycle, izin aplikasi, penyimpanan, " +
            "pengujian, dan proses build APK.");
        lesson("05", "Networking",
            "Kenali IP, DNS, TCP/UDP, HTTPS, router, firewall, dan dasar diagnostik jaringan.");
        lesson("06", "Secure Coding",
            "Validasi input, least privilege, pengelolaan rahasia, pembaruan dependensi, " +
            "dan pemeriksaan izin.");
        lesson("07", "Incident Response",
            "Catat gejala, hindari file mencurigakan, amankan akun lewat perangkat tepercaya, " +
            "dan ikuti prosedur pemulihan.");

        section("QUICK CHALLENGE", null);
        module("?", "PHISHING DETECTOR",
            "Pilih tindakan terbaik saat menerima pesan yang meminta kode OTP.",
            "INTERACTIVE QUIZ", this::quiz);
    }

    void lesson(String id, String title, String desc) {
        boolean done = prefs.getBoolean("lesson_" + id, false);
        module(id, title, desc, done ? "COMPLETED" : "TRAINING MODULE",
            () -> new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(desc + "\n\nGunakan pengetahuan ini secara etis dan hanya pada sistem yang kamu punya izin untuk uji.")
                .setNegativeButton("Back", null)
                .setPositiveButton(done ? "Close" : "Mark completed",
                    (d, w) -> {
                        if (!prefs.getBoolean("lesson_" + id, false)) {
                            completed++;
                            prefs.edit().putBoolean("lesson_" + id, true)
                                .putInt("completed", completed).apply();
                        }
                        show("academy");
                    }).show());
    }

    void quiz() {
        String[] opts = {
            "Klik tautan dan masukkan kata sandi",
            "Periksa domain melalui kanal resmi",
            "Kirim kode OTP kepada pengirim"
        };
        new AlertDialog.Builder(this)
            .setTitle("MISSION / PHISHING")
            .setSingleChoiceItems(opts, -1, (d, which) -> {
                d.dismiss();
                msg(which == 1 ? "MISSION COMPLETE" : "MISSION FAILED",
                    which == 1
                    ? "Benar. Verifikasi melalui kanal resmi dan jangan bagikan OTP."
                    : "Coba lagi. Jangan memasukkan kata sandi lewat tautan mencurigakan atau membagikan OTP.");
            })
            .setNegativeButton("Cancel", null).show();
    }

    void lab() {
        content.addView(text("SIMULATION ENVIRONMENT", 10, CYAN, true));
        content.addView(text("MISSION LAB", 27, WHITE, true));
        content.addView(text(
            "Training scenarios only. No real malware is executed.",
            12, MUTED, false));

        gap(content, 12);
        LinearLayout warning = col();
        pad(warning, 13, 13, 13, 13);
        warning.setBackground(bg(Color.rgb(37, 27, 18), 13,
                                Color.rgb(140, 88, 36)));
        warning.addView(text("⚠ SAFE SIMULATION MODE", 12,
                             Color.rgb(255, 191, 91), true));
        warning.addView(text(
            "Simulasi ini bersifat edukatif dan tidak mengubah perangkat.",
            11, MUTED, false));
        content.addView(warning);

        section("SELECT A MISSION", null);
        scenario("01", "Phishing Alert",
            "Pesan mendesak meminta kata sandi atau OTP.",
            new String[]{"Klik tautan", "Periksa melalui kanal resmi", "Kirim OTP"},
            1, "Periksa domain, jangan bagikan OTP, dan buka situs resmi secara langsung.");
        scenario("02", "Suspicious App",
            "Aplikasi meminta izin yang tidak sesuai fungsinya.",
            new String[]{"Setujui semuanya", "Tinjau izin dan sumber aplikasi", "Abaikan peringatan"},
            1, "Tinjau izin dan sumber aplikasi. Jangan instal file yang tidak tepercaya.");
        scenario("03", "Incident Response",
            "Kamu melihat aktivitas akun yang tidak dikenali.",
            new String[]{"Abaikan", "Amankan akun dari perangkat tepercaya", "Instal aplikasi acak"},
            1, "Gunakan perangkat tepercaya, amankan akun, catat gejala, dan minta bantuan jika perlu.");
        scenario("04", "Privacy Defense",
            "Orang tak dikenal meminta PIN atau kode verifikasi.",
            new String[]{"Kirim kode", "Verifikasi lewat kanal resmi", "Kirim data pribadi"},
            1, "Jangan membagikan PIN, OTP, atau data pribadi kepada pihak yang belum terverifikasi.");
    }

    void scenario(String id, String title, String desc, String[] opts,
                  int correct, String explain) {
        module(id, title, desc, "START MISSION", () ->
            new AlertDialog.Builder(this)
                .setTitle("MISSION / " + title)
                .setMessage(desc + "\n\nPilih respons paling aman.")
                .setSingleChoiceItems(opts, -1, (d, which) -> {
                    d.dismiss();
                    msg(which == correct ? "MISSION PASSED" : "TRY AGAIN",
                        (which == correct ? "Pilihan tepat.\n\n" :
                            "Pilihan tersebut belum aman.\n\n") + explain);
                })
                .setNegativeButton("Back", null).show());
    }

    void progress() {
        content.addView(text("PLAYER PROFILE", 10, CYAN, true));
        content.addView(text("TRAINING RECORD", 26, WHITE, true));
        gap(content, 12);

        LinearLayout box = col();
        pad(box, 17, 17, 17, 17);
        box.setBackground(grad(Color.rgb(8, 47, 77),
                               Color.rgb(7, 19, 40), 16));
        box.addView(text("TOTAL LESSONS COMPLETED", 10, CYAN, true));
        box.addView(text(String.valueOf(completed), 40, WHITE, true));
        box.addView(text("Stored on this device", 11, MUTED, false));
        content.addView(box);

        section("AVAILABLE MODULES", null);
        module("⌕", "Threat Analyzer", "Analisis APK lokal.",
            "AVAILABLE", () -> show("scan"));
        module("⌘", "Code Academy", "Belajar coding dan keamanan.",
            "AVAILABLE", () -> show("academy"));
        module("◎", "Mission Lab", "Simulasi keamanan aman.",
            "AVAILABLE", () -> show("lab"));

        button(content, "RESET TRAINING PROGRESS", () ->
            new AlertDialog.Builder(this)
                .setTitle("Reset progress?")
                .setMessage("Semua penanda materi selesai akan dihapus.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset", (d, w) -> {
                    prefs.edit().clear().apply();
                    completed = 0;
                    show("progress");
                }).show());
    }

    void msg(String title, String body) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(body)
            .setPositiveButton("OK", null).show();
    }

    String displayName(Uri uri) {
        String name = "Selected APK";
        try (Cursor c = getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (i >= 0) name = c.getString(i);
            }
        } catch (Exception ignored) {}
        return name;
    }

    String sha256(File f) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream in = new FileInputStream(f)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) md.update(buf, 0, n);
        }
        StringBuilder out = new StringBuilder();
        for (byte b : md.digest()) out.append(String.format("%02x", b));
        return out.toString();
    }

    @Override protected void onActivityResult(int req, int result, Intent data) {
        super.onActivityResult(req, result, data);
        if (req != PICK_APK || result != RESULT_OK ||
            data == null || data.getData() == null) return;

        Uri uri = data.getData();
        File temp = null;
        try {
            temp = new File(getCacheDir(), "cybersafe-scan.apk");
            try (InputStream in = getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(temp)) {
                if (in == null) throw new IOException("Cannot read selected file");
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            }

            String name = displayName(uri);
            String hash = sha256(temp);
            PackageInfo pi = getPackageManager().getPackageArchiveInfo(
                temp.getAbsolutePath(), PackageManager.GET_PERMISSIONS);
            StringBuilder report = new StringBuilder();
            report.append("FILE: ").append(name)
                .append("\nSIZE: ").append(temp.length()).append(" bytes")
                .append("\n\nSHA-256:\n").append(hash).append("\n\n");

            int score = 0;
            if (pi == null) {
                report.append("Package metadata could not be parsed. The file may be invalid, unsupported, or not an APK.\n");
                score += 2;
            } else {
                report.append("PACKAGE: ").append(pi.packageName)
                    .append("\nVERSION: ").append(pi.versionName)
                    .append("\n\nREQUESTED PERMISSIONS:\n");

                String[] perms = pi.requestedPermissions;
                if (perms == null || perms.length == 0) {
                    report.append("No permissions detected.\n");
                } else {
                    for (String perm : perms) {
                        report.append("• ").append(perm).append("\n");
                        String s = perm.toLowerCase(Locale.ROOT);
                        if (s.contains("request_install_packages") ||
                            s.contains("read_sms") || s.contains("send_sms") ||
                            s.contains("record_audio") ||
                            s.contains("read_call_log") ||
                            s.contains("accessibility")) score++;
                    }
                }
            }

            report.append("\nHEURISTIC INDICATORS: ")
                .append(score >= 3 ? "REVIEW RECOMMENDED" :
                    score > 0 ? "CAUTION" : "NO BASIC RULE TRIGGERED")
                .append("\n\nThis is not an antivirus verdict. Permissions alone do not prove malware. No indicators found does not guarantee safety.");

            msg("ANALYSIS REPORT", report.toString());
        } catch (Exception e) {
            msg("SCAN ERROR", "Could not inspect the file:\n" + e.getMessage());
        } finally {
            if (temp != null && temp.exists()) temp.delete();
        }
    }
}
