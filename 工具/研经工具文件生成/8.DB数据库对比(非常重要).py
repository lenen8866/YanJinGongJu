import sqlite3
import sys


# ========================
# 基础信息获取
# ========================

def get_tables(conn):
    cursor = conn.cursor()
    cursor.execute("SELECT name, type FROM sqlite_master WHERE type IN ('table','view') ORDER BY name;")
    return cursor.fetchall()  # [(name, type)]


def get_table_schema(conn, table):
    cursor = conn.cursor()
    cursor.execute(f"PRAGMA table_info('{table}')")
    return cursor.fetchall()


def get_indexes(conn, table):
    cursor = conn.cursor()
    cursor.execute(f"PRAGMA index_list('{table}')")
    indexes = cursor.fetchall()

    result = []
    for idx in indexes:
        name = idx[1]
        unique = idx[2]

        cursor.execute(f"PRAGMA index_info('{name}')")
        cols = cursor.fetchall()

        result.append((name, unique, cols))

    return result


def get_create_sql(conn, table):
    cursor = conn.cursor()
    cursor.execute(
        "SELECT sql FROM sqlite_master WHERE name=? AND type IN ('table','view')",
        (table,)
    )
    row = cursor.fetchone()
    return row[0] if row else ""


# ========================
# 对比逻辑
# ========================

def compare_table_list(t1, t2):
    map1 = dict(t1)
    map2 = dict(t2)

    ok = True

    only_old = set(map1) - set(map2)
    only_new = set(map2) - set(map1)

    if only_old:
        print(f"❌ 旧库有但新库缺少的表: {sorted(only_old)}")
        ok = False

    if only_new:
        print(f"❌ 新库多出的表: {sorted(only_new)}")
        ok = False

    for name in set(map1) & set(map2):
        if map1[name] != map2[name]:
            print(f"❌ 表类型不一致 [{name}]: 旧={map1[name]} 新={map2[name]}")
            ok = False

    return ok, sorted(set(map1) & set(map2))


def compare_schema(schema1, schema2, table):
    ok = True

    # 字段顺序
    order1 = [c[1] for c in schema1]
    order2 = [c[1] for c in schema2]

    if order1 != order2:
        print(f"  ❌ 字段顺序不一致:")
        print(f"     旧: {order1}")
        print(f"     新: {order2}")
        ok = False

    cols1 = {c[1]: c for c in schema1}
    cols2 = {c[1]: c for c in schema2}

    only_old = set(cols1) - set(cols2)
    only_new = set(cols2) - set(cols1)

    if only_old:
        print(f"  ❌ 缺少字段: {sorted(only_old)}")
        ok = False

    if only_new:
        print(f"  ❌ 多出字段: {sorted(only_new)}")
        ok = False

    for name in set(cols1) & set(cols2):
        c1, c2 = cols1[name], cols2[name]

        diffs = []

        if c1[2].upper() != c2[2].upper():
            diffs.append(f"类型 {c1[2]} vs {c2[2]}")

        if c1[3] != c2[3]:
            diffs.append(f"NOT NULL {c1[3]} vs {c2[3]}")

        if str(c1[4]) != str(c2[4]):
            diffs.append(f"默认值 {c1[4]} vs {c2[4]}")

        if c1[5] != c2[5]:
            diffs.append(f"主键 {c1[5]} vs {c2[5]}")

        if diffs:
            print(f"  ❌ 字段 [{name}] 不一致: {', '.join(diffs)}")
            ok = False

    return ok


def compare_indexes(idx1, idx2, table):
    ok = True

    map1 = {i[0]: i for i in idx1}
    map2 = {i[0]: i for i in idx2}

    only_old = set(map1) - set(map2)
    only_new = set(map2) - set(map1)

    if only_old:
        print(f"  ❌ 缺少索引: {sorted(only_old)}")
        ok = False

    if only_new:
        print(f"  ❌ 多出索引: {sorted(only_new)}")
        ok = False

    for name in set(map1) & set(map2):
        i1 = map1[name]
        i2 = map2[name]

        if i1[1] != i2[1]:
            print(f"  ❌ 索引 {name} UNIQUE 不一致")
            ok = False

        cols1 = [c[2] for c in i1[2]]
        cols2 = [c[2] for c in i2[2]]

        if cols1 != cols2:
            print(f"  ❌ 索引 {name} 字段不一致:")
            print(f"     旧: {cols1}")
            print(f"     新: {cols2}")
            ok = False

    return ok


def compare_create_sql(conn1, conn2, table):
    sql1 = get_create_sql(conn1, table)
    sql2 = get_create_sql(conn2, table)

    if sql1 != sql2:
        print(f"  ❌ CREATE SQL 不一致")
        print(f"     旧: {sql1}")
        print(f"     新: {sql2}")
        return False

    return True


# ========================
# 主函数
# ========================

def compare_db(db1, db2):
    print(f"\n{'='*60}")
    print(f" SQLite 严格一致性校验")
    print(f" 旧库: {db1}")
    print(f" 新库: {db2}")
    print(f"{'='*60}\n")

    conn1 = sqlite3.connect(db1)
    conn2 = sqlite3.connect(db2)

    tables1 = get_tables(conn1)
    tables2 = get_tables(conn2)

    ok, common_tables = compare_table_list(tables1, tables2)

    for table in common_tables:
        print(f"🔍 检查表: [{table}]")
        table_ok = True

        schema1 = get_table_schema(conn1, table)
        schema2 = get_table_schema(conn2, table)

        if not compare_schema(schema1, schema2, table):
            table_ok = False

        idx1 = get_indexes(conn1, table)
        idx2 = get_indexes(conn2, table)

        if not compare_indexes(idx1, idx2, table):
            table_ok = False

        if not compare_create_sql(conn1, conn2, table):
            table_ok = False

        if table_ok:
            print("  ✅ 完全一致")
        else:
            ok = False

        print()

    print("="*60)
    if ok:
        print("🎉 数据库 100% 完全一致（严格模式）")
    else:
        print("⚠️ 存在不一致，请修复后重试")
    print("="*60)

    conn1.close()
    conn2.close()



# ========================
# 入口
# ========================

if __name__ == "__main__":
    old_db = "hudong_old.db"
    new_db = "hudong_new.db"

    if len(sys.argv) == 3:
        old_db = sys.argv[1]
        new_db = sys.argv[2]

    compare_db(old_db, new_db)