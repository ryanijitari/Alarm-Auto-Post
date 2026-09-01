# Jitari Auto Post Alarm Android — GitHub Build Ready

APK pendamping Jitari Auto Post V2.2. Alarm dibuat lokal menggunakan Android `AlarmManager`; tidak memakai cron atau scheduler hosting.

## Build APK lewat GitHub (tanpa Android Studio)

1. Buat repository GitHub baru, misalnya `jitari-autopost-alarm`.
2. Upload **isi folder project ini** ke root repository. Pastikan folder `.github/workflows/` ikut ter-upload.
3. Buka tab **Actions** pada repository.
4. Pilih workflow **Build Android APK**.
5. Tekan **Run workflow** -> **Run workflow**.
6. Setelah build selesai (centang hijau), buka hasil workflow tersebut.
7. Pada bagian **Artifacts**, download **Jitari-Auto-Post-Alarm-APK**.
8. Ekstrak ZIP artifact. Di dalamnya ada `Jitari-Auto-Post-Alarm-debug.apk` yang bisa di-install di Android.

Workflow juga otomatis build ulang saat source Android di-push ke branch `main` atau `master`.

## Alur aplikasi

1. Install APK dan buka.
2. Masukkan URL HTTPS Jitari Auto Post sekali saja.
3. Buat posting -> pilih **Simpan sebagai Draft** -> isi waktu pengingat.
4. Saat halaman Draft dibuka dari APK, web dapat memanggil bridge `JitariAlarm.schedule()` untuk menyimpan alarm lokal.
5. Pada waktunya Android menampilkan notifikasi/alarm prioritas tinggi.
6. Ketuk notifikasi -> APK langsung membuka URL draft spesifik (`drafts.php?open=<id>`).
7. Tekan **Posting Sekarang** secara manual.

## Izin Android

- Android 13+ meminta izin notifikasi.
- Android 12+ dapat meminta izin **Exact alarms** agar alarm lebih tepat waktu.
- Beberapa merek HP memiliki optimasi baterai tambahan. Jika alarm terlambat, izinkan Jitari Auto Post berjalan normal/tidak dibatasi baterai.

## Catatan APK debug

Versi workflow ini menghasilkan **debug APK**, cocok untuk tes pribadi dan pemasangan langsung. Untuk distribusi jangka panjang/update tanpa perubahan signature, tahap berikutnya sebaiknya memakai **signed release APK** dengan keystore yang disimpan sebagai GitHub Secrets.


## v1.0.3
- Kotlin compile fix pada fungsi schedule AlarmBridge.
- Tetap memakai Java/Kotlin JVM 17.
