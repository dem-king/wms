#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
使用Python将数据导入MySQL数据库
"""

import pandas as pd
import re
import os
import pymysql
from typing import Dict, List, Tuple
from dataclasses import dataclass

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'wqy123456',
    'database': 'wms',
    'charset': 'utf8mb4'
}


@dataclass
class Category:
    category_name: str
    category_code: str
    sort_order: int
    is_consumable: int = 0


@dataclass
class SubCategory:
    category_name: str
    sub_category_name: str
    sub_category_code: str
    sort_order: int


@dataclass
class Item:
    item_code: str
    item_name: str
    pinyin: str
    model: str
    spec: str
    unit: str
    brand: str
    category_name: str
    sub_category_name: str
    stock_lower_limit: int
    stock_upper_limit: int
    purchase_price: float
    remark: str
    status: int
    is_consumable: int = 0
    is_returnable: int = 1
    stock_qty: int = 0


def get_connection():
    """获取数据库连接"""
    return pymysql.connect(**DB_CONFIG)


def parse_category_data(df: pd.DataFrame) -> Tuple[List[Category], List[SubCategory]]:
    """解析类别数据"""
    df = df[df['类别名称'] != '所有类别']
    df = df[df['上级类别'] != 'TOP']

    main_categories = {}
    sub_categories = []

    for _, row in df.iterrows():
        cat_name = str(row['类别名称']).strip()
        sort_order = int(row['排序码']) if pd.notna(row['排序码']) else 0

        if cat_name.startswith('普天'):
            main_cat_name = '普天备件'
            main_cat_code = 'PT'
        elif cat_name.startswith('科盛'):
            main_cat_name = '科盛备件'
            main_cat_code = 'KS'
        else:
            main_cat_name = '其他备件'
            main_cat_code = 'QT'

        if main_cat_name not in main_categories:
            main_categories[main_cat_name] = Category(
                category_name=main_cat_name,
                category_code=main_cat_code,
                sort_order=1 if main_cat_code == 'PT' else (2 if main_cat_code == 'KS' else 3)
            )

        sub_cat_code = generate_code(cat_name)
        sub_categories.append(SubCategory(
            category_name=main_cat_name,
            sub_category_name=cat_name,
            sub_category_code=sub_cat_code,
            sort_order=sort_order
        ))

    return list(main_categories.values()), sub_categories


def generate_code(name: str) -> str:
    """生成编码"""
    code = ''
    for char in name:
        if '\u4e00' <= char <= '\u9fff':
            code += char
        elif char.isalnum():
            code += char.upper()
    code = re.sub(r'[^\w]', '', code)[:10]
    return code if code else 'OTHER'


def parse_item_data(df: pd.DataFrame) -> List[Item]:
    """解析物品数据"""
    items = []

    for _, row in df.iterrows():
        item_code = str(row['产品编号']).strip() if pd.notna(row['产品编号']) else ''
        item_name = str(row['产品名称']).strip() if pd.notna(row['产品名称']) else ''
        pinyin = str(row['拼音码']).strip() if pd.notna(row['拼音码']) else ''
        unit = str(row['单位']).strip() if pd.notna(row['单位']) else '件'
        spec = str(row['规格']).strip() if pd.notna(row['规格']) else ''
        brand = str(row['品牌']).strip() if pd.notna(row['品牌']) else ''
        category = str(row['类别']).strip() if pd.notna(row['类别']) else ''

        if category.startswith('普天'):
            category_name = '普天备件'
        elif category.startswith('科盛'):
            category_name = '科盛备件'
        else:
            category_name = '其他备件'

        sub_category_name = category
        stock_lower = int(row['库存下限']) if pd.notna(row['库存下限']) else 0
        stock_upper = int(row['库存上限']) if pd.notna(row['库存上限']) else 0
        price = float(row['预设进价']) if pd.notna(row['预设进价']) else 0.0

        remarks = []
        if pd.notna(row.get('货架货位号')):
            remarks.append(f"货位:{row['货架货位号']}")
        for i in range(1, 7):
            col = f'备注{i}'
            if col in row and pd.notna(row[col]):
                remarks.append(str(row[col]))
        remark = '; '.join(remarks) if remarks else ''

        disabled = str(row['停用']).strip() if pd.notna(row['停用']) else ''
        status = 0 if disabled in ['是', '1', 'True', 'true'] else 1

        items.append(Item(
            item_code=item_code,
            item_name=item_name,
            pinyin=pinyin,
            model='',
            spec=spec,
            unit=unit,
            brand=brand,
            category_name=category_name,
            sub_category_name=sub_category_name,
            stock_lower_limit=stock_lower,
            stock_upper_limit=stock_upper,
            purchase_price=price,
            remark=remark,
            status=status
        ))

    return items


def import_categories(conn, categories: List[Category]) -> Dict[str, int]:
    """导入主类目"""
    cursor = conn.cursor()
    cat_id_map = {}

    for i, cat in enumerate(categories, 1):
        cat_id = 1000000000000000000 + i
        cat_id_map[cat.category_name] = cat_id

        sql = """
        INSERT INTO wms_category (id, category_name, category_code, category_color, icon, sort_order, is_consumable, del_flag, create_time, create_by)
        VALUES (%s, %s, %s, NULL, NULL, %s, %s, 0, NOW(), 'system')
        ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), category_code=VALUES(category_code)
        """
        cursor.execute(sql, (cat_id, cat.category_name, cat.category_code, cat.sort_order, cat.is_consumable))

    conn.commit()
    cursor.close()
    return cat_id_map


def import_sub_categories(conn, sub_categories: List[SubCategory], cat_id_map: Dict[str, int]) -> Dict[str, int]:
    """导入细分类目"""
    cursor = conn.cursor()
    sub_cat_id_map = {}

    for i, sub in enumerate(sub_categories, 1):
        sub_cat_id = 2000000000000000000 + i
        parent_id = cat_id_map.get(sub.category_name, 0)
        sub_cat_id_map[sub.sub_category_name] = sub_cat_id

        sql = """
        INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
        VALUES (%s, %s, %s, %s, %s, 0, NOW(), 'system')
        ON DUPLICATE KEY UPDATE sub_category_name=VALUES(sub_category_name), category_id=VALUES(category_id)
        """
        cursor.execute(sql, (sub_cat_id, parent_id, sub.sub_category_name, sub.sub_category_code, sub.sort_order))

    conn.commit()
    cursor.close()
    return sub_cat_id_map


def import_items(conn, items: List[Item], cat_id_map: Dict[str, int], sub_cat_id_map: Dict[str, int]):
    """导入物品档案"""
    cursor = conn.cursor()

    batch_size = 100
    total = len(items)

    for batch_start in range(0, total, batch_size):
        batch_end = min(batch_start + batch_size, total)
        batch_items = items[batch_start:batch_end]

        for i, item in enumerate(batch_items, 1):
            item_id = 3000000000000000000 + batch_start + i
            category_id = cat_id_map.get(item.category_name, 0)
            sub_category_id = sub_cat_id_map.get(item.sub_category_name, 0)

            sql = """
            INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                                  supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                                  stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NULL, %s, %s, %s, %s, %s, %s, %s, NULL, NULL, %s, 0, NOW(), 'system')
            ON DUPLICATE KEY UPDATE item_name=VALUES(item_name), spec=VALUES(spec), brand=VALUES(brand)
            """
            cursor.execute(sql, (
                item_id, item.item_code, item.item_name, item.pinyin, item.model, item.spec, item.unit, item.brand,
                category_id, sub_category_id, item.status, item.is_consumable, item.is_returnable,
                item.purchase_price, item.stock_qty, item.stock_lower_limit, item.stock_upper_limit, item.remark
            ))

        conn.commit()
        print(f"    已导入 {batch_end}/{total} 条物品记录")

    cursor.close()


def main():
    print("=" * 60)
    print("WMS数据导入到MySQL")
    print("=" * 60)

    script_dir = os.path.dirname(os.path.abspath(__file__))

    # 读取数据
    print("\n[1/4] 读取类别数据...")
    cat_df = pd.read_excel(os.path.join(script_dir, '基本信息.xls'))
    categories, sub_categories = parse_category_data(cat_df)
    print(f"    主类目: {len(categories)} 个")
    print(f"    细分类目: {len(sub_categories)} 个")

    print("\n[2/4] 读取物品数据...")
    item_df = pd.read_excel(os.path.join(script_dir, '产品资料.xls'))
    items = parse_item_data(item_df)
    print(f"    物品记录: {len(items)} 条")

    # 连接数据库
    print("\n[3/4] 连接数据库...")
    conn = get_connection()
    print("    数据库连接成功")

    # 导入数据
    print("\n[4/4] 导入数据到数据库...")

    print("  - 导入主类目...")
    cat_id_map = import_categories(conn, categories)
    print(f"    完成: {len(categories)} 个主类目")

    print("  - 导入细分类目...")
    sub_cat_id_map = import_sub_categories(conn, sub_categories, cat_id_map)
    print(f"    完成: {len(sub_categories)} 个细分类目")

    print("  - 导入物品档案...")
    import_items(conn, items, cat_id_map, sub_cat_id_map)
    print(f"    完成: {len(items)} 条物品记录")

    conn.close()

    print("\n" + "=" * 60)
    print("数据导入完成！")
    print("=" * 60)


if __name__ == '__main__':
    main()
