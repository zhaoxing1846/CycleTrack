# CycleTrack - 经期追踪应用

一款简洁优雅的女性经期追踪 Android 应用。

![版本](https://img.shields.io/badge/版本-v9_FINAL-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple)
![Compose](https://img.shields.io/badge/Compose-1.5.0-green)

---

## ✨ 功能特性

### 📅 经期管理
- **经期开始/结束标记** - 红色/浅红色区分
- **自动天数计算** - 根据标记自动计算经期长度
- **智能预测** - 基于历史数据预测下次经期（提前 30 天）

### 💚 亲密记录
- **浅绿色标记** - 与经期颜色区分
- **多维度统计** - 近 30 天/本月/上月次数

### 📊 统计图表
- **6 个月趋势图** - 经期天数和亲密次数变化
- **简洁可视化** - 折线图直观展示趋势

---

## 🎨 界面预览

### 颜色方案
| 类型 | 颜色 | 图标 |
|------|------|------|
| 经期开始 | 🔴 `#E53935` 红色 | ❤️ |
| 经期结束 | 🟠 `#FF7043` 浅红 | ✓ |
| 亲密 | 💚 `#66BB6A` 浅绿 | 🌿 |
| 预测 | 🌸 `#F48FB1` 粉色 | 📅 |

---

## 🛠️ 技术栈

- **语言：** Kotlin 2.0.0
- **UI 框架：** Jetpack Compose
- **设计系统：** Material Design 3
- **数据库：** Room
- **架构：** MVVM
- **协程：** `Dispatchers.IO` 用于数据库操作

---

## 📦 构建说明

### 环境要求
- Android Studio Hedgehog 或更高版本
- JDK 17+
- Android SDK 35
- 最低 SDK：26 (Android 8.0)

### 编译步骤

1. **克隆/下载项目**
   ```bash
   git clone <repository-url>
   cd CycleTrack
   ```

2. **清理缓存**（如果是覆盖旧版本）
   ```bash
   rm -rf .gradle/ build/ app/build/
   ```

3. **同步并构建**
   - Android Studio: `File → Open → Sync → Rebuild → Run`

---

## 📱 使用方法

### 标记经期
1. 点击日历上的日期（选中蓝色圆圈）
2. 点击底部"经期开始"按钮（红色）
3. 经期结束时点击"经期结束"按钮（浅红色）
4. 系统自动计算经期天数

### 标记亲密
1. 选择日期
2. 点击底部"亲密"按钮（浅绿色）
3. 再次点击取消标记

### 查看统计
1. 点击右上角 📊 图标
2. 查看近 6 个月趋势图

---

## 🔍 项目结构

```
CycleTrack/
├── app/src/main/kotlin/com/cycletrack/
│   ├── data/
│   │   ├── CycleDao.kt          # 数据访问对象
│   │   ├── CycleDatabase.kt     # Room 数据库
│   │   └── CycleRepository.kt   # 数据仓库
│   ├── model/
│   │   └── CycleRecord.kt       # 数据模型 + 枚举
│   └── ui/
│       ├── MainActivity.kt
│       ├── CalendarView.kt      # 日历视图
│       ├── HomeScreen.kt        # 主屏幕
│       ├── StatsPanel.kt        # 统计面板
│       └── StatsScreen.kt       # 6 个月趋势图
├── app/src/main/res/
│   ├── values/colors.xml
│   └── drawable/
├── build.gradle.kts
└── app/build.gradle.kts
```

---

## 📝 开发笔记

### 关键实现

1. **线程切换** - Room 操作必须在 `Dispatchers.IO`
   ```kotlin
   suspend fun toggleRecord(...) {
       withContext(Dispatchers.IO) {
           // 数据库操作
       }
   }
   ```

2. **预测逻辑** - 经期结束后开始预测
   ```kotlin
   if (!hasOngoingPeriod) {
       nextPeriodPredicted = lastPeriodStart + (cycleLength * 24h)
       isPredicting = true
   }
   ```

3. **上月统计** - 使用自然月而非滚动窗口
   ```kotlin
   intimacyLastMonth = records.count {
       it.date >= lastMonthStart && it.date < thisMonthStart
   }
   ```

---

## 🐛 已知问题

目前没有已知问题。如有 bug 请提交 Issue。

---

## 📄 许可证

本项目仅供学习和个人使用。

---

## 👨‍💻 开发者

**赵星 (Zhao Xing)**

---

**最后更新：** 2026-04-07  
**版本：** v9_FINAL
