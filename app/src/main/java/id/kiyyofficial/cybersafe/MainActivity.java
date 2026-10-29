package id.kiyyofficial.cybersafe;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class MainActivity extends Activity {
    int bg = Color.rgb(7, 13, 29);
    int panel = Color.rgb(18, 30, 53);
    int blue = Color.rgb(45, 190, 255);
    int white = Color.WHITE;
    LinearLayout root, content, nav;
    android.content.SharedPreferences prefs;
    int completed;
    static final int PICK_APK = 101;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        prefs = getSharedPreferences("cybersafe", MODE_PRIVATE);
        completed = prefs.getInt("completed", 0);
        showPage("home");
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(0, 7, 0, 7);
        return t;
    }

    void heading(LinearLayout box, String value) {
        TextView t = text(value, 21, blue);
        t.setTypeface(null, Typeface.BOLD);
        box.addView(t);
    }

    void paragraph(LinearLayout box, String value) {
        TextView t = text(value, 14, Color.rgb(205, 216, 235));
        box.addView(t);
    }

    void card(LinearLayout box, String title, String desc, Runnable action) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(17, 12, 17, 12);
        c.setBackgroundColor(panel);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                -1, -2);
        cp.setMargins(0, 6, 0, 7);
        box.addView(c, cp);

        TextView a = text(title, 17, white);
        a.setTypeface(null, Typeface.BOLD);
        c.addView(a);
        c.addView(text(desc, 13, Color.LTGRAY));
        c.setClickable(true);
        c.setOnClickListener(v -> action.run());
    }

    void button(LinearLayout box, String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(white);
        b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(24, 106, 168)));
        box.addView(b, new LinearLayout.LayoutParams(-1, -2));
        b.setOnClickListener(v -> action.run());
    }

    void showPage(String page) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 12, 18, 8);
        root.setBackgroundColor(bg);
        setContentView(root);

        TextView brand = text("CYBERSAFE ID  /  SECURITY LAB", 16, blue);
        brand.setTypeface(null, Typeface.BOLD);
        root.addView(brand);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, 10, 0, 14);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1f));

        switch (page) {
            case "scan": scannerPage(); break;
            case "learn": academyPage(); break;
            case "lab": labPage(); break;
            default: homePage();
        }

        nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(panel);
        root.addView(nav);
        navButton("Beranda", "home");
        navButton("Pemindai", "scan");
        navButton("Akademi", "learn");
        navButton("Simulasi", "lab");
    }

    void navButton(String label, String page) {
        TextView b = text(label, 12, white);
        b.setGravity(Gravity.CENTER);
        b.setPadding(3, 13, 3, 13);
        nav.addView(b, new LinearLayout.LayoutParams(0, -2, 1));
        b.setOnClickListener(v -> showPage(page));
    }

    void homePage() {
        heading(content, "Pusat Keamanan Digital");
        paragraph(content,
                "Belajar. Periksa. Lindungi. Tingkatkan kemampuan keamanan digitalmu.");
        card(content, "STATUS PERANGKAT",
                "CyberSafe ID menyediakan pemeriksaan dasar. " +
                "Belum ada klaim bahwa perangkat bebas malware.",
                () -> new AlertDialog.Builder(this)
                    .setTitle("Status pemeriksaan")
                    .setMessage("Aplikasi ini belum melakukan pemindaian seluruh perangkat. " +
                        "Gunakan pemindai APK untuk memeriksa file yang kamu pilih.")
                    .setPositiveButton("Mengerti", null).show());

        card(content, "PEMINDAI APK",
                "Pilih file APK, hitung SHA-256, dan periksa izin aplikasi.",
                () -> showPage("scan"));
        card(content, "AKADEMI CODING",
                "Materi Java, Python, Android, jaringan, dan keamanan siber.",
                () -> showPage("learn"));
        card(content, "SIMULASI INSIDEN",
                "Latihan edukasi phishing dan respons insiden yang aman.",
                () -> showPage("lab"));
        card(content, "PROGRES BELAJAR",
                "Pelajaran selesai: " + completed,
                () -> showPage("learn"));
        paragraph(content,
                "Catatan: hasil pemeriksaan APK adalah indikator awal, bukan sertifikasi keamanan.");
    }

    void scannerPage() {
        heading(content, "APK Risk Scanner");
        paragraph(content,
                "Pilih APK yang ingin diperiksa. File tidak diunggah ke server; " +
                "pemeriksaan dilakukan secara lokal.");
        button(content, "PILIH FILE APK", () -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("application/vnd.android.package-archive");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(i, PICK_APK);
            } catch (Exception e) {
                result("Pemilih file tidak dapat dibuka: " + e.getMessage());
            }
        });
        paragraph(content,
                "Pemeriksaan meliputi nama file, SHA-256, nama paket, " +
                "versi yang tersedia, dan izin yang diminta.");
    }

    void result(String message) {
        new AlertDialog.Builder(this)
            .setTitle("Hasil pemeriksaan")
            .setMessage(message)
            .setPositiveButton("Tutup", null)
            .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_APK || resultCode != RESULT_OK ||
                data == null || data.getData() == null) return;

        Uri uri = data.getData();
        try {
            String name = displayName(uri);
            File file = new File(getCacheDir(),
                    "scan-" + System.currentTimeMillis() + ".apk");
            try (InputStream in = getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(file)) {
                if (in == null) throw new IOException("File tidak dapat dibaca");
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            }

            String hash = sha256(file);
            PackageInfo pi = getPackageManager().getPackageArchiveInfo(
                    file.getAbsolutePath(),
                    PackageManager.GET_PERMISSIONS);

            StringBuilder report = new StringBuilder();
            report.append("File: ").append(name)
                  .append("\nUkuran: ").append(file.length()).append(" byte")
                  .append("\nSHA-256:\n").append(hash).append("\n");

            int score = 0;
            if (pi == null) {
                report.append("\nPaket tidak dapat dibaca sebagai APK yang valid. " +
                        "File mungkin rusak atau formatnya tidak didukung.");
                score += 2;
            } else {
                report.append("\nPaket: ").append(pi.packageName)
                      .append("\nVersi: ").append(pi.versionName)
                      .append("\n\nIzin yang diminta:\n");
                String[] permissions = pi.requestedPermissions;
                if (permissions == null || permissions.length == 0) {
                    report.append("Tidak ada izin yang terdeteksi.\n");
                } else {
                    for (String p : permissions) {
                        report.append("• ").append(p).append("\n");
                        String x = p.toLowerCase(Locale.ROOT);
                        if (x.contains("request_install_packages") ||
                            x.contains("read_sms") ||
                            x.contains("send_sms") ||
                            x.contains("record_audio") ||
                            x.contains("read_call_log") ||
                            x.contains("accessibility")) score++;
                    }
                }
            }

            report.append("\nIndikator pemeriksaan: ");
            if (score >= 3) {
                report.append("PERLU DIPERIKSA LEBIH LANJUT");
            } else if (score > 0) {
                report.append("PERHATIAN");
            } else {
                report.append("TIDAK ADA INDIKATOR DARI ATURAN DASAR INI");
            }
            report.append("\n\nSkor ini bukan hasil deteksi antivirus. " +
                    "Izin tertentu bisa wajar tergantung fungsi aplikasi. " +
                    "Tidak ada indikator bukan berarti file pasti aman.");

            file.delete();
            result(report.toString());
        } catch (Exception e) {
            result("Pemeriksaan gagal: " + e.getMessage());
        }
    }

    String displayName(Uri uri) {
        String name = "APK dipilih";
        try (Cursor c = getContentResolver().query(
                uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int ix = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (ix >= 0) name = c.getString(ix);
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
        StringBuilder s = new StringBuilder();
        for (byte b : md.digest()) s.append(String.format("%02x", b));
        return s.toString();
    }

    void academyPage() {
        heading(content, "Coding Academy");
        paragraph(content,
                "Baca materi, pahami contoh, lalu tandai pelajaran selesai. " +
                "Materi ini disediakan secara lokal.");
        lesson("01 • Dasar keamanan digital",
                "Gunakan kata sandi unik, aktifkan autentikasi dua faktor, " +
                "perbarui aplikasi, dan hindari tautan yang tidak dikenal.");
        lesson("02 • Python",
                "Variabel menyimpan nilai. Contoh: nama = 'Kiyy' dan " +
                "print(nama). Pelajari tipe data, kondisi, perulangan, fungsi, " +
                "serta penanganan kesalahan.");
        lesson("03 • Java",
                "Java memakai class dan method. Contoh: " +
                "System.out.println(\"Halo dunia\");. Pelajari variabel, " +
                "class, objek, koleksi, dan exception.");
        lesson("04 • Android",
                "Activity adalah komponen antarmuka. Pelajari lifecycle, " +
                "Intent, penyimpanan lokal, permission, dan pengujian aplikasi.");
        lesson("05 • Jaringan komputer",
                "Pelajari IP, DNS, TCP/UDP, HTTP/HTTPS, router, firewall, " +
                "dan cara membaca hasil diagnostik jaringan.");
        lesson("06 • Keamanan aplikasi",
                "Pahami prinsip least privilege, validasi input, penyimpanan " +
                "rahasia yang aman, pembaruan dependensi, dan audit izin.");
        lesson("07 • Analisis insiden",
                "Catat waktu, gejala, aplikasi terkait, dan tindakan yang diambil. " +
                "Isolasi perangkat yang dicurigai sesuai prosedur organisasi.");
        button(content, "KERJAKAN KUIS", this::quiz);
        button(content, "RESET PROGRES BELAJAR", () ->
            new AlertDialog.Builder(this)
                .setTitle("Reset progres?")
                .setMessage("Semua hitungan pelajaran selesai akan direset.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Reset", (d, w) -> {
                    completed = 0;
                    prefs.edit().putInt("completed", 0).apply();
                    showPage("learn");
                }).show());
    }

    void lesson(String title, String body) {
        card(content, title, body, () ->
            new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton("Tandai selesai", (d, w) -> {
                    completed++;
                    prefs.edit().putInt("completed", completed).apply();
                    Toast.makeText(this, "Progres tersimpan",
                            Toast.LENGTH_SHORT).show();
                    showPage("learn");
                })
                .setNegativeButton("Tutup", null).show());
    }

    void quiz() {
        final String[] options = {
            "Membuka semua tautan yang diterima",
            "Memeriksa alamat situs dan tidak memasukkan kata sandi lewat tautan mencurigakan",
            "Mematikan semua pembaruan keamanan"
        };
        new AlertDialog.Builder(this)
            .setTitle("Kuis: mengenali phishing")
            .setSingleChoiceItems(options, -1, (dialog, which) -> {
                dialog.dismiss();
                String msg = which == 1
                    ? "Benar! Periksa domain dan buka situs resmi secara langsung."
                    : "Belum tepat. Tautan mencurigakan dapat meniru situs asli.";
                result(msg);
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    void labPage() {
        heading(content, "Simulasi Insiden Aman");
        paragraph(content,
                "Pilih skenario edukasi. Modul ini tidak menginfeksi perangkat, " +
                "tidak mengirim pesan, dan tidak menjalankan malware.");
        card(content, "SIMULASI 1 • PHISHING",
                "Kamu menerima pesan mendesak yang meminta kata sandi.",
                () -> scenario("PHISHING",
                    "Jangan membuka tautan atau membagikan kode OTP. " +
                    "Periksa pengirim melalui kanal resmi, buka situs secara manual, " +
                    "dan laporkan pesan."));
        card(content, "SIMULASI 2 • APLIKASI MENCURIGAKAN",
                "Aplikasi baru meminta izin yang tidak sesuai fungsinya.",
                () -> scenario("APLIKASI MENCURIGAKAN",
                    "Jangan berikan izin yang tidak diperlukan. " +
                    "Periksa pengembang dan sumber unduhan. Jika ragu, " +
                    "jangan instal atau hapus aplikasi melalui pengaturan Android."));
        card(content, "SIMULASI 3 • RESPONS INSIDEN",
                "Perangkat menunjukkan aktivitas tidak biasa.",
                () -> scenario("RESPONS INSIDEN",
                    "Hentikan interaksi dengan file atau tautan mencurigakan. " +
                    "Putuskan koneksi bila ada indikasi kompromi aktif, " +
                    "gunakan perangkat tepercaya untuk mengganti kata sandi, " +
                    "perbarui sistem, dan minta bantuan teknisi jika diperlukan."));
        card(content, "SIMULASI 4 • DATA PRIBADI",
                "Seseorang meminta foto identitas dan kode verifikasi.",
                () -> scenario("PERLINDUNGAN DATA",
                    "Jangan membagikan OTP, PIN, atau data sensitif kepada pihak " +
                    "yang tidak terverifikasi. Pastikan tujuan dan identitas pihak tersebut."));
    }

    void scenario(String title, String advice) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(advice)
            .setPositiveButton("Selesai", null)
            .show();
    }
}
