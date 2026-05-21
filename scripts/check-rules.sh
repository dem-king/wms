#!/bin/bash
# =============================================================================
# Harness 自动化规则检查脚本
# 将 AGENTS.md 中的人工 PR Review 检查项迁移为可程序化验证的阻断/警告
#
# 用法:
#   ./scripts/check-rules.sh          # 默认：阻断级违规 → exit 1
#   ./scripts/check-rules.sh --strict # 警告也视为失败
#   ./scripts/check-rules.sh --list   # 列出所有规则
#
# 返回码:
#   0 = 全部通过（或无阻断级违规）
#   1 = 存在阻断级违规
# =============================================================================

set -o pipefail

# ── 颜色 ──────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# ── 统计 ──────────────────────────────────────────
PASS=0
FAIL=0
WARN=0
VIOLATIONS=""
WARNINGS=""

SERVER_DIR="wms-server"
STRICT=false

# ── 工具函数 ──────────────────────────────────────
pass_() { printf "${GREEN}[PASS]${NC} %s\n" "$1"; ((PASS++)); }
fail_() { printf "${RED}[FAIL]${NC} %-8s %s\n" "$1" "$2"; VIOLATIONS+="  $1 $2"$'\n'; ((FAIL++)); }
warn_() { printf "${YELLOW}[WARN]${NC} %-8s %s\n" "$1" "$2"; WARNINGS+="  $1 $2"$'\n'; ((WARN++)); }
info_() { printf "${CYAN}[INFO]${NC} %s\n" "$1"; }

# 检查文件中的多行模式：在指定行前后 N 行内是否包含某模式
# 参数: file line_num context_lines pattern
has_around_line() {
  local file="$1" line="$2" ctx="$3" pattern="$4"
  local start=$((line - ctx))
  [ $start -lt 1 ] && start=1
  local end=$((line + ctx))
  sed -n "${start},${end}p" "$file" | grep -q "$pattern"
}

# 检查文件中指定行之前 N 行内是否包含某模式
has_before_line() {
  local file="$1" line="$2" ctx="$3" pattern="$4"
  local start=$((line - ctx))
  [ $start -lt 1 ] && start=1
  sed -n "${start},${line}p" "$file" | grep -q "$pattern"
}

# 扫描目录下所有 Java 源文件（排除 target）
find_java() {
  find "$SERVER_DIR" -name "*.java" -not -path "*/target/*" "$@"
}

# ── 参数解析 ──────────────────────────────────────
case "${1:-}" in
  --strict) STRICT=true ;;
  --list)
    echo "规则列表:"
    grep -E '^\| [A-Z]+-[0-9]+' "$(dirname "$0")/../.harness/rules/编码约束.md" 2>/dev/null || true
    exit 0
    ;;
esac

echo ""
echo "============================================================"
echo "  Harness Rule Checker v1.0"
echo "  AGENTS.md → CI 自动化迁移"
echo "============================================================"
echo ""

# ============================================================================
# 一、安全检查规则（阻断级）
# ============================================================================
echo "${BOLD}── 安全检查规则 ──${NC}"

# SEC-01: 所有 @XxxMapping 方法必须有 @PreAuthorize
check_SEC01() {
  local ok=true
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local methods=$(grep -n '@\(GetMapping\|PostMapping\|PutMapping\|DeleteMapping\|PatchMapping\)' "$file" 2>/dev/null || true)
    [ -z "$methods" ] && continue
    while IFS=: read -r line_num rest; do
      [ -z "$line_num" ] && continue
      if ! has_around_line "$file" "$line_num" 5 '@PreAuthorize'; then
        local rel="${file#$SERVER_DIR/}"
        fail_ "SEC-01" "$rel:$line_num: @XxxMapping 方法缺少 @PreAuthorize"
        ok=false
      fi
    done <<< "$methods"
  done < <(find_java -path "*/controller/*.java")
  $ok && pass_ "SEC-01: 所有 Controller 方法有 @PreAuthorize"
}

# SEC-02: 查询方法必须有 @DataScope
check_SEC02() {
  local ok=true
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    for method_name in page list getById search listAll; do
      local methods=$(grep -n "public.*$method_name\s*(" "$file" 2>/dev/null || true)
      [ -z "$methods" ] && continue
      while IFS=: read -r line_num rest; do
        [ -z "$line_num" ] && continue
        if ! has_around_line "$file" "$line_num" 10 '@DataScope'; then
          local rel="${file#$SERVER_DIR/}"
          fail_ "SEC-02" "$rel:$line_num: 查询方法 '$method_name' 缺少 @DataScope"
          ok=false
        fi
      done <<< "$methods"
    done
  done < <(find_java -path "*/controller/*.java")
  $ok && pass_ "SEC-02: 所有查询方法有 @DataScope"
}

# SEC-03: 写操作必须有 @OperLog
check_SEC03() {
  local ok=true
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local methods=$(grep -n '@\(PostMapping\|PutMapping\|DeleteMapping\|PatchMapping\)' "$file" 2>/dev/null || true)
    [ -z "$methods" ] && continue
    while IFS=: read -r line_num rest; do
      [ -z "$line_num" ] && continue
      if ! has_around_line "$file" "$line_num" 10 '@OperLog'; then
        local rel="${file#$SERVER_DIR/}"
        fail_ "SEC-03" "$rel:$line_num: 写操作方法缺少 @OperLog"
        ok=false
      fi
    done <<< "$methods"
  done < <(find_java -path "*/controller/*.java")
  $ok && pass_ "SEC-03: 所有写操作方法有 @OperLog"
}

# SEC-04: 配置文件无硬编码密码/密钥
check_SEC04() {
  local ok=true
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local matches=$(grep -nE '(password|secret|jwt-secret|access-key|secret-key)\s*:' "$file" 2>/dev/null | grep -v '\${' | grep -v '^\s*#' | grep -v 'example\|demo\|test' || true)
    if [ -n "$matches" ]; then
      while IFS= read -r match; do
        [ -z "$match" ] && continue
        # 跳过值为空的行
        local val=$(echo "$match" | sed 's/.*:\s*//')
        [ -z "$val" ] && continue
        local rel="${file#$SERVER_DIR/}"
        fail_ "SEC-04" "$rel: 疑似硬编码敏感信息 → $(echo "$match" | cut -d: -f1-2)"
        ok=false
      done <<< "$matches"
    fi
  done < <(find "$SERVER_DIR" \( -name "application*.yml" -o -name "application*.yaml" -o -name "application*.properties" \) -not -path "*/target/*" 2>/dev/null)
  $ok && pass_ "SEC-04: 配置文件无硬编码敏感信息"
}

check_SEC01
check_SEC02
check_SEC03
check_SEC04

# ============================================================================
# 二、数据操作规则（阻断级）
# ============================================================================
echo ""
echo "${BOLD}── 数据操作规则 ──${NC}"

# DB-01: 禁止物理删除
check_DB01() {
  local ok=true
  local matches=$(grep -rn 'mapper\.delete\|mapper\.deleteById\|mapper\.deleteBatchIds' $(find_java -not -path "*/test/*") 2>/dev/null || true)
  if [ -n "$matches" ]; then
    while IFS= read -r match; do
      [ -z "$match" ] && continue
      local rel=$(echo "$match" | sed "s|$SERVER_DIR/||")
      fail_ "DB-01" "$rel: 禁止物理删除，请使用逻辑删除"
      ok=false
    done <<< "$matches"
  fi
  $ok && pass_ "DB-01: 无物理删除调用"
}

# DB-02: 禁止手动拼接 del_flag 条件
check_DB02() {
  local ok=true
  local matches=$(grep -rn '\.eq(.*[Gg]et[Dd]el[Ff]lag\b\|\.eq("del_flag"' $(find_java -not -path "*/test/*") 2>/dev/null || true)
  if [ -n "$matches" ]; then
    while IFS= read -r match; do
      [ -z "$match" ] && continue
      local rel=$(echo "$match" | sed "s|$SERVER_DIR/||")
      fail_ "DB-02" "$rel: 禁止手动拼接 del_flag 条件（MyBatis-Plus 自动处理）"
      ok=false
    done <<< "$matches"
  fi
  $ok && pass_ "DB-02: 无手动 del_flag 条件"
}

# DB-03: 逻辑删除必须使用 DelFlagConstants.DELETED
check_DB03() {
  local ok=true
  local matches=$(grep -rn 'setDelFlag\s*(\s*[0-9]\s*)\|\.delFlag\s*=\s*[0-9]' $(find_java -not -path "*/test/*") 2>/dev/null | grep -v 'DelFlagConstants\|NORMAL\|DELETED' || true)
  if [ -n "$matches" ]; then
    while IFS= read -r match; do
      [ -z "$match" ] && continue
      local rel=$(echo "$match" | sed "s|$SERVER_DIR/||")
      fail_ "DB-03" "$rel: 逻辑删除必须使用 DelFlagConstants 常量"
      ok=false
    done <<< "$matches"
  fi
  $ok && pass_ "DB-03: 逻辑删除使用 DelFlagConstants"
}

# DB-04: 主键必须使用雪花 ID
check_DB04() {
  local ok=true
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    if grep -q '@TableId.*AUTO' "$file" 2>/dev/null; then
      local rel="${file#$SERVER_DIR/}"
      fail_ "DB-04" "$rel: 主键必须使用 IdType.ASSIGN_ID（雪花ID）"
      ok=false
    fi
  done < <(find_java -path "*/entity/*.java")
  $ok && pass_ "DB-04: 所有 Entity 主键使用雪花 ID"
}

# DB-06: 禁止循环内调用 mapper.insert
check_DB06() {
  local ok=true
  local warn_count=0
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local matches=$(grep -n 'mapper\.insert\|\.insert(' "$file" 2>/dev/null | grep -v '//\|saveBatch\|batchInsert\|test' || true)
    if [ -n "$matches" ]; then
      while IFS=: read -r line_num rest; do
        [ -z "$line_num" ] && continue
        local ctx_start=$((line_num - 8))
        [ $ctx_start -lt 1 ] && ctx_start=1
        if sed -n "${ctx_start},${line_num}p" "$file" | grep -q '\bfor\b'; then
          local rel="${file#$SERVER_DIR/}"
          fail_ "DB-06" "$rel:$line_num: 循环内发现 mapper.insert，请使用 saveBatch"
          ok=false
        fi
      done <<< "$matches"
    fi
  done < <(find_java -path "*/service/impl/*.java")
  $ok && pass_ "DB-06: 无循环内 mapper.insert"
}

check_DB01
check_DB02
check_DB03
check_DB04
check_DB06

# ============================================================================
# 三、编号生成规则（阻断级）
# ============================================================================
echo ""
echo "${BOLD}── 编号生成规则 ──${NC}"

check_SEQ01() {
  local ok=true
  local matches=$(grep -rln 'likeRight.*orderByDesc\|likeRight' $(find_java -path "*/service/impl/*.java") 2>/dev/null || true)
  if [ -n "$matches" ]; then
    while IFS= read -r file; do
      [ -z "$file" ] && continue
      if grep -q 'LIMIT 1\|last.*LIMIT' "$file" 2>/dev/null; then
        local rel="${file#$SERVER_DIR/}"
        fail_ "SEQ-01" "$rel: 疑似"查询最大编号+1"模式，请使用 SequenceGenerator"
        ok=false
      fi
    done <<< "$matches"
  fi
  $ok && pass_ "SEQ-01: 无 '查询最大编号+1' 模式"
}

check_SEQ01

# ============================================================================
# 四、常量与分层规则（阻断级）
# ============================================================================
echo ""
echo "${BOLD}── 常量与分层规则 ──${NC}"

# CONST-02: ServiceImpl 中禁止私有常量
check_CONST02() {
  local ok=true
  local matches=$(grep -rn 'private static final' $(find_java -path "*/service/impl/*.java") 2>/dev/null | grep -v 'SerialVersionUID\|log\b\|logger\b' || true)
  if [ -n "$matches" ]; then
    while IFS= read -r match; do
      [ -z "$match" ] && continue
      local rel=$(echo "$match" | sed "s|$SERVER_DIR/||")
      fail_ "CONST-02" "$rel: ServiceImpl 禁止定义私有常量，请提取到常量类"
      ok=false
    done <<< "$matches"
  fi
  $ok && pass_ "CONST-02: ServiceImpl 无私有常量"
}

# ARCH-03: ServiceImpl 禁止 private toVo 方法
check_ARCH03() {
  local ok=true
  local matches=$(grep -rn 'private.*toVo\|private.*toVoList\|private.*convertTo' $(find_java -path "*/service/impl/*.java") 2>/dev/null || true)
  if [ -n "$matches" ]; then
    while IFS= read -r match; do
      [ -z "$match" ] && continue
      local rel=$(echo "$match" | sed "s|$SERVER_DIR/||")
      fail_ "ARCH-03" "$rel: ServiceImpl 禁止 private toVo 方法，请使用独立 Converter"
      ok=false
    done <<< "$matches"
  fi
  $ok && pass_ "ARCH-03: ServiceImpl 无 private toVo 转换方法"
}

# ARCH-02: Service/Controller 返回类型必须是 Vo
check_ARCH02() {
  local ok=true
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local matches=$(grep -n 'public.*[A-Z].*Entity\b\|public.*[A-Z].*Wms[A-Z]' "$file" 2>/dev/null | grep -v 'class\|extends\|PageParam\|PageResult\|R<\s*$' || true)
    if [ -n "$matches" ]; then
      while IFS=: read -r line_num rest; do
        [ -z "$line_num" ] && continue
        if echo "$rest" | grep -qE 'Wms[A-Z]\w+Entity|Wms[A-Z]\w+\b(?!Vo|Dto|Converter)' && echo "$rest" | grep -qE '(WmsInboundOrder|WmsOutboundOrder|WmsWarehouse|WmsItem|WmsBin|WmsCabinet|WmsArea|WmsCategory|WmsSubCategory|WmsTag|WmsStock|WmsElectronicLabel|SysUser|SysRole|SysMenu|SysConfig|SysDepartment|SysPermission|SysSupplier)\b' && echo "$rest" | grep -v 'Vo\|Dto'; then
          local rel="${file#$SERVER_DIR/}"
          fail_ "ARCH-02" "$rel:$line_num: 公共方法返回 Entity 而非 Vo"
          ok=false
        fi
      done <<< "$matches"
    fi
  done < <(find_java \( -path "*/service/*.java" -o -path "*/controller/*.java" \) -not -name "*Mapper.java")
  $ok && pass_ "ARCH-02: 公共方法返回 Vo 而非 Entity"
}

check_CONST02
check_ARCH03
check_ARCH02

# ============================================================================
# 五、命名规范（阻断级）
# ============================================================================
echo ""
echo "${BOLD}── 命名规范 ──${NC}"

check_NAME03() {
  local ok=true
  # VO 检查
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local basename=$(basename "$file")
    if ! echo "$basename" | grep -q 'Vo\.java$'; then
      local rel="${file#$SERVER_DIR/}"
      fail_ "NAME-03" "$rel: VO 类名必须以 Vo 结尾"
      ok=false
    fi
  done < <(find_java -path "*/domain/vo/*.java")
  # DTO 检查
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local basename=$(basename "$file")
    if ! echo "$basename" | grep -q 'Dto\.java$'; then
      local rel="${file#$SERVER_DIR/}"
      warn_ "NAME-04" "$rel: DTO 类名必须以 Dto 结尾"
    fi
  done < <(find_java -path "*/domain/dto/*.java")
  # Converter 检查
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local basename=$(basename "$file")
    if ! echo "$basename" | grep -q 'Converter\.java$'; then
      local rel="${file#$SERVER_DIR/}"
      warn_ "NAME-05" "$rel: Converter 类名必须以 Converter 结尾"
    fi
  done < <(find_java -path "*/converter/*.java")
  $ok && pass_ "NAME-03/04/05: 命名后缀符合规范"
}

check_NAME03

# ============================================================================
# 六、JavaDoc 规范（阻断级）
# ============================================================================
echo ""
echo "${BOLD}── JavaDoc 规范 ──${NC}"

# JAVADOC-01: Entity 每个字段必须有 @Schema 注解
check_JAVADOC01() {
  local ok=true
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local fields=$(grep -n 'private ' "$file" 2>/dev/null | grep -v 'static\|final\|SerialVersionUID\|serialVersionUID' || true)
    [ -z "$fields" ] && continue
    while IFS=: read -r line_num rest; do
      [ -z "$line_num" ] && continue
      if ! has_before_line "$file" "$line_num" 3 '@Schema\|/\*\*'; then
        local rel="${file#$SERVER_DIR/}"
        fail_ "JDOC-01" "$rel:$line_num: Entity 字段缺少 @Schema 注解"
        ok=false
      fi
    done <<< "$fields"
  done < <(find_java -path "*/entity/*.java")
  $ok && pass_ "JAVADOC-01: 所有 Entity 字段有 @Schema"
}

# JAVADOC-02: Service/Controller 类必须有类级 JavaDoc
check_JAVADOC02() {
  local ok=true
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local class_line=$(grep -n 'public class\|public interface' "$file" 2>/dev/null | head -1 || true)
    [ -z "$class_line" ] && continue
    local line_num=$(echo "$class_line" | cut -d: -f1)
    if ! has_before_line "$file" "$line_num" 10 '/\*\*'; then
      local rel="${file#$SERVER_DIR/}"
      fail_ "JDOC-02" "$rel:$line_num: 类缺少 JavaDoc /** */"
      ok=false
    fi
  done < <(find_java \( -path "*/service/impl/*.java" -o -path "*/controller/*.java" -o -path "*/service/*.java" \) -not -name "*Test*.java" -not -path "*/test/*")
  $ok && pass_ "JAVADOC-02: Service/Controller 有类级 JavaDoc"
}

check_JAVADOC01
check_JAVADOC02

# ============================================================================
# 七、警告级（默认不阻断，--strict 模式下阻断）
# ============================================================================
echo ""
echo "${BOLD}── 警告级检查 ──${NC}"

# CONST-01: 魔法数字扫描
check_CONST01() {
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local matches=$(grep -n '\.setStatus\s*(\s*[0-9]\s*)\|\.setDelFlag\s*(\s*[0-9]\s*)\|=\s*[0-9]\s*;' "$file" 2>/dev/null | grep -v 'DelFlagConstants\|OrderStatusEnum\|BizConstants\|ItemConstants\|WarehouseConstants\|//\|id\|serial\|0;\s*$\|sort\|page\|rows\|pageSize\|pageNum\|page\|limit\|count$\|size\|length\|index\|idx\|i\|j\|k' || true)
    if [ -n "$matches" ]; then
      while IFS= read -r match; do
        [ -z "$match" ] && continue
        local rel="${file#$SERVER_DIR/}"
        warn_ "CONST-01" "$rel:$(echo "$match" | cut -d: -f1): 疑似魔法数字"
      done <<< "$matches"
    fi
  done < <(find_java -path "*/service/impl/*.java")
}

# PERF-01: N+1 查询模式扫描
check_PERF01() {
  while IFS= read -r file; do
    [ -z "$file" ] && continue
    local selects=$(grep -n 'mapper\.selectById\|mapper\.selectOne\|mapper\.selectList' "$file" 2>/dev/null || true)
    if [ -n "$selects" ]; then
      while IFS=: read -r line_num rest; do
        [ -z "$line_num" ] && continue
        local ctx_start=$((line_num - 15))
        [ $ctx_start -lt 1 ] && ctx_start=1
        if sed -n "${ctx_start},${line_num}p" "$file" | grep -qE '\bfor\s*\(.*:' ; then
          local rel="${file#$SERVER_DIR/}"
          warn_ "PERF-01" "$rel:$line_num: 疑似 N+1 查询（循环内数据库调用）"
        fi
      done <<< "$selects"
    fi
  done < <(find_java -path "*/service/impl/*.java")
}

check_CONST01
check_PERF01

# ============================================================================
# 汇总
# ============================================================================
echo ""
echo "============================================================"
printf "  结果: ${GREEN}%d PASS${NC}  ${RED}%d FAIL${NC}  ${YELLOW}%d WARN${NC}\n" $PASS $FAIL $WARN
echo "============================================================"

# 输出违规详情
if [ $FAIL -gt 0 ]; then
  echo ""
  echo "${RED}${BOLD}── 阻断级违规详情 ──${NC}"
  echo "$VIOLATIONS"
fi

if [ $WARN -gt 0 ]; then
  echo ""
  echo "${YELLOW}${BOLD}── 警告级详情 ──${NC}"
  echo "$WARNINGS"
fi

# 决定退出码
if [ $FAIL -gt 0 ]; then
  echo ""
  echo "${RED}${BOLD}[BLOCKED] 存在 $FAIL 条阻断级违规，CI 检查未通过${NC}"
  exit 1
fi

if $STRICT && [ $WARN -gt 0 ]; then
  echo ""
  echo "${RED}${BOLD}[BLOCKED] --strict 模式：$WARN 条警告也视为阻断${NC}"
  exit 1
fi

echo ""
echo "${GREEN}${BOLD}[PASSED] 所有阻断级规则检查通过 ✓${NC}"
echo "  AGENTS.md PR 自查清单 → CI 自动化迁移完成"
exit 0