# UEH Room Booking Management System
## Hệ thống Quản lý Phòng học

A simple Java Swing application to manage room bookings - send forms and display received requests.

### Features
✅ Submit room booking forms  
✅ View all submitted bookings in a table  
✅ Form validation (email, date, required fields)  
✅ Clean, modern UI with Vietnamese support

---

## Prerequisites

### Required
- **Java JDK 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Git** - [Download](https://git-scm.com/)

### Verify Installation
```powershell
java -version
javac -version
git --version
```

---

## Setup

### Option 1: Clone from GitHub (Recommended)
```powershell
git clone https://github.com/holisurt/ueh.git
cd ueh
```

### Option 2: Download ZIP
1. Go to https://github.com/holisurt/ueh
2. Click **Code** → **Download ZIP**
3. Extract and navigate to the folder

---

## Running the Application

### Windows Users - Click & Run

**Option A: Double-click `run.bat`**
- Automatically compiles and runs the application
- Simplest method!

**Option B: PowerShell Script**
```powershell
.\run.ps1
```

### Manual Compilation & Run (Any OS)

```powershell
# Compile
javac Main.java

# Run
java -cp . Main
```

---

## Using the Application

### 1. Fill in the Form
- **Họ và tên** (Name) - Required
- **Email** - Must be @ueh.edu.vn format
- **Điện thoại** (Phone) - Required
- **Đơn vị** (Department) - Required
- **Ngày sử dụng** (Date) - YYYY-MM-DD format, must be 3+ days from today
- **Ca sử dụng** (Time Slot) - Morning/Afternoon/Evening
- **Loại phòng** (Room Type) - Lecture/Computer Lab
- **Phòng** (Room) - Select from available rooms
- **Nội dung lớp học** (Course) - Required
- **Lý do mượn** (Reason) - Required

### 2. Submit
- Click **Gửi Đăng ký** (Submit Registration)
- Form validates automatically
- Success message appears

### 3. View Bookings
- Submitted forms appear in the table below
- Shows: ID, Name, Email, Room, Date, Time, Course, Submit Date, Status

---

## Project Structure
```
code-quan-ly-phong-hoc/
├── Main.java              # Main application code
├── run.bat               # Windows batch script
├── run.ps1               # PowerShell script
├── README.md             # This file
├── .gitignore            # Git ignore rules
└── Main.class            # Compiled class (generated)
```

---

## Collaboration with Git

### Clone & Pull Updates
```powershell
git clone https://github.com/holisurt/ueh.git
cd ueh
git pull origin main
```

### Make Changes & Push
```powershell
# Make changes to Main.java

# Commit changes
git add Main.java
git commit -m "Describe your changes here"

# Push to remote
git push origin main
```

### Pull Latest Changes
```powershell
git pull origin main
```

---

## Troubleshooting

### "Command not found: java"
- Java is not installed or not in PATH
- Install [JDK 26](https://www.oracle.com/java/technologies/downloads/)
- Restart terminal after installation

### "Cannot find symbol" compilation error
- Check imports in Main.java
- Ensure Java version is 17+
- Delete `.class` files and recompile

### Application won't start
- Check Java is properly installed
- Verify you're in the correct directory
- Try: `java -cp . Main`

---

## Contributors
- **hoang1015** (Original author)
- **holisurt** (Collaborator)

---

## Technologies
- **Language**: Java
- **UI Framework**: Swing
- **Build**: Javac compiler
- **Version Control**: Git/GitHub

---

## License
Educational project for ET4430E course

**Questions?** Contact the team via GitHub Issues
