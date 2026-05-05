import sqlite3
import sys
import os


# ========================
# 基础获取
# ========================

def get_tables(conn):
    cursor = conn.cursor()
    cursor.execute("""
        SELECT name, type, sql
        FROM sqlite_master
        WHERE type IN ('table','view')
        AND name NOT LIKE 'sqlite_%'
        ORDER BY name
    """)
    return cursor.fetchall()


def get_indexes(conn):
    cursor = conn.cursor()
    cursor.execute("""
        SELECT name, tbl_name, sql
        FROM sqlite_master
        WHERE type='index'
        AND sql IS NOT NULL
    """)
    return cursor.fetchall()


def get_triggers(conn):
    cursor = conn.cursor()
    cursor.execute("""
        SELECT name, tbl_name, sql
        FROM sqlite_master
        WHERE type='trigger'
    """)
    return cursor.fetchall()


def get_row_count(conn, table):
    try:
        cursor = conn.cursor()
        cursor.execute(f"SELECT COUNT(*) FROM '{table}'")
        return cursor.fetchone()[0]
    except:
        return "N/A"


def get_table_schema(conn, table):
    cursor = conn.cursor()
    cursor.execute(f"PRAGMA table_info('{table}')")
    return cursor.fetchall()


# ========================
# 导出 SQL（核心）
# ========================

def export_structure_sql(conn):
    lines = []

    lines.append("-- ====================================")
    lines.append("-- SQLite STRUCTURE EXPORT")
    lines.append("-- ⚠️ DO NOT MODIFY")
    lines.append("-- ====================================")
    lines.append("")

    tables = get_tables(conn)

    for name, typ, sql in tables:
        if sql:
            lines.append(sql.strip() + ";")
            lines.append("")

    indexes = get_indexes(conn)
    if indexes:
        lines.append("-- Indexes")
        for name, tbl, sql in indexes:
            if sql:
                lines.append(sql.strip() + ";")
        lines.append("")

    triggers = get_triggers(conn)
    if triggers:
        lines.append("-- Triggers")
        for name, tbl, sql in triggers:
            if sql:
                lines.append(sql.strip() + ";")
        lines.append("")

    return "\n".join(lines)


# ========================
# 可读报告
# ========================

def generate_report(conn, db_path):
    file_size = os.path.getsize(db_path)
    size_str = f"{file_size / 1024:.1f} KB" if file_size < 1024*1024 else f"{file_size / 1024 / 1024:.2f} MB"

    tables = get_tables(conn)

    lines = []
    lines.append("=" * 60)
    lines.append(f"SQLite 数据库分析报告")
    lines.append(f"文件: {db_path}")
    lines.append(f"大小: {size_str}")
    lines.append(f"对象数量: {len(tables)}")
    lines.append("=" * 60)
    lines.append("")

    for name, typ, _ in tables:
        row_count = get_row_count(conn, name) if typ == "table" else "视图"

        lines.append(f"{'-'*60}")
        lines.append(f"{typ.upper()}: {name} (行数: {row_count})")
        lines.append(f"{'-'*60}")

        schema = get_table_schema(conn, name)

        if schema:
            lines.append("字段:")
            for col in schema:
                lines.append(f"  - {col[1]} ({col[2]}) PK={col[5]} NOTNULL={col[3]} DEFAULT={col[4]}")

        lines.append("")

    return "\n".join(lines)


# ========================
# 主流程
# ========================

def export_db(db_path):
    if not os.path.exists(db_path):
        print(f"❌ 文件不存在: {db_path}")
        return

    conn = sqlite3.connect(db_path)

    print(f"📦 正在分析数据库: {db_path}")

    # 1. 导出 SQL（最重要）
    structure_sql = export_structure_sql(conn)

    # 2. 报告
    report = generate_report(conn, db_path)

    conn.close()

    # 输出路径
    base = db_path.replace(".db", "")
    sql_path = base + "_structure.sql"
    txt_path = base + "_report.txt"

    # 写 SQL
    with open(sql_path, "w", encoding="utf-8") as f:
        f.write("/*\n")
        f.write("AI使用说明：\n")
        f.write("1. 必须严格执行以下SQL\n")
        f.write("2. 不允许修改\n")
        f.write("*/\n\n")
        f.write("-- STRUCTURE SQL START\n\n")
        f.write(structure_sql)
        f.write("\n-- STRUCTURE SQL END\n")

    # 写报告
    with open(txt_path, "w", encoding="utf-8") as f:
        f.write(report)

    print(f"\n✅ 导出完成：")
    print(f"📄 SQL结构文件: {sql_path}")
    print(f"📄 分析报告: {txt_path}")
    print("\n👉 用法：把 SQL 文件直接给 AI 或直接执行即可还原结构")


# ========================
# 入口
# ========================

if __name__ == "__main__":
    db_path = "../hudong_old.db"

    if len(sys.argv) >= 2:
        db_path = sys.argv[1]

    export_db(db_path)