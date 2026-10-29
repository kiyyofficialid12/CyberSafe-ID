package id.kiyyofficial.cybersafe;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
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
    final int BG = Color.rgb(3, 7, 19);
    final int PANEL = Color.rgb(10, 19, 39);
    final int PANEL2 = Color.rgb(14, 29, 54);
    final int CYAN = Color.rgb(35, 224, 255);
    final int BLUE = Color.rgb(72, 104, 255);
    final int GREEN = Color.rgb(66, 255, 176);
    final int RED = Color.rgb(255, 83, 116);
    final int WHITE = Color.rgb(239, 247, 255);
    final int MUTED = Color.rgb(140, 164, 197);

    LinearLayout root, content;
    android.content.SharedPreferences prefs;
    String page = "home";
    int completed = 0;
    static final int PICK_APK = 404;

    final String[] lessonTitles = {
        "Fondasi Cybersecurity",
        "Python untuk Pemula",
        "Dasar Linux dan Termux",
        "Jaringan dan TCP/IP",
        "Web Security",
        "Android App Security",
        "Incident Response"
    };

    final String[] lessonDescriptions = {
        "Pelajari autentikasi dua faktor, kata sandi kuat, phishing, pembaruan perangkat, dan perlindungan data pribadi.",
        "Kenali variabel, tipe data, percabangan, perulangan, fungsi, dan latihan coding yang aman.",
        "Kenali terminal, direktori, file, izin akses, serta penggunaan perintah Linux secara bertanggung jawab.",
        "Pelajari IP address, DNS, port, HTTP/HTTPS, router, dan dasar pemecahan masalah jaringan.",
        "Pelajari validasi input, autentikasi, session, HTTPS, dan cara mencegah kerentanan web umum.",
        "Kenali permission Android, penyimpanan aman, tanda tangan aplikasi, dan praktik pengembangan yang aman.",
        "Pelajari cara mengenali insiden, mencatat bukti secara aman, mengisolasi perangkat, dan memulihkan sistem."
    };

    int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("cybersafe_v2", MODE_PRIVATE);
        completed = prefs.getInt("completed", 0);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(BG);
            getWindow().setNavigationBarColor(BG);
        }
        show("home");
    }

    GradientDrawable shape(int color, int radius, int stroke) {
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

    TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
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

    void gap(LinearLayout parent, int height) {
        View v = new View(this);
        parent.addView(v, new LinearLayout.LayoutParams(1, dp(height)));
    }

    void line(LinearLayout parent) {
        View v = new View(this);
        v.setBackgroundColor(Color.rgb(28, 57, 87));
        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1, dp(1));
        p.setMargins(0, dp(10), 0, dp(10));
        parent.addView(v, p);
    }

    void show(String target) {
        page = target;
        root = col();
        root.setBackgroundColor(BG);
        pad(root, 15, 7, 15, 5);
        setContentView(root);

        LinearLayout header = row();
        TextView logo = text("⌬", 32, CYAN, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(shape(PANEL2, 13, Color.rgb(31, 113, 151)));
        header.addView(logo, new LinearLayout.LayoutParams(dp(48), dp(46)));

        LinearLayout brand = col();
        pad(brand, 10, 0, 0, 0);
        brand.addView(text("CYBERSAFE // ID", 15, WHITE, true));
        brand.addView(text("CYBER GAMING SECURITY SYSTEM", 8, CYAN, true));
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        TextView status = text("● ONLINE", 9, GREEN, true);
        status.setGravity(Gravity.CENTER);
        status.setBackground(shape(Color.rgb(8, 43, 40), 9, Color.rgb(29, 112, 92)));
        pad(status, 9, 8, 9, 8);
        header.addView(status);
        root.addView(header);

        line(root);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        content = col();
        pad(content, 0, 3, 0, 16);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        if ("scan".equals(target)) scannerPage();
        else if ("learn".equals(target)) academyPage();
        else if ("lab".equals(target)) labPage();
        else if ("progress".equals(target)) progressPage();
        else homePage();

        buildNav();
    }

    void buildNav() {
        LinearLayout nav = row();
        nav.setGravity(Gravity.CENTER);
        nav.setBackground(shape(PANEL, 16, Color.rgb(30, 61, 95)));
        pad(nav, 3, 4, 3, 4);

        String[] labels = {"⌂\nHOME", "⌕\nSCAN", "⌘\nLEARN", "◎\nLAB", "▤\nDATA"};
        String[] pages = {"home", "scan", "learn", "lab", "progress"};

        for (int i = 0; i < pages.length; i++) {
            final String destination = pages[i];
            boolean selected = page.equals(destination);
            LinearLayout item = col();
            item.setGravity(Gravity.CENTER);
            item.setBackground(shape(
                selected ? Color.rgb(13, 54, 83) : Color.TRANSPARENT,
                11, selected ? CYAN : 0));
            TextView t = text(labels[i], 9, selected ? CYAN : MUTED, selected);
            t.setGravity(Gravity.CENTER);
            item.addView(t, new LinearLayout.LayoutParams(-1, dp(48)));
            nav.addView(item, new LinearLayout.LayoutParams(0, dp(48), 1));
            item.setOnClickListener(v -> show(destination));
        }

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(7), 0, 0);
        root.addView(nav, p);
    }

    void section(String title, String badge) {
        LinearLayout r = row();
        r.addView(text(title, 15, WHITE, true),
            new LinearLayout.LayoutParams(0, -2, 1));
        if (badge != null) r.addView(text(badge, 9, CYAN, true));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(17), 0, dp(8));
        content.addView(r, p);
    }

    void button(LinearLayout parent, String title, Runnable action) {
        TextView b = text(title, 12, WHITE, true);
        b.setGravity(Gravity.CENTER);
        pad(b, 12, 13, 12, 13);
        b.setBackground(grad(Color.rgb(11, 143, 195),
                             Color.rgb(48, 78, 213), 12));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(5), 0, dp(5));
        parent.addView(b, p);
        b.setOnClickListener(v -> action.run());
    }

    void module(String icon, String title, String description,
                String tag, Runnable action) {
        LinearLayout box = col();
        pad(box, 13, 13, 13, 13);
        box.setBackground(shape(PANEL, 16, Color.rgb(27, 55, 86)));

        LinearLayout head = row();
        TextView symbol = text(icon, 24, CYAN, true);
        symbol.setGravity(Gravity.CENTER);
        symbol.setBackground(shape(Color.rgb(15, 43, 70), 11, 0));
        head.addView(symbol, new LinearLayout.LayoutParams(dp(46), dp(46)));

        LinearLayout labels = col();
        pad(labels, 11, 0, 0, 0);
        labels.addView(text(title, 12, WHITE, true));
        labels.addView(text(tag, 9, GREEN, true));
        head.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
        head.addView(text("›", 25, CYAN, true));
        box.addView(head);

        TextView desc = text(description, 11, MUTED, false);
        desc.setGravity(Gravity.TOP);
        desc.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams d = new LinearLayout.LayoutParams(-1, -2);
        d.setMargins(0, dp(9), 0, 0);
        box.addView(desc, d);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(5), 0, dp(6));
        content.addView(box, p);
        box.setOnClickListener(v -> action.run());
    }

    void stat(LinearLayout parent, String value, String caption) {
        LinearLayout b = col();
        pad(b, 9, 11, 5, 11);
        b.setBackground(shape(PANEL2, 12, Color.rgb(31, 66, 102)));
        b.addView(text(value, 19, CYAN, true));
        b.addView(text(caption, 9, MUTED, false));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1);
        p.setMargins(dp(3), 0, dp(3), 0);
        parent.addView(b, p);
    }

    void homePage() {
        gap(content, 5);
        content.addView(text("SYSTEM / SECURITY / 01", 10, CYAN, true));
        gap(content, 5);
        content.addView(text("LEVEL UP YOUR\nCYBER DEFENSE", 29, WHITE, true));
        TextView intro = text(
            "Belajar keamanan digital. Analisis file. Bangun skill developer.",
            12, MUTED, false);
        intro.setLineSpacing(dp(3), 1f);
        content.addView(intro);

        gap(content, 14);

        LinearLayout hero = col();
        pad(hero, 15, 15, 15, 15);
        hero.setBackground(grad(Color.rgb(9, 49, 78),
                                Color.rgb(17, 22, 51), 20));
        hero.setBackground(shape(Color.rgb(8, 29, 52), 20,
                                 Color.rgb(30, 139, 181)));

        LinearLayout heroTop = row();
        LinearLayout heroText = col();
        heroText.addView(text("CYBER SHIELD", 10, CYAN, true));
        gap(heroText, 5);
        heroText.addView(text("Defense Hub", 21, WHITE, true));
        heroText.addView(text("EDUCATION MODE ACTIVE", 9, GREEN, true));
        heroTop.addView(heroText, new LinearLayout.LayoutParams(0, -2, 1));
        RadarView radar = new RadarView(this);
        heroTop.addView(radar, new LinearLayout.LayoutParams(dp(105), dp(105)));
        hero.addView(heroTop);

        line(hero);
        hero.addView(text("YOUR DEVICE • LOCAL LEARNING CENTER", 9, MUTED, true));
        gap(hero, 8);
        button(hero, "OPEN SECURITY SCANNER  →", () -> show("scan"));
        content.addView(hero);

        section("SYSTEM OVERVIEW", "LIVE DASHBOARD");
        LinearLayout stats = row();
        stat(stats, String.valueOf(completed), "LESSONS DONE");
        stat(stats, "04", "CORE MODULES");
        stat(stats, "LOCAL", "DATA MODE");
        content.addView(stats);
        stats.setOnClickListener(v -> show("progress"));

        section("MISSION CONTROL", "SELECT MODULE");
        module("⌕", "APK RISK SCANNER",
            "Hash SHA-256, metadata paket, versi, dan izin yang diminta APK.",
            "LOCAL FILE ANALYSIS", () -> show("scan"));
        module("⌘", "CODING ACADEMY",
            "Jalur belajar Python, Linux, jaringan, web, Android, dan respons insiden.",
            "7 LEARNING MISSIONS", () -> show("learn"));
        module("◎", "CYBER LAB",
            "Latihan mengenali phishing, aplikasi mencurigakan, dan respons insiden.",
            "SAFE SIMULATION", () -> show("lab"));
        module("▤", "PLAYER PROGRESS",
            "Pantau jumlah materi yang telah kamu selesaikan di perangkat.",
            "PERSONAL LEARNING DATA", () -> show("progress"));

        gap(content, 8);
        content.addView(text(
            "CYBERSAFE ID • TOOL EDUKASI, BUKAN ANTIVIRUS PENUH.",
            9, MUTED, false));
    }

    class RadarView extends View {
        Paint p = new Paint(3);
        RadarView(Context c) { super(c); }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f, cy = getHeight() / 2f;
            float radius = Math.min(cx, cy) - dp(5);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(dp(1));
            p.setColor(Color.rgb(24, 107, 143));
            for (int i = 1; i <= 3; i++)
                canvas.drawCircle(cx, cy, radius * i / 3f, p);
            canvas.drawLine(cx - radius, cy, cx + radius, cy, p);
            canvas.drawLine(cx, cy - radius, cx, cy + radius, p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(CYAN);
            canvas.drawCircle(cx, cy, dp(4), p);
            p.setColor(GREEN);
            canvas.drawCircle(cx + radius * .46f, cy - radius * .35f, dp(3), p);
            p.setColor(BLUE);
            canvas.drawCircle(cx - radius * .35f, cy + radius * .42f, dp(3), p);
        }
    }

    void scannerPage() {
        content.addView(text("APK RISK SCANNER", 24, WHITE, true));
        content.addView(text("INSPECT • HASH • REVIEW", 10, CYAN, true));
        gap(content, 13);

        LinearLayout panel = col();
        pad(panel, 16, 18, 16, 18);
        panel.setBackground(shape(PANEL, 19, Color.rgb(34, 92, 131)));
        TextView icon = text("⌕", 45, CYAN, true);
        icon.setGravity(Gravity.CENTER);
        panel.addView(icon);
        TextView title = text("Analyze APK File", 19, WHITE, true);
        title.setGravity(Gravity.CENTER);
        panel.addView(title);
        TextView sub = text("Pilih file APK dari penyimpanan perangkat.", 11, MUTED, false);
        sub.setGravity(Gravity.CENTER);
        panel.addView(sub);
        gap(panel, 10);
        button(panel, "SELECT APK FILE", this::pickApk);
        content.addView(panel);

        button(content, "REVIEW INSTALLED APPS", this::scanInstalledApps);
        section("ANALYSIS TOOLS", "3 CHECKS");
        module("01", "SHA-256 FINGERPRINT",
            "Menghasilkan hash untuk membandingkan identitas file.",
            "CRYPTOGRAPHIC HASH", () -> info("SHA-256",
            "Hash tidak menentukan apakah file berbahaya. Gunakan untuk membandingkan file tepercaya."));
        module("02", "PACKAGE METADATA",
            "Membaca nama paket dan versi jika metadata APK tersedia.",
            "FILE INFORMATION", () -> info("Package metadata",
            "Metadata dapat membantu mengenali aplikasi, tetapi tidak membuktikan keamanan."));
        module("03", "PERMISSION REVIEW",
            "Meninjau izin yang diminta dan menandai indikator sederhana.",
            "BASIC HEURISTICS", () -> info("Permission review",
            "Izin sensitif tidak otomatis berarti malware. Periksa konteks dan sumber aplikasi."));
        gap(content, 8);
        content.addView(text(
            "Privasi: file dianalisis secara lokal. Hasil heuristik bukan jaminan bebas malware.",
            11, MUTED, false));
    }


    void scanInstalledApps() {
        try {
            PackageManager pm = getPackageManager();
            List<PackageInfo> apps =
                pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

            Collections.sort(apps, (a, b) -> {
                String na = String.valueOf(
                    pm.getApplicationLabel(a.applicationInfo));
                String nb = String.valueOf(
                    pm.getApplicationLabel(b.applicationInfo));
                return na.compareToIgnoreCase(nb);
            });

            ArrayList<PackageInfo> visible = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();

            for (PackageInfo pi : apps) {
                if (pi.applicationInfo == null) continue;
                String name = String.valueOf(
                    pm.getApplicationLabel(pi.applicationInfo));
                labels.add(name + "\n" + pi.packageName);
                visible.add(pi);
            }

            if (visible.isEmpty()) {
                info("Installed Apps", "Tidak ada aplikasi yang dapat ditampilkan.");
                return;
            }

            new AlertDialog.Builder(this)
                .setTitle("INSTALLED APP REVIEW")
                .setItems(labels.toArray(new String[0]), (dialog, which) ->
                    showInstalledAppReport(visible.get(which)))
                .setNegativeButton("Tutup", null)
                .show();

        } catch (Exception e) {
            info("Scan Error", "Gagal membaca daftar aplikasi:\n" + e.getMessage());
        }
    }

    void showInstalledAppReport(PackageInfo pi) {
        PackageManager pm = getPackageManager();
        String appName = String.valueOf(
            pm.getApplicationLabel(pi.applicationInfo));
        StringBuilder report = new StringBuilder();
        int indicators = 0;

        report.append("APP: ").append(appName)
            .append("\nPACKAGE: ").append(pi.packageName)
            .append("\nVERSION: ").append(pi.versionName)
            .append("\n\nREQUESTED PERMISSIONS:\n");

        String[] permissions = pi.requestedPermissions;
        if (permissions == null || permissions.length == 0) {
            report.append("Tidak ada izin yang terbaca.\n");
        } else {
            for (String permission : permissions) {
                report.append("• ").append(permission).append("\n");
                String value = permission.toLowerCase(Locale.ROOT);
                if (value.contains("request_install_packages") ||
                    value.contains("read_sms") ||
                    value.contains("send_sms") ||
                    value.contains("record_audio") ||
                    value.contains("read_call_log") ||
                    value.contains("accessibility")) {
                    indicators++;
                }
            }
        }

        report.append("\nBASIC INDICATORS: ")
            .append(indicators >= 3 ? "REVIEW RECOMMENDED" :
                    indicators > 0 ? "REVIEW PERMISSIONS" :
                    "NO BASIC RULE TRIGGERED")
            .append("\n\nIndikator hanya membantu menentukan hal yang perlu diperiksa. ")
            .append("Ini bukan bukti malware dan bukan jaminan aplikasi aman.");

        info("APP SECURITY REVIEW", report.toString());
    }

    void pickApk() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("*/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        try { startActivityForResult(i, PICK_APK); }
        catch (Exception e) {
            info("File picker", "Tidak bisa membuka pemilih file: " + e.getMessage());
        }
    }

    String displayName(Uri uri) {
        String name = "selected.apk";
        try (android.database.Cursor c = getContentResolver().query(
            uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int index = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) name = c.getString(index);
            }
        } catch (Exception ignored) {}
        return name;
    }

    String sha256(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream in = new FileInputStream(file)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) md.update(buf, 0, n);
        }
        StringBuilder out = new StringBuilder();
        for (byte b : md.digest()) out.append(String.format("%02x", b));
        return out.toString();
    }

    @Override protected void onActivityResult(
        int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != PICK_APK || result != RESULT_OK ||
            data == null || data.getData() == null) return;

        Uri uri = data.getData();
        File temp = null;
        try {
            temp = new File(getCacheDir(), "cybersafe-scan.apk");
            try (InputStream in = getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(temp)) {
                if (in == null) throw new IOException("File tidak bisa dibaca.");
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            }

            StringBuilder report = new StringBuilder();
            report.append("FILE: ").append(displayName(uri))
                .append("\nSIZE: ").append(temp.length()).append(" bytes")
                .append("\n\nSHA-256:\n").append(sha256(temp)).append("\n\n");

            int score = 0;
            PackageInfo pi = getPackageManager().getPackageArchiveInfo(
                temp.getAbsolutePath(), PackageManager.GET_PERMISSIONS);

            if (pi == null) {
                report.append("Metadata paket tidak dapat dibaca. File mungkin bukan APK valid.\n");
                score += 2;
            } else {
                report.append("PACKAGE: ").append(pi.packageName)
                    .append("\nVERSION: ").append(pi.versionName)
                    .append("\n\nREQUESTED PERMISSIONS:\n");
                String[] perms = pi.requestedPermissions;
                if (perms == null || perms.length == 0) {
                    report.append("Tidak ada izin yang terdeteksi.\n");
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

            report.append("\nINDIKATOR HEURISTIK: ")
                .append(score >= 3 ? "REVIEW RECOMMENDED" :
                        score > 0 ? "CAUTION" : "NO BASIC RULE TRIGGERED")
                .append("\n\nIni bukan vonis antivirus. Tidak ada indikator bukan jaminan aman.");

            info("APK ANALYSIS REPORT", report.toString());
        } catch (Exception e) {
            info("SCAN ERROR", "File gagal dianalisis:\n" + e.getMessage());
        } finally {
            if (temp != null && temp.exists()) temp.delete();
        }
    }

    void academyPage() {
        content.addView(text("CODING ACADEMY", 25, WHITE, true));
        content.addView(text("TRAIN YOUR SKILLS • ONE LEVEL AT A TIME", 9, CYAN, true));
        gap(content, 12);

        LinearLayout p = col();
        pad(p, 14, 14, 14, 14);
        p.setBackground(shape(PANEL, 16, Color.rgb(34, 84, 122)));
        p.addView(text("LEARNING PROGRESS", 10, CYAN, true));
        p.addView(text(completed + " / " + lessonTitles.length + " missions completed",
                       19, WHITE, true));
        p.addView(text("Baca materi, lalu tandai selesai untuk menyimpan progres.",
                       11, MUTED, false));
        content.addView(p);

        section("LEARNING PATH", "7 MISSIONS");
        for (int i = 0; i < lessonTitles.length; i++) {
            final int index = i;
            boolean done = prefs.getBoolean("lesson_" + i, false);
            module(done ? "✓" : String.format(Locale.US, "%02d", i + 1),
                lessonTitles[i], lessonDescriptions[i],
                done ? "MISSION COMPLETED" : "TAP TO STUDY",
                () -> lessonDetail(index));
        }

        section("QUICK KNOWLEDGE CHECK", null);
        module("?", "Phishing Awareness",
            "Latihan singkat mengenali pesan yang mencurigakan.",
            "3 QUESTIONS", this::quiz);
    }

    void lessonDetail(int index) {
        LinearLayout box = col();
        pad(box, 5, 4, 5, 4);
        box.addView(text(lessonTitles[index], 19, CYAN, true));
        gap(box, 8);
        TextView desc = text(lessonDescriptions[index], 14, WHITE, false);
        desc.setGravity(Gravity.TOP);
        desc.setLineSpacing(dp(4), 1f);
        box.addView(desc);
        gap(box, 12);
        box.addView(text(
            "PRAKTIK: tuliskan satu hal yang kamu pelajari dan bagaimana menerapkannya secara aman.",
            12, MUTED, false));
        new AlertDialog.Builder(this)
            .setTitle("ACADEMY MISSION")
            .setView(box)
            .setNegativeButton("Kembali", null)
            .setPositiveButton(
                prefs.getBoolean("lesson_" + index, false) ? "Selesai" : "Tandai selesai",
                (d, w) -> {
                    if (!prefs.getBoolean("lesson_" + index, false)) {
                        prefs.edit().putBoolean("lesson_" + index, true).apply();
                        completed++;
                        prefs.edit().putInt("completed", completed).apply();
                    }
                    show("learn");
                }).show();
    }

    void quiz() {
        final String[] choices = {
            "Klik tautan segera karena pesan mendesak",
            "Periksa pengirim dan alamat tautan, jangan masukkan data",
            "Kirim kata sandi untuk memverifikasi akun"
        };
        new AlertDialog.Builder(this)
            .setTitle("PHISHING AWARENESS")
            .setMessage("Kamu menerima pesan mendesak yang meminta kata sandi lewat tautan. Apa tindakan paling aman?")
            .setItems(choices, (d, which) -> {
                if (which == 1) info("Jawaban benar",
                    "Bagus. Periksa alamat situs melalui kanal resmi dan jangan membagikan kata sandi atau kode OTP.");
                else info("Coba lagi",
                    "Jangan buru-buru membuka tautan atau memberikan kredensial. Verifikasi melalui aplikasi atau situs resmi.");
            }).show();
    }

    void labPage() {
        content.addView(text("CYBER LAB", 26, WHITE, true));
        content.addView(text("SAFE SIMULATION ENVIRONMENT", 10, CYAN, true));
        gap(content, 8);

        LinearLayout notice = col();
        pad(notice, 14, 14, 14, 14);
        notice.setBackground(shape(Color.rgb(42, 31, 17), 14,
                                   Color.rgb(155, 107, 39)));
        notice.addView(text("⚠ TRAINING MODE ONLY", 12, Color.rgb(255, 197, 98), true));
        notice.addView(text(
            "Skenario berikut bersifat edukasi dan tidak menjalankan malware atau serangan sungguhan.",
            11, MUTED, false));
        content.addView(notice);

        section("SIMULATION MISSIONS", "3 SCENARIOS");
        module("01", "Suspicious APK",
            "Sebuah APK berasal dari tautan tidak dikenal dan meminta banyak izin. Apa langkah aman?",
            "DECISION TRAINING", () -> scenario(
                "APK Mencurigakan",
                new String[]{"Instal dan berikan semua izin",
                    "Jangan instal; verifikasi sumber dan periksa melalui alat tepercaya",
                    "Kirim file ke teman agar dicoba"},
                1,
                "Hindari instalasi dari sumber tak dikenal. Verifikasi sumber dan gunakan pemeriksaan tepercaya."));
        module("02", "Phishing Message",
            "Pesan mengaku dari layanan resmi dan meminta kode OTP.",
            "SOCIAL ENGINEERING", () -> scenario(
                "Pesan Phishing",
                new String[]{"Bagikan OTP",
                    "Abaikan permintaan dan verifikasi lewat kanal resmi",
                    "Balas dengan kata sandi"},
                1,
                "OTP tidak boleh dibagikan. Hubungi layanan lewat aplikasi atau situs resmi."));
        module("03", "Incident Response",
            "Perangkat menampilkan aktivitas aneh setelah memasang aplikasi baru.",
            "SAFE RESPONSE", () -> scenario(
                "Respons Insiden",
                new String[]{"Abaikan semua tanda",
                    "Hentikan aktivitas sensitif, minta bantuan tepercaya, dan periksa aplikasi",
                    "Pasang aplikasi pembersih dari tautan acak"},
                1,
                "Hindari transaksi sensitif, periksa aplikasi dari pengaturan resmi, dan cari bantuan tepercaya."));
    }

    void scenario(String title, String[] choices, int correct, String explanation) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setSingleChoiceItems(choices, -1, (dialog, which) -> {
                dialog.dismiss();
                if (which == correct) {
                    info("MISSION COMPLETE", "Jawaban tepat.\n\n" + explanation);
                } else {
                    info("MISSION REVIEW", "Belum tepat. Pelajari kembali prinsip keamanan.\n\n" + explanation);
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    void progressPage() {
        content.addView(text("PLAYER DATA", 25, WHITE, true));
        content.addView(text("YOUR CYBER LEARNING PROFILE", 10, CYAN, true));
        gap(content, 13);

        LinearLayout card = col();
        pad(card, 17, 18, 17, 18);
        card.setBackground(grad(Color.rgb(10, 48, 76),
                               Color.rgb(15, 23, 47), 18));
        card.addView(text("TOTAL MISSIONS COMPLETED", 10, CYAN, true));
        card.addView(text(completed + " / " + lessonTitles.length,
                          34, WHITE, true));
        int percent = Math.min(100, completed * 100 / lessonTitles.length);
        card.addView(text("Progress: " + percent + "%", 12, GREEN, true));

        ProgressBar bar = new ProgressBar(this, null,
            android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setProgress(percent);
        if (Build.VERSION.SDK_INT >= 21) {
            bar.setProgressTintList(android.content.res.ColorStateList.valueOf(CYAN));
        }
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(8));
        bp.setMargins(0, dp(9), 0, dp(9));
        card.addView(bar, bp);
        card.addView(text(
            "Progres disimpan secara lokal di aplikasi ini.",
            11, MUTED, false));
        content.addView(card);

        section("MISSION CHECKLIST", null);
        for (int i = 0; i < lessonTitles.length; i++) {
            final int index = i;
            boolean done = prefs.getBoolean("lesson_" + i, false);
            module(done ? "✓" : "○", lessonTitles[i],
                done ? "Materi telah ditandai selesai." : "Materi belum diselesaikan.",
                done ? "COMPLETED" : "PENDING",
                () -> lessonDetail(index));
        }

        gap(content, 8);
        button(content, "RESET LEARNING PROGRESS", () ->
            new AlertDialog.Builder(this)
                .setTitle("Reset progres?")
                .setMessage("Semua tanda materi selesai akan dihapus.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Reset", (d, w) -> {
                    android.content.SharedPreferences.Editor e = prefs.edit();
                    for (int i = 0; i < lessonTitles.length; i++)
                        e.remove("lesson_" + i);
                    e.putInt("completed", 0).apply();
                    completed = 0;
                    show("progress");
                }).show());
    }

    void info(String title, String message) {
        TextView body = text(message, 13, WHITE, false);
        body.setGravity(Gravity.TOP);
        body.setLineSpacing(dp(3), 1f);
        pad(body, 18, 10, 18, 10);
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setView(body)
            .setPositiveButton("OK", null)
            .show();
    }
}
