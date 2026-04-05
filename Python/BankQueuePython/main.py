"""
============================================================
  BANK QUEUE SYSTEM - Sistem Antrian Bank
  Bahasa     : Python
  GUI        : Tkinter (built-in, tidak perlu install)
  Struktur   : Queue (Linked List)
  TTS        : pyttsx3  →  pip install pyttsx3
============================================================
"""

import tkinter as tk
from tkinter import simpledialog, messagebox
from queue_antrian import QueueAntrian
import threading

# ── TTS ────────────────────────────────────────────────────
TTS_AVAILABLE = False
try:
    import pyttsx3
    TTS_AVAILABLE = True
except ImportError:
    pass

def speak(text: str):
    if not TTS_AVAILABLE:
        print(f"[TTS] {text}")
        return
    def _run():
        try:
            engine = pyttsx3.init()
            engine.setProperty("rate", 145)
            engine.say(text)
            engine.runAndWait()
        except Exception:
            pass
    threading.Thread(target=_run, daemon=True).start()


# ── Palet Warna ─────────────────────────────────────────────
C = {
    "bg":        "#0A0F1E",
    "card":      "#121929",
    "row":       "#19203B",
    "row1":      "#1E3C78",
    "blue":      "#1E78FF",
    "cyan":      "#00D2D2",
    "gold":      "#FFB900",
    "white":     "#E6EBF0",
    "gray":      "#828FAF",
    "success":   "#28C878",
    "danger":    "#FF4646",
    "border":    "#1E3A6E",
    "hdr":       "#0D1A4A",
}

F = {
    "title":  ("Courier New", 20, "bold"),
    "sub":    ("Helvetica", 10),
    "mono":   ("Courier New", 13, "bold"),
    "mono_s": ("Courier New", 11),
    "bold":   ("Helvetica", 14, "bold"),
    "med":    ("Helvetica", 12),
    "small":  ("Helvetica", 10),
    "huge":   ("Courier New", 64, "bold"),
    "count":  ("Courier New", 15, "bold"),
    "num_row":("Courier New", 20, "bold"),
}


class BankQueueApp(tk.Tk):
    def __init__(self):
        super().__init__()
        self.queue = QueueAntrian()
        self.title("Bank Queue System")
        self.geometry("960x680")
        self.minsize(780, 560)
        self.configure(bg=C["bg"])

        self._build_header()
        self._build_body()
        self._build_footer()
        self._refresh()

    # ══════════════════════════════════════════════════════════
    #  HEADER
    # ══════════════════════════════════════════════════════════
    def _build_header(self):
        hdr = tk.Frame(self, bg=C["hdr"], height=68)
        hdr.pack(fill="x")
        hdr.pack_propagate(False)

        # Garis bawah biru
        tk.Frame(hdr, bg=C["blue"], height=2).place(relx=0, rely=1, relwidth=1, anchor="sw")

        tk.Label(hdr, text="BANK QUEUE SYSTEM",
                 font=F["title"], bg=C["hdr"], fg=C["cyan"]).place(x=24, y=10)
        tk.Label(hdr, text="Sistem Antrian Digital  •  Linked List Queue",
                 font=F["sub"], bg=C["hdr"], fg=C["gray"]).place(x=26, y=42)

        self.lbl_jumlah = tk.Label(hdr, text="0 Antrian",
                                   font=F["count"], bg=C["hdr"], fg=C["gold"])
        self.lbl_jumlah.place(relx=1.0, x=-24, y=20, anchor="ne")

    # ══════════════════════════════════════════════════════════
    #  BODY
    # ══════════════════════════════════════════════════════════
    def _build_body(self):
        body = tk.Frame(self, bg=C["bg"])
        body.pack(fill="both", expand=True, padx=16, pady=12)

        # ─ Kiri ──────────────────────────────────────────────
        left = tk.Frame(body, bg=C["bg"], width=290)
        left.pack(side="left", fill="y", padx=(0, 12))
        left.pack_propagate(False)

        self._build_called_card(left)

        for text, bg, fg, cmd in [
            ("➕  Ambil Antrian",   C["blue"],    "white",    self._ambil),
            ("📢  Panggil Antrian", C["cyan"],    C["bg"],    self._panggil),
            ("🔄  Reset Antrian",   "#282E48",    C["gray"],  self._reset),
        ]:
            self._btn(left, text, bg, fg, cmd)

        # ─ Kanan (daftar antrian) ─────────────────────────────
        right_frame = tk.Frame(body, bg=C["card"], bd=0,
                               highlightthickness=1, highlightbackground=C["border"])
        right_frame.pack(side="left", fill="both", expand=True)

        tk.Label(right_frame, text="DAFTAR ANTRIAN",
                 font=F["mono"], bg=C["card"], fg=C["gold"]
                 ).pack(anchor="w", padx=16, pady=(14, 6))
        tk.Frame(right_frame, bg=C["border"], height=1).pack(fill="x", padx=16)

        # Canvas + Scrollbar untuk daftar antrian
        canvas = tk.Canvas(right_frame, bg=C["card"], highlightthickness=0)
        vsb    = tk.Scrollbar(right_frame, orient="vertical", command=canvas.yview)
        self.frame_list = tk.Frame(canvas, bg=C["card"])

        self.frame_list.bind(
            "<Configure>",
            lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
        )
        canvas.create_window((0, 0), window=self.frame_list, anchor="nw")
        canvas.configure(yscrollcommand=vsb.set)

        vsb.pack(side="right", fill="y", pady=8)
        canvas.pack(side="left", fill="both", expand=True, padx=(16, 0), pady=8)
        canvas.bind_all("<MouseWheel>",
                        lambda e: canvas.yview_scroll(-(e.delta // 120), "units"))

    def _build_called_card(self, parent):
        card = tk.Frame(parent, bg=C["card"], bd=0,
                        highlightthickness=1, highlightbackground=C["blue"])
        card.pack(fill="x", pady=(0, 14))

        tk.Label(card, text="SEDANG DIPANGGIL",
                 font=F["mono_s"], bg=C["card"], fg=C["gray"]).pack(pady=(18, 0))

        self.lbl_nomor = tk.Label(card, text="—",
                                  font=F["huge"], bg=C["card"], fg=C["cyan"])
        self.lbl_nomor.pack()

        self.lbl_nama = tk.Label(card, text="Belum ada",
                                 font=F["med"], bg=C["card"], fg=C["white"])
        self.lbl_nama.pack(pady=(0, 20))

    def _btn(self, parent, text, bg, fg, cmd):
        b = tk.Button(parent, text=text, font=F["bold"],
                      bg=bg, fg=fg, activebackground=bg, activeforeground=fg,
                      relief="flat", cursor="hand2", bd=0, pady=13, command=cmd)
        b.pack(fill="x", pady=5)
        lighter = self._lighten(bg)
        b.bind("<Enter>", lambda e: b.configure(bg=lighter))
        b.bind("<Leave>", lambda e: b.configure(bg=bg))

    # ══════════════════════════════════════════════════════════
    #  FOOTER
    # ══════════════════════════════════════════════════════════
    def _build_footer(self):
        f = tk.Frame(self, bg=C["bg"])
        f.pack(fill="x", padx=16, pady=(0, 8))

        self.lbl_status = tk.Label(f, text="Sistem siap. Silahkan ambil nomor antrian.",
                                   font=("Helvetica", 10, "italic"), bg=C["bg"], fg=C["gray"])
        self.lbl_status.pack(side="left")

        tk.Label(f, text="v1.0  •  Python + Tkinter  •  Queue (Linked List)",
                 font=F["mono_s"], bg=C["bg"], fg="#323C5A").pack(side="right")

    # ══════════════════════════════════════════════════════════
    #  AKSI
    # ══════════════════════════════════════════════════════════
    def _ambil(self):
        nama = simpledialog.askstring("Ambil Nomor Antrian",
                                      "Masukkan nama Anda:", parent=self)
        if nama is None:
            return
        nama = nama.strip()
        if not nama:
            self._status("❌  Nama tidak boleh kosong.", C["danger"])
            return
        nomor = self.queue.enqueue(nama)
        self._status(f"✅  Nomor {nomor} – {nama} berhasil didaftarkan.", C["success"])
        self._refresh()

    def _panggil(self):
        if self.queue.is_empty():
            self._status("⚠️  Antrian kosong.", C["gold"])
            self.lbl_nomor.config(text="—", fg=C["cyan"])
            self.lbl_nama.config(text="Antrian kosong")
            return
        node = self.queue.dequeue()
        self.lbl_nomor.config(text=str(node.nomor_antrian), fg=C["cyan"])
        self.lbl_nama.config(text=node.nama)
        self._status(f"📢  Memanggil nomor {node.nomor_antrian} – {node.nama}", C["cyan"])
        self._flash()
        speak(f"Nomor {node.nomor_antrian} {node.nama} silahkan menuju loket")
        self._refresh()

    def _reset(self):
        if not messagebox.askyesno("Konfirmasi Reset",
                                   "Reset semua antrian?\nTindakan ini tidak bisa dibatalkan.",
                                   parent=self):
            return
        self.queue.reset()
        self.lbl_nomor.config(text="—", fg=C["cyan"])
        self.lbl_nama.config(text="Belum ada")
        self._refresh()
        self._status("🔄  Antrian direset.", C["gray"])

    # ══════════════════════════════════════════════════════════
    #  REFRESH DAFTAR ANTRIAN
    # ══════════════════════════════════════════════════════════
    def _refresh(self):
        for w in self.frame_list.winfo_children():
            w.destroy()

        nodes = self.queue.get_all()
        sz = len(nodes)
        self.lbl_jumlah.config(text=f"{sz} Antrian")

        if sz == 0:
            tk.Label(self.frame_list, text="Antrian kosong",
                     font=("Helvetica", 12, "italic"),
                     bg=C["card"], fg=C["gray"]).pack(pady=30)
            return

        for i, node in enumerate(nodes):
            self._build_row(i + 1, node)

    def _build_row(self, urutan: int, node):
        first = urutan == 1
        bg     = C["row1"] if first else C["row"]
        border = C["blue"] if first else C["border"]

        row = tk.Frame(self.frame_list, bg=bg, bd=0,
                       highlightthickness=1, highlightbackground=border)
        row.pack(fill="x", pady=3, padx=2)

        # Nomor antrian
        tk.Label(row, text=f"{node.nomor_antrian:03d}",
                 font=F["num_row"], bg=bg,
                 fg=C["cyan"] if first else C["blue"], width=4
                 ).pack(side="left", padx=12, pady=8)

        # Info nama + urutan
        info = tk.Frame(row, bg=bg)
        info.pack(side="left", fill="both", expand=True, pady=6)

        tk.Label(info, text=node.nama, font=F["bold"],
                 bg=bg, fg=C["white"], anchor="w").pack(fill="x")

        sub  = "▶  Berikutnya dipanggil" if first else f"Urutan ke-{urutan}"
        subc = C["gold"] if first else C["gray"]
        tk.Label(info, text=sub, font=F["small"],
                 bg=bg, fg=subc, anchor="w").pack(fill="x")

    # ══════════════════════════════════════════════════════════
    #  HELPER
    # ══════════════════════════════════════════════════════════
    def _flash(self):
        seq = [C["gold"], C["cyan"], C["blue"], C["gold"], C["cyan"], C["cyan"]]
        def step(i=0):
            if i < len(seq):
                self.lbl_nomor.config(fg=seq[i])
                self.after(90, lambda: step(i + 1))
        step()

    def _status(self, msg: str, color: str):
        self.lbl_status.config(text=msg, fg=color)

    @staticmethod
    def _lighten(hex_c: str) -> str:
        try:
            h = hex_c.lstrip("#")
            r, g, b = int(h[0:2],16), int(h[2:4],16), int(h[4:6],16)
            return "#{:02x}{:02x}{:02x}".format(min(255,r+30), min(255,g+30), min(255,b+30))
        except Exception:
            return hex_c


if __name__ == "__main__":
    BankQueueApp().mainloop()
