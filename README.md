# AI Floating Assistant — Android starter 0.1.0

Proyek Android native Java untuk asisten AI melayang. APK debug dibuat melalui GitHub Actions.

## Yang sudah disiapkan

- Tombol lingkaran ✦ yang bisa digeser dan tampil di atas aplikasi lain.
- Panel chat bergaya navy, putih, dan teal.
- Pilihan provider Gemini atau DeepSeek.
- Input pertanyaan teks, salin jawaban dengan tekan lama, dan riwayat chat selama layanan aktif.
- Analisis screenshot layar melalui dialog persetujuan Android.
- API key dimasukkan pengguna dan disimpan terenkripsi menggunakan Android Keystore AES-GCM.
- API request berjalan di worker thread, bukan UI thread.
- GitHub Actions membangun APK debug dan menyimpannya sebagai artifact.

## Catatan penting

1. **Jangan gunakan kembali API key yang pernah ditempel di chat publik/percakapan.** Cabut/revoke key tersebut di dashboard penyedia, buat key baru, lalu masukkan sendiri melalui Pengaturan aplikasi.
2. API key tidak ditanam di kode maupun APK. Penyimpanan lokal terenkripsi membantu melindungi key saat tersimpan, tetapi tidak menjadikan key aman untuk dibagikan bersama APK; aplikasi yang berjalan tetap dapat dianalisis pada perangkat yang dimodifikasi.
3. API Gemini/DeepSeek dapat mempunyai batas gratis, kuota, atau biaya. APK ini tidak membuat akses API menjadi gratis atau tanpa batas.
4. Screenshot hanya dimulai setelah pengguna menekan tombol Analisis layar dan menyetujui dialog system Android. Gambar dikirim ke provider AI yang sedang dipilih saat pengguna menekan tombol kirim.
5. Izin overlay dan notifikasi layanan foreground harus diberikan. Beberapa ponsel perlu mengizinkan aplikasi berjalan di latar belakang/autostart agar bubble tidak dihentikan oleh penghemat baterai.
6. Versi ini adalah starter MVP. Belum ada uji instalasi pada perangkat fisik, belum ada tanda tangan rilis/Play Store, belum ada chat suara, OCR lokal, pembacaan otomatis teks aplikasi, maupun sinkronisasi riwayat lintas perangkat.

## Cara build lewat GitHub

1. Buat repository GitHub baru.
2. Upload seluruh isi folder proyek ini ke root repository (pastikan folder `.github/workflows` ikut terunggah).
3. Buka tab **Actions**. Jalankan workflow **Build AI Floating Assistant APK** dengan tombol **Run workflow**, atau tunggu build otomatis setelah push ke branch `main`/`master`.
4. Setelah job selesai, buka hasil workflow dan unduh artifact **AI-Floating-Assistant-APK**.
5. Ekstrak ZIP artifact untuk mendapatkan `app-debug.apk`, lalu pindahkan ke HP dan instal.

## Cara menyiapkan aplikasi

1. Buka aplikasi **AI Floating Assistant**.
2. Masukkan API key Gemini dan/atau DeepSeek baru pada bagian Koneksi AI, lalu tekan Simpan API key.
3. Tekan **Aktifkan Assistive AI**.
4. Izinkan **Tampil di atas aplikasi lain** pada pengaturan Android.
5. Ketuk lingkaran ✦ untuk membuka panel. Pilih provider, tulis pertanyaan, lalu tekan tombol panah.
6. Untuk screenshot, tekan **Analisis layar**, setujui dialog Android, tunggu gambar terlampir di panel, tulis pertanyaan, lalu kirim.

## API model

- Gemini: `gemini-3.8-flash` melalui Gemini Interactions REST API.
- DeepSeek: `deepseek-flash` melalui `https://api.deepseek.com/chat/completions`, termasuk format gambar `image_url`.

Nama model dan kuota dapat berubah. Jika penyedia menolak model atau key, lihat pesan error di panel dan periksa dokumentasi/model yang aktif di akun masing-masing.

## Build configuration

- `minSdk`: 23 (Android 6.0)
- `targetSdk`: 35
- `compileSdk`: 36
- Android Gradle Plugin: 8.13.2
- Gradle: 8.13
- JDK: 17
