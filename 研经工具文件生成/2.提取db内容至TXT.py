import sqlite3
import os

DB_PATH = r"hudong_old.db"
OUT_DIR = r"E:\My_Project\研经工具\all_books"

# ================= 工具 =================

def safe_name(name):
    if not name:
        return "未命名"
    name = str(name)
    return name.strip()

def mkdir(path):
    if not os.path.exists(path):
        os.makedirs(path)

def write_txt(path, content):
    with open(path, "w", encoding="utf-8") as f:
        f.write(content or "")

# ================= 连接 =================

conn = sqlite3.connect(DB_PATH)
cursor = conn.cursor()

mkdir(OUT_DIR)

books_root = os.path.join(OUT_DIR, "01-书籍")
sp_root    = os.path.join(OUT_DIR, "02-灵修")
baike_root = os.path.join(OUT_DIR, "03-百科")

mkdir(books_root)
mkdir(sp_root)
mkdir(baike_root)

print("🚀 开始导出...\n")

# =====================================================
# 1️⃣ 书籍（带分类树）
# =====================================================
print("📖 处理书籍分类...")

cursor.execute("SELECT id, cateName, parentId FROM category")
rows = cursor.fetchall()

cat_map = {cid: {"name": safe_name(name), "parent": pid} for cid, name, pid in rows}

level1_ids = [cid for cid, v in cat_map.items() if v["parent"] == 0]
level1_ids.sort()

level1_dirs = {}

for i, cid in enumerate(level1_ids, 1):
    name = cat_map[cid]["name"]
    path = os.path.join(books_root, f"{i:02d}-{name}")
    mkdir(path)
    level1_dirs[cid] = path

level2_map = {}

for pid in level1_ids:
    children = [cid for cid, v in cat_map.items() if v["parent"] == pid]
    children.sort()

    for i, cid in enumerate(children, 1):
        name = cat_map[cid]["name"]
        path = os.path.join(level1_dirs[pid], f"{i:02d}-{name}")
        mkdir(path)
        level2_map[cid] = path

# =====================================================
# 书籍导出
# =====================================================
print("📖 导出书籍...")

cursor.execute("SELECT id, volName, categoryId FROM volume ORDER BY id")
books = cursor.fetchall()

book_group = {}

for book_id, name, cid in books:
    if cid not in level2_map:
        continue
    book_group.setdefault(cid, []).append((book_id, safe_name(name)))

for cid, book_list in book_group.items():
    for idx, (book_id, book_name) in enumerate(book_list, 1):
        book_dir = os.path.join(level2_map[cid], f"{idx:03d}-{book_name}")
        mkdir(book_dir)

        print(f"   📖 {book_name}")

        cursor.execute("""
            SELECT name, content
            FROM chapter
            WHERE volumeId=?
            ORDER BY indexId
        """, (book_id,))

        chapters = cursor.fetchall()

        for i, (ch_name, content) in enumerate(chapters, 1):
            filename = f"{i:03d}-{safe_name(ch_name)}.txt"
            write_txt(os.path.join(book_dir, filename), content)

# =====================================================
# 2️⃣ 灵修
# =====================================================
print("\n📙 导出灵修...")

cursor.execute("""
    SELECT id, daytime, name, content, parent, book
    FROM spirituality
    ORDER BY id
""")

rows = cursor.fetchall()

sp_group = {}

for sid, day, name, content, parent, book in rows:
    p = safe_name(parent or "未分类")
    b = safe_name(book or "未分类书籍")
    if p not in sp_group:
        sp_group[p] = {}
    if b not in sp_group[p]:
        sp_group[p][b] = []
    sp_group[p][b].append((day, name, content))

for i, (parent_name, books) in enumerate(sp_group.items(), 1):
    level1 = os.path.join(sp_root, f"{parent_name}")
    mkdir(level1)

    for j, (book_name, entries) in enumerate(books.items(), 1):
        level2 = os.path.join(level1, f"{j:02d}-{book_name}")
        mkdir(level2)

        for day, name, content in entries:
            filename = f"{safe_name(day)}-{safe_name(name)}.txt"
            write_txt(os.path.join(level2, filename), content)

# =====================================================
# 3️⃣ 百科
# =====================================================
print("\n📘 导出百科...")

cursor.execute("SELECT id, cateName FROM baike_category")

for i, (cid, cname) in enumerate(cursor.fetchall(), 1):
    cat_dir = os.path.join(baike_root, f"{i:02d}-{safe_name(cname)}")
    mkdir(cat_dir)

    cursor.execute("""
        SELECT name, content
        FROM baike
        WHERE categoryId=?
        ORDER BY indexId
    """, (cid,))

    for j, (name, content) in enumerate(cursor.fetchall(), 1):
        filename = f"{j:04d}-{safe_name(name)}.txt"
        write_txt(os.path.join(cat_dir, filename), content)

conn.close()

print("\n✅ 导出完成")