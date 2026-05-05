import sqlite3
import os
import re
from datetime import datetime

DB_PATH = r"E:\My_Project\研经工具\旧数据库处理\hudong_new.db"
ROOT_DIR = r"E:\My_Project\研经工具\all_books"

# ========= 工具 =========

def get_index_prefix(name):
    m = re.match(r'^(\d+)-', name)
    return int(m.group(1)) if m else 0

def clean_name(name):
    """去掉数字前缀：'01-书名' → '书名'"""
    return re.sub(r'^\d+-', '', name)

def read_txt(path):
    with open(path, "r", encoding="utf-8") as f:
        return f.read()

def ensure_dir(path):

    os.makedirs(path, exist_ok=True)

def today_str():
    return datetime.now().strftime("%Y-%m-%d")

# ========= 初始化 =========
ensure_dir(os.path.dirname(DB_PATH))

conn = sqlite3.connect(DB_PATH)
cursor = conn.cursor()

print("🚀 初始化数据库...")

cursor.executescript("""
DROP TABLE IF EXISTS volume;
DROP TABLE IF EXISTS chapter;
DROP TABLE IF EXISTS spirituality;
DROP TABLE IF EXISTS baike;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS baike_category;
DROP TABLE IF EXISTS android_metadata;
DROP TABLE IF EXISTS bookmark;
DROP TABLE IF EXISTS spirituality_category;
DROP TABLE IF EXISTS sysconfig;

CREATE TABLE category(id INTEGER,cateName varchar(20),volCount INTEGER,parentId INTEGER);
CREATE TABLE volume(id INTEGER PRIMARY KEY,volName varchar(16),chpCount INTEGER,categoryId INTEGER,updateTime varchar(19));
CREATE TABLE chapter(id INTEGER PRIMARY KEY, indexId integer, name varchar(20), volumeId varchar(2), content text,categoryId integer,parentId integer);
CREATE INDEX idxT1 ON chapter(categoryId);
CREATE INDEX idxT2 ON chapter(parentId);
CREATE TABLE "baike_category" (
\t`id`\tINTEGER,
\t`cateName`\tvarchar(20),
\t`volCount`\tINTEGER,
\t`parentId`\tINTEGER,
\t`updateTime`\tINTEGER,
\tPRIMARY KEY(`id`)
);
CREATE TABLE baike(id INTEGER PRIMARY KEY,name varchar(20),indexId INTEGER,content text,categoryId INTEGER,cateName varchar(20) );
CREATE TABLE android_metadata (locale TEXT);
CREATE TABLE bookmark(id INTEGER PRIMARY KEY,volumeId varchar(4),volumeName varchar(16),chapterId varchar(4),chapterName varchar(50),chapterPosition INTEGER,chpCount INTEGER,type INTEGER,content varchar(500),description varchar(500));
CREATE TABLE sysconfig(ckey varchar(20),cvalue varchar(20));
""")

cursor.execute("CREATE TABLE [spirituality](\r\n  [id] INTEGER, \r\n  [daytime] varchar(4), \r\n  [book] varchar(20), \r\n  [name] varchar(20), \r\n  [content] text, \r\n  [parent] VARCHAR(20))")
cursor.execute("CREATE TABLE [spirituality_category](\r\n  [id] INTEGER PRIMARY KEY, \r\n  [cateName] VARCHAR(20), \r\n  [volCount] INTEGER, \r\n  [parentId] INTEGER, \r\n  [updateTime] INTEGER)")

# =====================================================
# 固定数据
# =====================================================
cursor.execute("INSERT INTO android_metadata (locale) VALUES (?)", ("zh_CN",))

cursor.executemany("INSERT INTO sysconfig (ckey, cvalue) VALUES (?, ?)", [
    ("LastReadFile", "01_1.txt"),
    ("MainTextSize",  "20"),
    ("TextColor",     "-5592406"),
    ("BgColor",       "-16777216"),
])

# =====================================================
# 1️⃣ 书籍导入（01-书籍）
# category：插入一级 → 立即插完该一级所有二级 → 下一个一级
# =====================================================
print("📖 导入书籍...")

books_root = os.path.join(ROOT_DIR, "01-书籍")

volume_id = 1
chapter_id = 1
category_id = 1
today = today_str()

for lvl1 in sorted(os.listdir(books_root)):
    lvl1_path = os.path.join(books_root, lvl1)
    if not os.path.isdir(lvl1_path):
        continue
    lvl1_name = clean_name(lvl1)
    lvl1_cat_id = category_id

    lvl2_dirs = sorted([d for d in os.listdir(lvl1_path) if os.path.isdir(os.path.join(lvl1_path, d))])

    cursor.execute("INSERT INTO category (id, cateName, volCount, parentId) VALUES (?, ?, ?, ?)",
                   (category_id, lvl1_name, len(lvl2_dirs), 0))
    category_id += 1

    for lvl2 in lvl2_dirs:
        lvl2_path = os.path.join(lvl1_path, lvl2)
        lvl2_name = clean_name(lvl2)
        current_cat_id = category_id

        book_dirs = sorted([d for d in os.listdir(lvl2_path) if os.path.isdir(os.path.join(lvl2_path, d))])

        cursor.execute("INSERT INTO category (id, cateName, volCount, parentId) VALUES (?, ?, ?, ?)",
                       (category_id, lvl2_name, len(book_dirs), lvl1_cat_id))
        category_id += 1

        for book in book_dirs:
            book_path = os.path.join(lvl2_path, book)
            book_name = clean_name(book)
            print("   📖", book_name)

            txt_files = sorted([f for f in os.listdir(book_path) if f.endswith(".txt")])

            cursor.execute(
                "INSERT INTO volume (id, volName, chpCount, categoryId, updateTime) VALUES (?, ?, ?, ?, ?)",
                (volume_id, book_name, len(txt_files), current_cat_id, today)
            )

            for ch_index, file in enumerate(txt_files, start=1):
                content = read_txt(os.path.join(book_path, file))
                ch_name = clean_name(file).replace(".txt", "")

                cursor.execute(
                    "INSERT INTO chapter (id, indexId, name, volumeId, content, categoryId, parentId) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    (chapter_id, ch_index, ch_name, str(volume_id), content, current_cat_id, lvl1_cat_id)
                )
                chapter_id += 1

            volume_id += 1

# =====================================================
# 2️⃣ 灵修导入（02-灵修）
# spirituality.parent → 保留原始文件夹名（含前缀），如 '010-每日晨钟'
# spirituality_category.updateTime → None
# =====================================================
print("\n📙 导入灵修...")

sp_root = os.path.join(ROOT_DIR, "02-灵修")
sp_cat_id = 1
sp_id = 1

for parent in sorted(os.listdir(sp_root)):
    parent_path = os.path.join(sp_root, parent)
    if not os.path.isdir(parent_path):
        continue
    parent_raw = parent          # 保留原始名，如 '010-每日晨钟'
    parent_name = clean_name(parent)

    cursor.execute(
        "INSERT INTO spirituality_category (id, cateName, volCount, parentId, updateTime) VALUES (?, ?, ?, ?, ?)",
        (sp_cat_id, parent_name, 0, 0, 0)
    )
    sp_cat_id += 1

    for book in sorted(os.listdir(parent_path)):
        book_path = os.path.join(parent_path, book)
        if not os.path.isdir(book_path):
            continue
        book_name = clean_name(book)

        for file in sorted(os.listdir(book_path)):
            if not file.endswith(".txt"):
                continue

            content = read_txt(os.path.join(book_path, file))
            raw_name = clean_name(file).replace(".txt", "")

            day_match = re.match(r'^(\d+月\d+日)-(.+)$', raw_name)
            if day_match:
                daytime = day_match.group(1)
                name = day_match.group(2)
            else:
                daytime = ""
                name = raw_name

            cursor.execute(
                "INSERT INTO spirituality (id, daytime, name, content, parent, book) VALUES (?, ?, ?, ?, ?, ?)",
                (sp_id, daytime, name, content, parent_raw, book_name)
            )
            sp_id += 1

# =====================================================
# 3️⃣ 百科导入（03-百科）
# baike.name       → 去掉数字前缀：'0001-阿爸(Abba)' → '阿爸(Abba)'
# baike.cateName   → 去掉前缀：'010-百科' → '百科'
# baike_category.cateName → 同上去掉前缀，排序按序号
# =====================================================
print("\n📘 导入百科...")

baike_root = os.path.join(ROOT_DIR, "03-百科")
bk_id = 1
bk_cat_id = 1

for cat in sorted(os.listdir(baike_root)):
    cat_path = os.path.join(baike_root, cat)
    if not os.path.isdir(cat_path):
        continue

    # 排序按文件夹序号，cateName 去掉前缀：'010-百科' → '百科'
    cat_name = clean_name(cat)
    txt_files = sorted([f for f in os.listdir(cat_path) if f.endswith(".txt")])
    now_cn = datetime.now().strftime("%Y年%m月%d日")

    cursor.execute(
        "INSERT INTO baike_category (id, cateName, volCount, parentId, updateTime) VALUES (?, ?, ?, ?, ?)",
        (bk_cat_id, cat_name, len(txt_files), 0, now_cn)
    )

    for file in txt_files:
        txt_content = read_txt(os.path.join(cat_path, file))
        index_num = get_index_prefix(file)
        name = clean_name(file).replace(".txt", "")

        cursor.execute(
            "INSERT INTO baike (id, name, indexId, content, categoryId, cateName) VALUES (?, ?, ?, ?, ?, ?)",
            (bk_id, name, index_num, txt_content, bk_cat_id, cat_name)
        )
        bk_id += 1

    bk_cat_id += 1

# =====================================================
conn.commit()
conn.close()

print("\n✅ DB 生成完成！")