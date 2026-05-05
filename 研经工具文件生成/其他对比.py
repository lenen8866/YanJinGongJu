import sqlite3

old_db = "hudong_old.db"
new_db = "hudong_new.db"

tables = ["spirituality", "spirituality_category"]

conn1 = sqlite3.connect(old_db)
conn2 = sqlite3.connect(new_db)

for table in tables:
    print(f"\n{'='*60}")
    print(f"表: {table}")

    c1 = conn1.cursor()
    c2 = conn2.cursor()

    c1.execute("SELECT sql FROM sqlite_master WHERE name=?", (table,))
    c2.execute("SELECT sql FROM sqlite_master WHERE name=?", (table,))

    sql1 = c1.fetchone()[0]
    sql2 = c2.fetchone()[0]

    print(f"\n旧库 repr:\n{repr(sql1)}")
    print(f"\n新库 repr:\n{repr(sql2)}")
    print(f"\n旧库长度: {len(sql1)}  新库长度: {len(sql2)}")

    if sql1 == sql2:
        print("✅ 完全一致")
    else:
        print("❌ 不一致，逐字符定位...")
        min_len = min(len(sql1), len(sql2))
        for i in range(min_len):
            if sql1[i] != sql2[i]:
                print(f"  第 {i} 个字符不同:")
                print(f"  旧={repr(sql1[i])}  新={repr(sql2[i])}")
                print(f"  旧上下文: {repr(sql1[max(0,i-15):i+15])}")
                print(f"  新上下文: {repr(sql2[max(0,i-15):i+15])}")
                break

conn1.close()
conn2.close()