package id.kiyyofficial.cybersafe;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class MainActivity extends Activity {
    final int BG = Color.rgb(5, 11, 27);
    final int PANEL = Color.rgb(15, 27, 49);
    final int PANEL2 = Color.rgb(20, 37, 64);
    final int CYAN = Color.rgb(48, 202, 255);
    final int BLUE = Color.rgb(65, 115, 255);
    final int MUTED = Color.rgb(164, 183, 211);
    final int WHITE = Color.rgb(242, 248, 255);
    final int GREEN = Color.rgb(66, 224, 166);
    final int ORANGE = Color.rgb(255, 185, 83);

    LinearLayout root, content, nav;
    android.content.SharedPreferences prefs;
    int completed;
    String currentPage = "home";
    static final int PICK_APK = 101;

    int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("cybersafe", MODE_PRIVATE);
        completed = prefs.getInt("completed", 0);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(BG);
            getWindow().setNavigationBarColor(BG);
        }
        showPage("home");
    }

    GradientDrawable shape(int color, int radius, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (strokeColor != 0) d.setStroke(dp(1), strokeColor);
        return d;
    }

    GradientDrawable gradient(int c1, int c2, int radius) {
        GradientDrawable d = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR, new int[]{c1, c2});
        d.setCornerRadius(dp(radius));
        return d;
    }

    TextView label(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setIncludeFontPadding(true);
        return t;
    }

    TextView bold(String value, int size, int color) {
        TextView t = label(value, size, color);
        t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return t;
    }

    LinearLayout column() {
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

    void gap(LinearLayout parent, int height) {
        View v = new View(this);
        parent.addView(v, new LinearLayout.LayoutParams(1, dp(height)));
    }

    void pad(View v, int l, int t, int r, int b) {
        v.setPadding(dp(l), dp(t), dp(r), dp(b));
    }

    void showPage(String page) {
        currentPage = page;
        root = column();
        root.setBackgroundColor(BG);
        pad(root, 17, 8, 17, 5);
        setContentView(root);

        LinearLayout top = row();
        TextView logo = bold("◈", 28, CYAN);
        top.addView(logo, new LinearLayout.LayoutParams(dp(38), dp(44)));

        LinearLayout brand = column();
        brand.addView(bold("CYBERSAFE ID", 16, WHITE));
        brand.addView(label("DIGITAL DEFENSE • SECURITY LAB", 9, MUTED));
        top.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        TextView shield = label("⬡", 24, CYAN);
        shield.setGravity(Gravity.CENTER);
        shield.setBackground(shape(PANEL2, 14, Color.rgb(38, 83, 126)));
        top.addView(shield, new LinearLayout.LayoutParams(dp(43), dp(43)));
        root.addView(top);

        View line = new View(this);
        line.setBackgroundColor(Color.rgb(30, 49, 77));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(1));
        lp.setMargins(0, dp(9), 0, dp(8));
        root.addView(line, lp);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        content = column();
        pad(content, 0, 4, 0, 18);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        if ("scan".equals(page)) scannerPage();
        else if ("learn".equals(page)) academyPage();
        else if ("lab".equals(page)) labPage();
        else if ("progress".equals(page)) progressPage();
        else homePage();

        buildNav();
    }

    void buildNav() {
        nav = row();
        nav.setGravity(Gravity.CENTER);
        nav.setBackground(shape(PANEL, 17, Color.rgb(28, 48, 78)));
        pad(nav, 3, 4, 3, 4);
        String[] names = {"⌂\nHome", "⌕\nScanner", "⌘\nAcademy", "◎\nCyber Lab"};
        String[] pages = {"home", "scan", "learn", "lab"};
        for (int i = 0; i < names.length; i++) {
            final String target = pages[i];
            LinearLayout item = column();
            item.setGravity(Gravity.CENTER);
            item.setBackground(shape(
                currentPage.equals(target) ? Color.rgb(23, 59, 91) : Color.TRANSPARENT,
                12, currentPage.equals(target) ? Color.rgb(35, 110, 154) : 0));
            TextView t = label(names[i], 11,
                currentPage.equals(target) ? CYAN : MUTED);
            t.setGravity(Gravity.CENTER);
            t.setLineSpacing(0, 1f);
            item.addView(t, new LinearLayout.LayoutParams(-1, dp(49)));
            nav.addView(item, new LinearLayout.LayoutParams(0, dp(49), 1));
            item.setOnClickListener(v -> showPage(target));
        }
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(6), 0, dp(2));
        root.addView(nav, p);
    }

    void section(String title, String right) {
        LinearLayout r = row();
        r.addView(bold(title, 16, WHITE),
            new LinearLayout.LayoutParams(0, -2, 1));
        if (right != null) r.addView(label(right, 11, CYAN));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(17), 0, dp(7));
        content.addView(r, p);
    }

    void card(LinearLayout parent, String icon, String title,
              String desc, String badge, Runnable action) {
        LinearLayout c = column();
        pad(c, 14, 13, 14, 13);
        c.setBackground(shape(PANEL, 17, Color.rgb(27, 48, 77)));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(5), 0, dp(6));
        parent.addView(c, p);

        LinearLayout head = row();
        TextView ic = label(icon, 23, CYAN);
        ic.setGravity(Gravity.CENTER);
        ic.setBackground(shape(Color.rgb(22, 48, 76), 12, 0));
        head.addView(ic, new LinearLayout.LayoutParams(dp(45), dp(45)));

        LinearLayout texts = column();
        pad(texts, 11, 0, 0, 0);
        texts.addView(bold(title, 14, WHITE));
        if (badge != null) texts.addView(label(badge, 10, GREEN));
        head.addView(texts, new LinearLayout.LayoutParams(0, -2, 1));
        TextView arrow = label("›", 27, CYAN);
        head.addView(arrow);
        c.addView(head);
        TextView detail = label(desc, 12, MUTED);
        detail.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams d = new LinearLayout.LayoutParams(-1, -2);
        d.setMargins(0, dp(10), 0, 0);
        c.addView(detail, d);
        c.setOnClickListener(v -> action.run());
        c.setClickable(true);
    }

    void actionButton(LinearLayout parent, String title, Runnable action) {
        TextView b = bold(title, 13, WHITE);
        b.setGravity(Gravity.CENTER);
        pad(b, 12, 12, 12, 12);
        b.setBackground(gradient(Color.rgb(18, 127, 193),
                                Color.rgb(39, 83, 190), 13));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(5), 0, dp(5));
        parent.addView(b, p);
        b.setOnClickListener(v -> action.run());
    }

    void miniStat(LinearLayout parent, String value, String title) {
        LinearLayout box = column();
        pad(box, 10, 12, 8, 12);
        box.setBackground(shape(PANEL2, 14, Color.rgb(31, 62, 98)));
        box.addView(bold(value, 21, CYAN));
        box.addView(label(title, 10, MUTED));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1);
        p.setMargins(dp(3), 0, dp(3), 0);
        parent.addView(box, p);
    }

    void homePage() {
        gap(content, 5);
        TextView eyebrow = bold("CYBERSECURITY • LEARN • PROTECT", 10, CYAN);
        content.addView(eyebrow);
        gap(content, 5);
        content.addView(bold("Pertahanan Digital\nDimulai Dari Kamu", 27, WHITE));
        TextView sub = label(
            "Belajar keamanan siber, analisis APK, dan coding dalam satu tempat.",
            13, MUTED);
        sub.setLineSpacing(dp(3), 1f);
        content.addView(sub);

        gap(content, 14);
        LinearLayout hero = column();
        pad(hero, 17, 17, 17, 17);
        hero.setBackground(gradient(Color.rgb(14, 53, 87),
                                    Color.rgb(17, 28, 57), 21));
        hero.setBackground(shape(Color.rgb(13, 39, 68), 21,
                                 Color.rgb(38, 105, 148)));
        LinearLayout h = row();
        LinearLayout left = column();
        left.addView(label("SECURITY CENTER", 10, CYAN));
        gap(left, 5);
        left.addView(bold("CyberSafe Dashboard", 17, WHITE));
        left.addView(label("Mode edukasi aktif", 11, GREEN));
        h.addView(left, new LinearLayout.LayoutParams(0, -2, 1));
        TextView emblem = label("⬡", 55, CYAN);
        emblem.setGravity(Gravity.CENTER);
        h.addView(emblem, new LinearLayout.LayoutParams(dp(76), dp(82)));
        hero.addView(h);
        gap(hero, 10);
        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(39, 76, 112));
        hero.addView(divider, new LinearLayout.LayoutParams(-1, dp(1)));
        gap(hero, 7);
        hero.addView(label(
            "Pemantauan keamanan otomatis seluruh perangkat belum diaktifkan. " +
            "Gunakan modul pemindai untuk memeriksa APK pilihanmu.",
            11, MUTED));
        content.addView(hero);

        section("Ringkasan Belajar", "Lihat progres ›");
        LinearLayout stats = row();
        miniStat(stats, String.valueOf(completed), "Materi selesai");
        miniStat(stats, "4", "Modul utama");
        miniStat(stats, "Lokal", "Penyimpanan");
        content.addView(stats);
        stats.setOnClickListener(v -> showPage("progress"));

        section("Security Modules", "4 modul");
        card(content, "⌕", "APK RISK SCANNER",
            "Analisis file APK, hash SHA-256, nama paket, versi, dan izin.",
            "PEMERIKSAAN LOKAL", () -> showPage("scan"));
        card(content, "⌘", "CODING ACADEMY",
            "Materi pemrograman, keamanan aplikasi, jaringan, dan kuis.",
            "BELAJAR SESUAI PROGRES", () -> showPage("learn"));
        card(content, "◎", "CYBER LAB",
            "Latihan phishing, aplikasi mencurigakan, dan respons insiden.",
            "SIMULASI AMAN", () -> showPage("lab"));
        card(content, "▤", "PROGRES BELAJAR",
            "Lihat jumlah pelajaran yang sudah ditandai selesai.",
            "TERSIMPAN DI PERANGKAT", () -> showPage("progress"));

        gap(content, 8);
        content.addView(label(
            "CyberSafe ID • Alat edukasi. Hasil pemindaian bukan jaminan bebas malware.",
            10, MUTED));
    }

    void scannerPage() {
        content.addView(bold("APK Risk Scanner", 25, WHITE));
        content.addView(label(
            "Periksa informasi dasar APK langsung di perangkatmu.",
            13, MUTED));
        gap(content, 13);

        LinearLayout hero = column();
        hero.setGravity(Gravity.CENTER);
        pad(hero, 16, 19, 16, 19);
        hero.setBackground(shape(PANEL, 20, Color.rgb(34, 74, 112)));
        TextView icon = label("⌕", 48, CYAN);
        icon.setGravity(Gravity.CENTER);
        hero.addView(icon);
        hero.addView(bold("Analisis File APK", 18, WHITE));
        hero.addView(label("Pilih APK untuk memulai pemeriksaan", 12, MUTED));
        gap(hero, 9);
        actionButton(hero, "PILIH FILE APK", this::pickApk);
        content.addView(hero);

        section("Yang Diperiksa", null);
        card(content, "01", "SHA-256",
            "Menghitung sidik jari kriptografis file yang dipilih.",
            "HASH FILE", () -> info("SHA-256",
                "Hash membantu membandingkan file, tetapi tidak dengan sendirinya menentukan apakah file berbahaya."));
        card(content, "02", "Informasi Paket",
            "Mencoba membaca nama paket dan versi aplikasi.",
            "METADATA", () -> info("Informasi paket",
                "Nama paket dan versi dapat membantu mengidentifikasi aplikasi."));
        card(content, "03", "Izin Aplikasi",
            "Menampilkan izin yang diminta oleh APK yang bisa dibaca.",
            "ANALISIS HEURISTIK", () -> info("Analisis izin",
                "Izin tertentu dapat berisiko jika tidak sesuai fungsi aplikasi. Izin saja tidak membuktikan malware."));
        content.addView(label(
            "Privasi: APK diproses secara lokal dan tidak sengaja diunggah ke server. " +
            "Hasil ini bukan pengganti antivirus atau analisis profesional.",
            11, MUTED));
    }

    void pickApk() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("*/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(i, PICK_APK);
        } catch (Exception e) {
            info("Pemilih file", "Tidak bisa membuka pemilih file: " + e.getMessage());
        }
    }

    void academyPage() {
        content.addView(bold("Coding Academy", 25, WHITE));
        content.addView(label(
            "Bangun kemampuan dari dasar menuju pengembangan aplikasi.",
            13, MUTED));
        gap(content, 12);

        LinearLayout progressCard = column();
        pad(progressCard, 15, 15, 15, 15);
        progressCard.setBackground(shape(PANEL, 17, Color.rgb(35, 67, 102)));
        progressCard.addView(label("PROGRES MATERI", 10, CYAN));
        progressCard.addView(bold(completed + " pelajaran selesai", 20, WHITE));
        progressCard.addView(label(
            "Buka materi, pelajari konsepnya, lalu tandai selesai.",
            11, MUTED));
        content.addView(progressCard);

        section("Learning Path", "7 pelajaran");
        lesson("01", "Dasar Keamanan Digital",
            "Pelajari kata sandi unik, autentikasi dua faktor, pembaruan perangkat, " +
            "phishing, dan cara menjaga data pribadi.");
        lesson("02", "Python untuk Pemula",
            "Variabel menyimpan data. Contoh: nama = 'Kiyy' lalu print(nama). " +
            "Berikutnya pelajari tipe data, kondisi, perulangan, fungsi, dan exception.");
        lesson("03", "Java Programming",
            "Contoh output Java: System.out.println(\"Halo dunia\");. " +
            "Pelajari class, method, objek, koleksi, dan penanganan error.");
        lesson("04", "Android Development",
            "Kenali Activity, Intent, lifecycle, permission, penyimpanan lokal, " +
            "pengujian, dan cara membangun APK.");
        lesson("05", "Jaringan Komputer",
            "Pelajari IP, DNS, TCP/UDP, HTTP/HTTPS, router, firewall, dan diagnostik jaringan.");
        lesson("06", "Secure Coding",
            "Pelajari validasi input, least privilege, pengelolaan rahasia, " +
            "pembaruan dependensi, dan audit izin.");
        lesson("07", "Incident Response",
            "Dokumentasikan gejala dan waktu, lindungi akun dari perangkat tepercaya, " +
            "serta ikuti prosedur pemulihan yang aman.");
        section("Knowledge Check", null);
        card(content, "?", "Kuis Keamanan Digital",
            "Uji pemahamanmu tentang cara mengenali pesan phishing.",
            "KUIS INTERAKTIF", this::quiz);
    }

    void lesson(String number, String title, String body) {
        card(content, number, title, body,
            prefs.getBoolean("lesson_" + number, false) ? "✓ SELESAI" : "MATERI",
            () -> openLesson(number, title, body));
    }

    void openLesson(String number, String title, String body) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(body + "\n\nIngat: gunakan materi ini hanya pada perangkat dan sistem yang kamu punya izin untuk mengujinya.")
            .setNegativeButton("Kembali", null)
            .setPositiveButton(
                prefs.getBoolean("lesson_" + number, false) ? "Sudah selesai" : "Tandai selesai",
                (d, w) -> {
                    if (!prefs.getBoolean("lesson_" + number, false)) {
                        prefs.edit().putBoolean("lesson_" + number, true)
                            .putInt("completed", ++completed).apply();
                    }
                    showPage("learn");
                })
            .show();
    }

    void quiz() {
        String[] options = {
            "Langsung memasukkan kata sandi melalui tautan",
            "Memeriksa domain dan membuka situs resmi secara langsung",
            "Membagikan kode OTP kepada pengirim"
        };
        new AlertDialog.Builder(this)
            .setTitle("Kuis • Mengenali Phishing")
            .setSingleChoiceItems(options, -1, (dialog, which) -> {
                dialog.dismiss();
                if (which == 1) {
                    info("Jawaban benar!",
                        "Periksa domain, jangan bagikan OTP, dan buka situs resmi secara langsung.");
                } else {
                    info("Coba lagi",
                        "Jangan masukkan kata sandi lewat tautan mencurigakan dan jangan bagikan kode OTP.");
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    void labPage() {
        content.addView(bold("Cyber Lab", 25, WHITE));
        content.addView(label(
            "Latihan interaktif untuk mengenali ancaman dan memilih respons yang aman.",
            13, MUTED));
        gap(content, 12);

        LinearLayout banner = column();
        pad(banner, 15, 16, 15, 16);
        banner.setBackground(gradient(Color.rgb(19, 53, 79),
                                      Color.rgb(21, 29, 54), 18));
        banner.addView(bold("SAFE SIMULATION MODE", 13, CYAN));
        banner.addView(label(
            "Seluruh skenario di sini berupa edukasi. Tidak ada malware sungguhan yang dijalankan.",
            12, MUTED));
        content.addView(banner);

        section("Training Scenarios", null);
        scenarioCard("01", "Pesan Phishing",
            "Pesan mendesak meminta kata sandi atau kode OTP.",
            new String[]{"Klik tautan segera", "Periksa lewat kanal resmi", "Bagikan kode OTP"},
            1,
            "Periksa alamat pengirim dan domain, jangan membagikan OTP, dan buka situs resmi secara manual.");
        scenarioCard("02", "Aplikasi Mencurigakan",
            "Aplikasi meminta izin yang tidak sesuai fungsinya.",
            new String[]{"Setujui semua izin", "Tinjau izin dan sumber aplikasi", "Abaikan peringatan"},
            1,
            "Tinjau izin, pengembang, dan sumber unduhan. Jangan instal jika sumbernya tidak tepercaya.");
        scenarioCard("03", "Aktivitas Tidak Biasa",
            "Perangkat menunjukkan aktivitas yang tidak kamu kenali.",
            new String[]{"Abaikan semuanya", "Catat gejala dan lindungi akun", "Pasang aplikasi acak"},
            1,
            "Catat gejala, hindari file mencurigakan, gunakan perangkat tepercaya untuk mengamankan akun, dan minta bantuan jika perlu.");
        scenarioCard("04", "Permintaan Data Pribadi",
            "Orang tak dikenal meminta PIN atau kode verifikasi.",
            new String[]{"Kirim kode", "Verifikasi identitas lewat kanal resmi", "Kirim foto identitas"},
            1,
            "Jangan bagikan PIN, OTP, atau data sensitif kepada pihak yang belum terverifikasi.");
    }

    void scenarioCard(String no, String title, String desc,
                      String[] options, int correct, String explanation) {
        card(content, no, title, desc, "LATIHAN",
            () -> new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(desc + "\n\nPilih tindakan yang paling aman:")
                .setSingleChoiceItems(options, -1, (dialog, which) -> {
                    dialog.dismiss();
                    String answer = which == correct
                        ? "Pilihan tepat.\n\n"
                        : "Pilihan itu belum aman.\n\n";
                    info("Evaluasi simulasi", answer + explanation);
                })
                .setNegativeButton("Batal", null)
                .show());
    }

    void progressPage() {
        content.addView(bold("Progres Belajar", 25, WHITE));
        content.addView(label("Ringkasan aktivitas belajar di perangkat ini.", 13, MUTED));
        gap(content, 12);
        LinearLayout box = column();
        pad(box, 17, 17, 17, 17);
        box.setBackground(shape(PANEL, 18, Color.rgb(35, 68, 105)));
        box.addView(label("TOTAL MATERI SELESAI", 11, CYAN));
        box.addView(bold(String.valueOf(completed), 36, WHITE));
        box.addView(label("Progres tersimpan secara lokal pada aplikasi.", 12, MUTED));
        content.addView(box);
        section("Modul Aplikasi", null);
        card(content, "⌕", "APK Risk Scanner",
            "Pemeriksaan lokal berdasarkan metadata, hash, dan izin.",
            "TERSEDIA", () -> showPage("scan"));
        card(content, "⌘", "Coding Academy",
            "Buka materi dan tandai pelajaran selesai.",
            "TERSEDIA", () -> showPage("learn"));
        card(content, "◎", "Cyber Lab",
            "Latihan skenario keamanan yang aman.",
            "TERSEDIA", () -> showPage("lab"));
        actionButton(content, "RESET PROGRES BELAJAR", () ->
            new AlertDialog.Builder(this)
                .setTitle("Reset progres?")
                .setMessage("Hitungan progres dan tanda selesai semua materi akan dihapus.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Reset", (d, w) -> {
                    prefs.edit().clear().apply();
                    completed = 0;
                    showPage("progress");
                }).show());
    }

    void info(String title, String message) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Mengerti", null)
            .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_APK || resultCode != RESULT_OK ||
            data == null || data.getData() == null) return;

        Uri uri = data.getData();
        File temp = null;
        try {
            String name = displayName(uri);
            temp = new File(getCacheDir(), "cybersafe-scan.apk");
            try (InputStream in = getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(temp)) {
                if (in == null) throw new IOException("File tidak dapat dibaca");
                byte[] buffer = new byte[8192];
                int n;
                while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
            }

            String hash = sha256(temp);
            PackageInfo pi = getPackageManager().getPackageArchiveInfo(
                temp.getAbsolutePath(), PackageManager.GET_PERMISSIONS);
            StringBuilder report = new StringBuilder();
            report.append("Nama file: ").append(name)
                .append("\nUkuran: ").append(temp.length()).append(" byte")
                .append("\n\nSHA-256:\n").append(hash).append("\n\n");

            int score = 0;
            if (pi == null) {
                report.append("Metadata paket tidak bisa dibaca. File mungkin rusak, bukan APK, atau menggunakan format yang tidak didukung.\n");
                score += 2;
            } else {
                report.append("Nama paket: ").append(pi.packageName)
                    .append("\nVersi: ").append(pi.versionName)
                    .append("\n\nIzin yang diminta:\n");
                String[] permissions = pi.requestedPermissions;
                if (permissions == null || permissions.length == 0) {
                    report.append("Tidak ada izin yang terdeteksi.\n");
                } else {
                    for (String permission : permissions) {
                        report.append("• ").append(permission).append("\n");
                        String p = permission.toLowerCase(Locale.ROOT);
                        if (p.contains("request_install_packages") ||
                            p.contains("read_sms") || p.contains("send_sms") ||
                            p.contains("record_audio") ||
                            p.contains("read_call_log") ||
                            p.contains("accessibility")) score++;
                    }
                }
            }

            report.append("\nIndikator heuristik: ");
            if (score >= 3) report.append("PERLU DITINJAU LEBIH LANJUT");
            else if (score > 0) report.append("PERHATIAN");
            else report.append("TIDAK ADA INDIKATOR DARI ATURAN DASAR");

            report.append("\n\nPenting: hasil ini bukan antivirus. Izin yang sensitif bisa diperlukan oleh aplikasi yang sah. Tidak ditemukan indikator bukan berarti APK pasti aman.");

            new AlertDialog.Builder(this)
                .setTitle("Laporan APK")
                .setMessage(report.toString())
                .setPositiveButton("Selesai", null)
                .show();
        } catch (Exception e) {
            info("Pemeriksaan gagal",
                "Tidak dapat memeriksa file: " + e.getMessage());
        } finally {
            if (temp != null && temp.exists()) temp.delete();
        }
    }

    String displayName(Uri uri) {
        String name = "APK dipilih";
        try (Cursor c = getContentResolver().query(uri, null, null, null, null)) {
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
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) != -1) md.update(buffer, 0, n);
        }
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) result.append(String.format("%02x", b));
        return result.toString();
    }
}
