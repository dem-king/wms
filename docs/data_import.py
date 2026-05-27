#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
WMS系统数据导入脚本
将客户提供的Excel数据导入到WMS系统中

数据映射规则：
1. 类别层级：按品牌（普天/科盛/其他）作为主类目，具体类型作为细分类目
2. 物品编号：使用客户已有编号
3. 库存初始化：库存数量设为0
4. 停用物品：状态设为禁用（status=0）
"""

import pandas as pd
import re
import json
from typing import Dict, List, Tuple, Optional
from dataclasses import dataclass, asdict


@dataclass
class Category:
    """主类目"""
    category_name: str
    category_code: str
    sort_order: int
    is_consumable: int = 0


@dataclass
class SubCategory:
    """细分类目"""
    category_name: str  # 所属主类目名称
    sub_category_name: str
    sub_category_code: str
    sort_order: int


@dataclass
class Item:
    """物品档案"""
    item_code: str
    item_name: str
    pinyin: str
    model: str
    spec: str
    unit: str
    brand: str
    category_name: str  # 主类目名称
    sub_category_name: str  # 细分类目名称
    stock_lower_limit: int
    stock_upper_limit: int
    purchase_price: float
    remark: str
    status: int  # 1-启用, 0-禁用
    is_consumable: int = 0
    is_returnable: int = 1
    stock_qty: int = 0


def parse_category_data(df: pd.DataFrame) -> Tuple[List[Category], List[SubCategory]]:
    """
    解析类别数据，按品牌分组生成主类目和细分类目

    规则：
    - 普天开头的 -> 主类目：普天备件
    - 科盛开头的 -> 主类目：科盛备件
    - 其他 -> 主类目：其他备件
    """
    # 排除"所有类别"和"TOP"
    df = df[df['类别名称'] != '所有类别']
    df = df[df['上级类别'] != 'TOP']

    main_categories = {}
    sub_categories = []

    for _, row in df.iterrows():
        cat_name = str(row['类别名称']).strip()
        sort_order = int(row['排序码']) if pd.notna(row['排序码']) else 0

        # 确定主类目
        if cat_name.startswith('普天'):
            main_cat_name = '普天备件'
            main_cat_code = 'PT'
        elif cat_name.startswith('科盛'):
            main_cat_name = '科盛备件'
            main_cat_code = 'KS'
        else:
            main_cat_name = '其他备件'
            main_cat_code = 'QT'

        # 创建主类目（去重）
        if main_cat_name not in main_categories:
            main_categories[main_cat_name] = Category(
                category_name=main_cat_name,
                category_code=main_cat_code,
                sort_order=1 if main_cat_code == 'PT' else (2 if main_cat_code == 'KS' else 3)
            )

        # 创建细分类目编码
        sub_cat_code = generate_code(cat_name)

        sub_categories.append(SubCategory(
            category_name=main_cat_name,
            sub_category_name=cat_name,
            sub_category_code=sub_cat_code,
            sort_order=sort_order
        ))

    return list(main_categories.values()), sub_categories


def generate_code(name: str) -> str:
    """根据名称生成编码"""
    # 提取中文首字母或英文首字母
    code = ''
    for char in name:
        if '\u4e00' <= char <= '\u9fff':
            # 简单处理：取拼音首字母（这里简化处理，实际可用pypinyin库）
            code += char
        elif char.isalnum():
            code += char.upper()
    # 限制长度并处理特殊字符
    code = re.sub(r'[^\w]', '', code)[:10]
    return code if code else 'OTHER'


def parse_item_data(df: pd.DataFrame) -> List[Item]:
    """解析物品数据"""
    items = []

    for _, row in df.iterrows():
        # 产品编号
        item_code = str(row['产品编号']).strip() if pd.notna(row['产品编号']) else ''

        # 产品名称
        item_name = str(row['产品名称']).strip() if pd.notna(row['产品名称']) else ''

        # 拼音码
        pinyin = str(row['拼音码']).strip() if pd.notna(row['拼音码']) else ''

        # 单位
        unit = str(row['单位']).strip() if pd.notna(row['单位']) else '件'

        # 规格和型号处理
        spec = str(row['规格']).strip() if pd.notna(row['规格']) else ''
        model = ''

        # 品牌
        brand = str(row['品牌']).strip() if pd.notna(row['品牌']) else ''

        # 类别映射
        category = str(row['类别']).strip() if pd.notna(row['类别']) else ''

        # 确定主类目和细分类目
        if category.startswith('普天'):
            category_name = '普天备件'
        elif category.startswith('科盛'):
            category_name = '科盛备件'
        else:
            category_name = '其他备件'

        sub_category_name = category

        # 库存上下限
        stock_lower = int(row['库存下限']) if pd.notna(row['库存下限']) else 0
        stock_upper = int(row['库存上限']) if pd.notna(row['库存上限']) else 0

        # 预设进价
        price = float(row['预设进价']) if pd.notna(row['预设进价']) else 0.0

        # 备注整合
        remarks = []
        if pd.notna(row['货架货位号']):
            remarks.append(f"货位:{row['货架货位号']}")
        for i in range(1, 7):
            col = f'备注{i}'
            if col in row and pd.notna(row[col]):
                remarks.append(str(row[col]))
        remark = '; '.join(remarks) if remarks else ''

        # 停用状态
        disabled = str(row['停用']).strip() if pd.notna(row['停用']) else ''
        status = 0 if disabled in ['是', '1', 'True', 'true'] else 1

        items.append(Item(
            item_code=item_code,
            item_name=item_name,
            pinyin=pinyin,
            model=model,
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


def generate_sql(categories: List[Category], sub_categories: List[SubCategory],
                 items: List[Item]) -> str:
    """生成SQL插入语句"""
    sql_lines = []
    sql_lines.append("-- WMS数据导入SQL")
    sql_lines.append("-- 生成时间: 自动生成")
    sql_lines.append("-- 注意：执行前请确保数据库已备份")
    sql_lines.append("")

    # 主类目映射（名称->ID）
    cat_id_map = {}
    sub_cat_id_map = {}

    # 1. 插入主类目
    sql_lines.append("-- ==================== 主类目 ====================")
    for i, cat in enumerate(categories, 1):
        cat_id = 1000000000000000000 + i  # 雪花ID格式
        cat_id_map[cat.category_name] = cat_id
        sql_lines.append(f"""
INSERT INTO wms_category (id, category_name, category_code, category_color, icon, sort_order, is_consumable, del_flag, create_time, create_by)
VALUES ({cat_id}, '{cat.category_name}', '{cat.category_code}', NULL, NULL, {cat.sort_order}, {cat.is_consumable}, 0, NOW(), 'system');""")

    # 2. 插入细分类目
    sql_lines.append("\n-- ==================== 细分类目 ====================")
    for i, sub in enumerate(sub_categories, 1):
        sub_cat_id = 2000000000000000000 + i
        parent_id = cat_id_map.get(sub.category_name, 0)
        sub_cat_id_map[sub.sub_category_name] = sub_cat_id
        sql_lines.append(f"""
INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES ({sub_cat_id}, {parent_id}, '{sub.sub_category_name}', '{sub.sub_category_code}', {sub.sort_order}, 0, NOW(), 'system');""")

    # 3. 插入物品
    sql_lines.append("\n-- ==================== 物品档案 ====================")
    for i, item in enumerate(items, 1):
        item_id = 3000000000000000000 + i
        category_id = cat_id_map.get(item.category_name, 0)
        sub_category_id = sub_cat_id_map.get(item.sub_category_name, 0)

        # 处理SQL特殊字符
        item_name = item.item_name.replace("'", "''")
        spec = item.spec.replace("'", "''")
        brand = item.brand.replace("'", "''")
        remark = item.remark.replace("'", "''")
        pinyin = item.pinyin.replace("'", "''")

        sql_lines.append(f"""
INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES ({item_id}, '{item.item_code}', '{item_name}', '{pinyin}', '{item.model}', '{spec}', '{item.unit}',
        '{brand}', {category_id}, {sub_category_id}, NULL, {item.status}, {item.is_consumable}, {item.is_returnable},
        {item.purchase_price}, {item.stock_qty}, {item.stock_lower_limit}, {item.stock_upper_limit}, NULL, NULL,
        '{remark}', 0, NOW(), 'system');""")

    return '\n'.join(sql_lines)


def generate_api_json(categories: List[Category], sub_categories: List[SubCategory],
                      items: List[Item]) -> Dict:
    """生成API调用所需的JSON数据"""
    result = {
        "categories": [asdict(c) for c in categories],
        "subCategories": [],
        "items": []
    }

    # 按主类目分组细分类目
    for sub in sub_categories:
        result["subCategories"].append({
            "parentCategoryName": sub.category_name,
            **asdict(sub)
        })

    # 物品数据
    for item in items:
        result["items"].append(asdict(item))

    return result


def main():
    """主函数"""
    print("=" * 60)
    print("WMS系统数据导入工具")
    print("=" * 60)

    import os
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # 1. 读取类别数据
    print("\n[1/4] 读取类别数据...")
    cat_df = pd.read_excel(os.path.join(script_dir, '基本信息.xls'))
    print(f"    读取到 {len(cat_df)} 条类别记录")

    # 2. 解析类别
    print("\n[2/4] 解析类别层级...")
    categories, sub_categories = parse_category_data(cat_df)
    print(f"    生成 {len(categories)} 个主类目")
    print(f"    生成 {len(sub_categories)} 个细分类目")

    for cat in categories:
        subs = [s for s in sub_categories if s.category_name == cat.category_name]
        print(f"    - {cat.category_name}: {len(subs)} 个细分类目")

    # 3. 读取物品数据
    print("\n[3/4] 读取物品数据...")
    item_df = pd.read_excel(os.path.join(script_dir, '产品资料.xls'))
    print(f"    读取到 {len(item_df)} 条物品记录")

    # 4. 解析物品
    print("\n[4/4] 解析物品数据...")
    items = parse_item_data(item_df)

    # 统计
    enabled = sum(1 for i in items if i.status == 1)
    disabled = sum(1 for i in items if i.status == 0)
    print(f"    正常状态: {enabled} 条")
    print(f"    停用状态: {disabled} 条")

    # 5. 生成SQL文件
    print("\n" + "=" * 60)
    print("生成导入文件...")
    print("=" * 60)

    sql_content = generate_sql(categories, sub_categories, items)
    with open('import_data.sql', 'w', encoding='utf-8') as f:
        f.write(sql_content)
    print("    ✓ SQL文件: import_data.sql")

    # 6. 生成JSON文件（用于API调用）
    api_data = generate_api_json(categories, sub_categories, items)
    with open('import_data.json', 'w', encoding='utf-8') as f:
        json.dump(api_data, f, ensure_ascii=False, indent=2)
    print("    ✓ JSON文件: import_data.json")

    # 7. 生成统计报告
    report = f"""
# WMS数据导入报告

## 数据来源
- 类别文件: 基本信息.xls ({len(cat_df)} 条)
- 物品文件: 产品资料.xls ({len(item_df)} 条)

## 生成结果

### 主类目 ({len(categories)} 个)
"""
    for cat in categories:
        subs = [s for s in sub_categories if s.category_name == cat.category_name]
        report += f"- {cat.category_name} ({cat.category_code}): {len(subs)} 个细分类目\n"

    report += f"""
### 细分类目 ({len(sub_categories)} 个)
"""
    for sub in sub_categories:
        report += f"- {sub.category_name} -> {sub.sub_category_name}\n"

    report += f"""
### 物品档案 ({len(items)} 条)
- 正常状态: {enabled} 条
- 停用状态: {disabled} 条

## 使用说明

### 方式一：直接执行SQL
```bash
mysql -u username -p database_name < import_data.sql
```

### 方式二：通过API导入
使用 import_data.json 中的数据，调用系统API：
1. POST /api/categories - 创建主类目
2. POST /api/categories/{{id}}/sub-categories - 创建细分类目
3. POST /api/items - 创建物品

## 注意事项
1. 执行前请备份数据库
2. 确保类别数据先导入，再导入物品
3. 物品编号使用客户原有编号
4. 停用物品状态设为0（禁用）
"""

    with open('import_report.md', 'w', encoding='utf-8') as f:
        f.write(report)
    print("    ✓ 报告文件: import_report.md")

    print("\n" + "=" * 60)
    print("数据导入文件生成完成！")
    print("=" * 60)
    print("\n生成的文件:")
    print("  1. import_data.sql  - SQL插入语句")
    print("  2. import_data.json - API调用数据")
    print("  3. import_report.md - 导入报告")


if __name__ == '__main__':
    main()
