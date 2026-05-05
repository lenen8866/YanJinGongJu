# 快速“体检”一个 SQLite 数据库，帮你看清结构 + 数据情况
import sqlite3

db_path = r"../hudong_old.db"

conn = sqlite3.connect(db_path)
conn.row_factory = sqlite3.Row
cursor = conn.cursor()

cursor.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;")
tables = [t[0] for t in cursor.fetchall()]

print("📋 数据库详细结构：\n")

for table in tables:
    if table.startswith("sqlite_"):
        continue

    print(f"\n📌 表: {table}")

    # 字段
    cursor.execute(f"PRAGMA table_info(`{table}`);")
    columns = [col["name"] for col in cursor.fetchall()]
    print("字段:", columns)

    # 数据量
    cursor.execute(f"SELECT COUNT(*) FROM `{table}`;")
    count = cursor.fetchone()[0]
    print("数据量:", count)

    # 示例数据
    cursor.execute(f"SELECT * FROM `{table}` LIMIT 2;")
    rows = cursor.fetchall()

    for r in rows:
        print("示例:", dict(r))

conn.close()