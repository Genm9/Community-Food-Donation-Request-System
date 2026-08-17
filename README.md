# Community Food Donation and Request System
## JavaFX Dashboard, Storage and Authentication Module

This JavaFX project implements the **Community Food Donation and Request System**. It allows users to register food donations, submit food assistance requests, and view donation and request records. It also includes an optional administrator screen for reviewing requests and updating their status.

The project includes English **Login** and **Register** screens. Registered accounts are stored locally in `data/accounts.txt`. Every Java source file is placed directly inside the `src/` folder, and the source files use Java's default package without package declarations.

The source has been compiled successfully with **JDK 21** and **JavaFX Controls**. The storage workflow supports saving records, loading records, updating request status, deleting records, calculating totals, registering accounts, and authenticating users.

## Flat Source Structure

Although the files are logically grouped by responsibility, there are no Java package folders. This keeps the source structure simple for a beginner JavaFX project.

| Logical responsibility | Java files directly under `src/` | Purpose |
|---|---|---|
| Application and navigation | `FoodDonationApp.java`, `AppNavigator.java` | Starts JavaFX, controls login state, and coordinates role-based screen navigation. |
| Authentication | `LoginView.java`, `RegisterView.java`, `UserAccount.java`, `AdminDashboardView.java` | Allows users to create accounts, login, and route automatically to the correct dashboard based on role. |
| Model classes | `Donation.java`, `FoodRequest.java` | Represents donation and food-assistance request objects. |
| Storage | `DataManager.java`, `FileStorageService.java`, `LoadReport.java` | Manages objects and saves/loads donation, request, and account text files. |
| User interface | `DashboardView.java`, `AdminDashboardView.java`, `AdminView.java`, `SummaryView.java`, `DonationFormView.java`, `RequestFormView.java` | Builds modern JavaFX screens and handles user-interface events. |
| Shared UI utilities | `UiStyle.java`, `NavigationMenu.java` | Provides modern colours, cards, input styles, reusable taskbars, hover effects, and active-page highlighting. |
| Utilities | `AlertUtil.java`, `ValidationUtil.java` | Provides reusable alert and validation methods. |
| Exception handling | `InvalidDataFormatException.java` | Represents invalid form or file data. |

## Project Structure

```text
CommunityFoodDashboardStorage/
├── src/
│   ├── AdminDashboardView.java
│   ├── AdminView.java
│   ├── AlertUtil.java
│   ├── AppNavigator.java
│   ├── DashboardView.java
│   ├── DataManager.java
│   ├── Donation.java
│   ├── DonationFormView.java
│   ├── FileStorageService.java
│   ├── FoodDonationApp.java
│   ├── FoodRequest.java
│   ├── InvalidDataFormatException.java
│   ├── LoadReport.java
│   ├── LoginView.java
│   ├── RegisterView.java
│   ├── RequestFormView.java
│   ├── SummaryView.java
│   ├── UserAccount.java
│   ├── ValidationUtil.java
│   ├── UiStyle.java
│   └── NavigationMenu.java

├── data/                         # Created automatically after data is saved
├── out/                          # Compiled classes, created by run.bat
├── README.md
└── run.bat
```

## Main System Functions

| Feature | Responsible files | Description |
|---|---|---|
| User registration | `RegisterView.java`, `DataManager.java` | Creates a username and password, checks duplicate usernames, validates password confirmation, and saves the account. |
| User/Admin login | `LoginView.java`, `DataManager.java`, `FoodDonationApp.java` | Checks credentials and automatically routes `User` accounts to the User Dashboard and `Admin` accounts to the Admin Dashboard. |
| Logout | `DashboardView.java`, `FoodDonationApp.java` | Clears the current login and returns to the Login screen. |
| User Dashboard | `DashboardView.java`, `NavigationMenu.java`, `UiStyle.java` | Shows the signed-in user, modern statistic cards, storage status, and highlights the active Dashboard taskbar button. |
| Admin Dashboard | `AdminDashboardView.java`, `NavigationMenu.java`, `UiStyle.java` | Shows administrator statistics, uses the Admin colour theme, and highlights the active Admin Dashboard taskbar button. |
| Register food donations | `DonationFormView.java`, `DataManager.java` | Allows a user to submit food donation information. |
| Submit food assistance requests | `RequestFormView.java`, `DataManager.java` | Allows a user to submit a request for food assistance. |
| View records | `AdminView.java`, `SummaryView.java`, `NavigationMenu.java` | Shows donation and request records and highlights `Manage Records` or `Summary` when those pages are open. |
| Administrator functions | `AdminDashboardView.java`, `AdminView.java`, `DataManager.java` | Allows Admin accounts to search records, update request status, display details, and delete records. |
| File save/load | `FileStorageService.java` | Stores donations, requests, and accounts in separate text files. |
| Form validation | `ValidationUtil.java`, `InvalidDataFormatException.java` | Checks required fields, phone numbers, positive integers, passwords, and confirmation fields. |

## Run the Application

The project uses standard Java source files and does not require Maven or Gradle. Install a JDK and JavaFX SDK first. This Windows version uses the JavaFX SDK path configured in `run.bat`:

```text
C:\Program Files\Java\javafx-sdk-21.0.11\lib
```

### Windows automatic run

Open the project folder and double-click `run.bat`. Alternatively, open Command Prompt in the project folder and run:

```bat
run.bat
```

The batch file automatically compiles every `.java` file in `src`, writes compiled classes to `out`, and starts `FoodDonationApp`. If your JavaFX SDK is installed in a different location, edit the `JAVAFX_LIB` line in `run.bat`. Keep the quotation marks because the path contains spaces.

### Windows manual compile and run command

Open Command Prompt in the project folder and execute:

```bat
if exist out rmdir /s /q out
mkdir out

javac --module-path "C:\Program Files\Java\javafx-sdk-21.0.11\lib" --add-modules javafx.controls -d out src\*.java

java --module-path "C:\Program Files\Java\javafx-sdk-21.0.11\lib" --add-modules javafx.controls -cp out FoodDonationApp
```

Because the project uses the default package, the main class is simply `FoodDonationApp`. If Windows reports that `java` or `javac` is not recognised, install a JDK and add its `bin` folder to the Windows PATH.

## Data File Formats

The application automatically creates the `data` folder and stores one record per line. On first startup, it also creates the demonstration administrator account `admin` with password `admin123` if no Admin account already exists.

### `data/accounts.txt`

```text
Username|Password|Role
```

Example:

```text
alex|food1234|User
```

### `data/donations.txt`

```text
DonationID|DonorName|Phone|FoodName|Category|Quantity|ExpiryDate|Notes
```

Example:

```text
D001|Ali Tan|0123456789|Rice|Dry Food|5|30/12/2026|Sealed package
```

### `data/requests.txt`

```text
RequestID|RequesterName|Phone|FamilySize|CategoryNeeded|Urgency|Notes|Status
```

Example:

```text
R001|Siti Aminah|0189999999|4|Dry Food|High|Family has young children|Pending
```

The pipe symbol (`|`) is the text-file separator. The program replaces pipes and line breaks in user text before saving so the file format remains readable.

> This is an educational local text-file login system for the course project. It is not intended to provide production-level password security.

## Role-Based Authentication Flow

When the application starts, `FoodDonationApp` loads saved records and account data, creates the demonstration administrator account if necessary, and displays one shared `LoginView`. A new normal user selects **Register New Account**, enters a username, password, and confirmation password, and clicks **Create Account**. The Register page always creates the role `User`; users cannot register themselves as administrators.

After a successful login, `completeLogin()` receives both the username and role. A `User` account is routed to `DashboardView`, where the user can donate food, request food assistance, view summaries, and save or load data. An `Admin` account is routed to `AdminDashboardView`, where the administrator can open `AdminView`, review records, update request statuses, view Summary, and save or load data. The regular User Dashboard does not display the Manage Records button, and `showAdminView()` also checks the role before opening the administrator screen.

The demonstration administrator account is:

```text
Username: admin
Password: admin123
Role: Admin
```

After login, both roles have a **Logout** button. Logout clears the current username and role and returns to Login without deleting saved records. All authenticated pages use the shared `NavigationMenu` taskbar. The active page is displayed with an accent-colour background, white text, bold font, and a left border so users can immediately see whether they are on Dashboard, Donate Food, Request Food, Summary, or Manage Records.

## Integration with Other Team Members

Because all classes use the default package, teammates do not need to write imports for the project classes. They can directly create and use the shared objects:

```java
Donation donation = new Donation(
        dataManager.generateDonationId(),
        donorName,
        phone,
        foodName,
        category,
        quantity,
        expiryDate,
        notes
);
dataManager.addDonation(donation);
```

A food request form can directly use:

```java
FoodRequest request = new FoodRequest(
        dataManager.generateRequestId(),
        requesterName,
        phone,
        familySize,
        categoryNeeded,
        urgency,
        notes,
        "Pending"
);
dataManager.addRequest(request);
```

The most important integration rule is that the application must use **one shared `DataManager` object**. Do not create another `new DataManager()` inside a teammate's form class.

The current navigation methods in `FoodDonationApp` are:

```java
setRoot(new DonationFormView(dataManager, this).build());
setRoot(new RequestFormView(dataManager, this).build());
```

When the final team forms are ready, replace those two view class names while keeping the shared `dataManager` and navigator argument.

## Demonstration Flow

First login with the demonstration administrator account to show the separate **Admin Dashboard**. Open **Manage Records**, select **Food Requests**, change a selected request from **Pending** to **Approved**, and then open **Summary** to demonstrate the updated total. Logout, select **Register New Account**, create a normal user account, and login again. The **User Dashboard** should show Donate Food and Request Food but should not show Manage Records. Submit one donation and one food request, click **Save Data**, close the program, reopen it, login again, and use **Load Data** to show that the account and system records are recovered from text files.

## Suggested Presentation Explanation

Explain that `FoodDonationApp` creates the JavaFX `Stage`, loads the account and system files, and controls role-based navigation. Explain that `LoginView` and `RegisterView` use `TextField`, `PasswordField`, buttons, event handlers, validation, and alerts. Explain that `UiStyle` centralises the modern colour palette, cards, inputs, and button styles, while `NavigationMenu` creates the shared taskbar and changes the active page button colour. Explain that `UserAccount` stores a username, password, and role, while `DataManager` uses an `ArrayList` to manage accounts, donations, and requests. Explain that `FileStorageService` uses `PrintWriter`, `Scanner`, and try-with-resources for text-file I/O. Finally, explain that `DashboardView` is for normal users, `AdminDashboardView` and `AdminView` are for administrators, and `SummaryView` provides the shared record overview. The administrator functions are an optional enhancement of the required system.
