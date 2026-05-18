# Git 工作流规范

> 本规范适用于 3 人小团队协作开发，基于简化版 Git Flow

---

## 一、分支结构

```
main (生产分支) ───────●─────────────────●─────────────
                      ↑                 ↑
                     PR                PR
                      ↑                 ↑
develop (开发分支) ───┼───●───●───●─────┼───●───●──────
                      ↑   ↑   ↑         ↑   ↑
                     PR  PR  PR        PR  PR
                      ↑   ↑   ↑         ↑   ↑
feature/xxx (功能分支) ●───●───●   ●─────●───●   ●──────
                     A   A   A   B         C   C

A=开发者A的功能分支  B=开发者B的功能分支  C=开发者C的功能分支
```

### 分支职责

| 分支 | 用途 | 保护级别 |
|------|------|----------|
| `main` | 生产环境代码，随时可发布 | 🔒 禁止直接推送 |
| `develop` | 日常开发集成，测试环境部署 | 🔒 禁止直接推送 |
| `feature/xxx` | 个人功能开发，可自由推送 | ✅ 可自由推送 |

---

## 二、日常开发流程

### 1. 开始新功能

```bash
# 1. 切换到开发分支并更新
git checkout develop
git pull origin develop

# 2. 创建自己的功能分支（命名规范：feature/功能描述-姓名缩写）
git checkout -b feature/login-zhangsan

# 3. 开发过程中多次提交
git add .
git commit -m "feat: 完成登录页面UI"
git push origin feature/login-zhangsan
```

### 2. 功能完成后合并到开发分支

**方式一：Pull Request（推荐）**

1. 在 GitHub 网页上发起 Pull Request
2. 选择 `feature/xxx` → `develop`
3. 填写 PR 标题和描述
4. 请求团队成员审查
5. 审查通过后合并

**方式二：命令行（仅限无冲突情况）**

```bash
git checkout develop
git pull origin develop
git merge feature/login-zhangsan
git push origin develop
```

---

## 三、冲突处理

当提示冲突时，按以下步骤解决：

```bash
# 1. 先保存当前工作
git stash

# 2. 更新开发分支
git checkout develop
git pull origin develop

# 3. 回到功能分支，合并开发分支
git checkout feature/login-zhangsan
git merge develop

# 4. 手动解决冲突文件（编辑文件，删除冲突标记）
# <<<<<<< HEAD
# 你的代码
# =======
# 别人的代码
# >>>>>>> develop

# 5. 标记冲突已解决
git add .
git commit -m "merge: 解决与develop分支的冲突"

# 6. 恢复之前的工作
git stash pop
```

---

## 四、发布流程

```bash
# 1. 开发分支测试通过后，合并到main
git checkout main
git pull origin main
git merge develop
git tag -a v1.0.0 -m "版本1.0.0发布"
git push origin main --tags

# 2. 线上紧急修复（hotfix）
git checkout main
git checkout -b hotfix/fix-bug-001
# ...修复代码...
git checkout main
git merge hotfix/fix-bug-001
git checkout develop
git merge hotfix/fix-bug-001  # 同步修复到开发分支
```

---

## 五、命名规范

### 分支命名

| 类型 | 示例 | 说明 |
|------|------|------|
| 功能分支 | `feature/login-zhangsan` | feature/功能-姓名缩写 |
| 修复分支 | `bugfix/login-error-lisi` | bugfix/问题-姓名缩写 |
| 紧急修复 | `hotfix/db-connection` | hotfix/问题描述 |

### 提交信息规范

格式：`类型: 描述`

| 类型 | 用途 | 示例 |
|------|------|------|
| `feat:` | 新功能 | `feat: 新增用户登录功能` |
| `fix:` | 修复bug | `fix: 修复登录验证失败问题` |
| `docs:` | 文档更新 | `docs: 更新API文档` |
| `style:` | 代码格式调整 | `style: 格式化代码` |
| `refactor:` | 重构 | `refactor: 优化登录逻辑` |
| `test:` | 测试相关 | `test: 添加登录单元测试` |
| `chore:` | 构建/工具 | `chore: 更新依赖版本` |
| `merge:` | 合并冲突 | `merge: 解决与develop的冲突` |

---

## 六、GitHub 分支保护配置

### 设置步骤

1. 进入 GitHub 仓库 → Settings → Branches
2. 点击 "Add rule" 添加保护规则

### main 分支保护规则

| 设置项 | 配置 |
|--------|------|
| Branch name pattern | `main` |
| Require a pull request before merging | ✅ 勾选 |
| Require approvals | 1 |
| Dismiss stale PR approvals | ✅ 勾选 |
| Require status checks to pass | ✅ 勾选（如有CI） |
| Include administrators | ✅ 勾选 |

### develop 分支保护规则

| 设置项 | 配置 |
|--------|------|
| Branch name pattern | `develop` |
| Require a pull request before merging | ✅ 勾选 |
| Require approvals | 1（可选，团队决定） |
| Dismiss stale PR approvals | ✅ 勾选 |
| Require status checks to pass | ✅ 勾选（如有CI） |

---

## 七、团队协作 Checklist

```
□ 每天开始工作前：git pull origin develop
□ 创建功能分支前：确保基于最新的develop
□ 开发过程中：经常commit，及时push
□ 功能完成后：先pull最新develop，解决冲突后再合并
□ 合并前：自测通过，代码整洁
□ 合并后：删除已合并的功能分支
□ 发现冲突：及时沟通，不要强行覆盖他人代码
```

---

## 八、快速参考卡片

```bash
# 每天开始
git checkout develop && git pull origin develop

# 开始新功能
git checkout -b feature/xxx develop

# 提交代码
git add . && git commit -m "feat: xxx" && git push origin feature/xxx

# 合并到develop（先更新）
git checkout develop && git pull origin develop
git merge feature/xxx && git push origin develop

# 删除本地分支
git branch -d feature/xxx

# 查看分支状态
git branch -a

# 保存临时工作
git stash

# 恢复临时工作
git stash pop
```

---

## 九、常见问题

### Q1: 什么时候应该创建功能分支？
**A:** 开始任何新功能开发前都应该创建功能分支，即使是小改动。

### Q2: 功能分支应该存在多久？
**A:** 建议 1-3 天内完成并合并，避免长期存在的功能分支。

### Q3: 合并后发现有问题怎么办？
**A:** 
- 如果还没push：使用 `git reset --hard HEAD~1` 回退
- 如果已push：创建新的修复分支 `bugfix/xxx` 进行修复

### Q4: 多人同时修改同一文件怎么办？
**A:** 
1. 先沟通确认分工
2. 后合并的人负责解决冲突
3. 冲突解决后通知相关人员

### Q5: 可以强制推送吗？
**A:** 功能分支可以 `git push --force-with-lease`，但 `main` 和 `develop` 严禁强制推送。

---

## 十、团队约定

1. **每天开始工作前**先 `git pull origin develop` 更新代码
2. **完成功能后**及时发起 PR，不要囤积代码
3. **审查 PR 时**认真负责，发现问题及时指出
4. **发生冲突时**主动沟通，不要擅自覆盖他人代码
5. **代码合并后**及时删除功能分支，保持仓库整洁

---

*最后更新：2026-05-18*
