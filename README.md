# SUARA: PLATFORM ASPIRASI DAN VOTING KEBIJAKAN

## Ringkasan Proyek

**SUARA** (Sistem untuk Aspirasi Rakyat) adalah aplikasi mobile native Android yang dirancang sebagai platform **e-democracy berskala nasional**. Tujuannya adalah menjadi jembatan langsung dan aman antara warga negara Indonesia dan pemerintah pusat untuk partisipasi dalam proses kebijakan publik.

Aplikasi ini fokus pada **akuntabilitas** dan **validitas data** melalui verifikasi identitas wajib saat pendaftaran.

## Stack Teknologi (MVP)

Proyek *full-stack* ini dibangun dengan fokus pada ekosistem **Kotlin** untuk efisiensi.

| Komponen | Teknologi Utama | Keterangan |
| :--- | :--- | :--- |
| **Frontend (Android)** | **Kotlin** & **Jetpack Compose** | Modern, Declarative UI |
| **Backend & API** | **Ktor** (Full-Stack Kotlin) | Ringan, Asynchronous Web Server |
| **Database/ORM** | **Exposed** & PostgreSQL/MYSQL | Kontrol penuh data untuk jangka panjang |
| **Arsitektur** | **MVVM** (Frontend) / **Layered** (Backend) | Menjamin kualitas kode yang terstruktur dan *testable* |

## Fitur Inti (Minimum Viable Product - MVP)

Fokus utama MVP adalah modul Autentikasi, Kebijakan, dan Partisipasi yang berfungsi penuh.

### 1. Modul Autentikasi Dasar
* Registrasi & Login pengguna dasar.
* Penggunaan **JWT** (JSON Web Tokens) untuk keamanan sesi *stateless*.

### 2. Modul Kebijakan (Informasi Publik)
* Menampilkan **Daftar Kebijakan** aktif (judul, deskripsi, status).
* Menyajikan **Detail Kebijakan** (latar belakang, tujuan, periode *voting*).

### 3. Modul Partisipasi Pengguna
* **Voting Sederhana:** Mekanisme Setuju/Tidak Setuju (sekali suara per kebijakan).
* **Pengiriman Aspirasi:** Pengguna dapat mengirimkan masukan teks yang dicatat sistem untuk ditinjau.

## Status Pengerjaan

Proyek ini sedang dalam tahap pengerjaan MVP sesuai **Rencana Sprint 4 Minggu**.

* **Minggu 1:** Basis Data & Autentikasi (Selesai: Setup lingkungan, Login/Registrasi, JWT).
* **Minggu 2:** Modul Kebijakan (Sedang Berjalan: Implementasi API dan Halaman Daftar/Detail Kebijakan).
* **Minggu 3:** Mekanisme Partisipasi (Akan Datang).
* **Minggu 4:** Finalisasi & Testing (Akan Datang).

## Akses Repositori

```bash
$ git clone https://github.com/mkaspulanwar/SUARA-230104040212-TUGASAKHIR.git
```

---
