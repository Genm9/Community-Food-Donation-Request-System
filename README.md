# Community Food Donation and Request System

## JavaFX dashboard, authentication, storage, and record management

The **Community Food Donation and Request System** is a JavaFX desktop application for recording food donations and food-assistance requests. Users can create accounts, sign in, submit donation or request records, review summaries, export records as CSV, and save data locally. Administrator accounts can additionally review records, update request statuses, search data, delete records, and use the CSV export function when a spreadsheet-compatible copy of the records is needed.

The project uses Java's **default package**: all Java source files are stored directly under `src/` and do not contain package declarations. The current project layout and source list are documented in [`file_manifest.txt`][1].

## Features

| Feature | Description |
|---|---|
| Account registration | Creates normal `User` accounts, validates the input, checks for duplicate usernames, and stores account data locally. |
| Role-based login | Routes normal users to the User Dashboard and administrators to the Admin Dashboard after authentication. |
| Donation management | Uses the integrated FoodDonationSystem layout: Food Item, Quantity, Category, Expiry Date, Pickup Location, Submit/Clear actions, and personal donation history. The page keeps the user's blue theme and stores records in the shared DataManager. |
| Food-assistance requests | Allows users to submit requests containing requester details, family size, category needed, urgency, and notes. |
| Record management | Allows administrators to search records, view details, update request status, and delete records. |
| Summary dashboard | Displays donation and request information, totals, and application status. |
| Local persistence | Saves accounts, donations, and requests to text files under the `data/` directory. |
| CSV export | Exports donation and request records in comma-separated values format so they can be opened in spreadsheet applications or used for reporting. |
| Scrollable forms | Wraps the integrated Donation and Request forms in JavaFX `ScrollPane` containers so all input fields and the donation history table remain accessible when the window is too short to display the complete form. |
| Form validation | Checks required fields, phone numbers, positive quantities, passwords, confirmation fields, and other input rules. |
| Logout | Clears the current login session and returns the application to the Login screen without deleting saved records. |

## Requirements

The Windows launcher is configured for the following environment:

| Requirement | Version or configuration |
|---|---|
| Operating system | Windows with Command Prompt or double-click support for `.bat` files |
| Java Development Kit | JDK 21 or a compatible JDK that provides `java` and `javac` |
| JavaFX SDK | JavaFX SDK 21.0.11, or another compatible JavaFX SDK installed locally |
| JavaFX module | `javafx.controls` |

The JavaFX library path currently configured in [`run.bat`][2] is:

```text
C:\Program Files\Java\javafx-sdk-21.0.11\lib
```

If JavaFX is installed elsewhere, update the `JAVAFX_LIB` value in `run.bat` before starting the application.

## Project structure

The source files use a flat structure rather than Java package folders. The following table reflects the current file manifest.

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
│   └── ValidationUtil.java
├── data/                         # Created or populated when application data is saved
├── out/                          # Compiled class files created by run.bat
├── README.md
├── file_manifest.txt
└── run.bat
```

### Source file responsibilities

| Area | Files | Responsibility |
|---|---|---|
| Application and navigation | `FoodDonationApp.java`, `AppNavigator.java` | Starts JavaFX, owns the main application flow, controls the current view, and coordinates navigation after login. |
| Authentication | `LoginView.java`, `RegisterView.java`, `UserAccount.java` | Registers users, authenticates accounts, stores account roles, and provides login-related views. |
| User and administrator views | `DashboardView.java`, `AdminDashboardView.java`, `AdminView.java`, `SummaryView.java` | Displays user and administrator dashboards, record-management screens, and summary information. |
| Data models | `Donation.java`, `FoodRequest.java` | Represents food donation and food-assistance request records. |
| Forms and responsive layout | `DonationFormView.java`, `RequestFormView.java` | Collects and validates donation and request data, and uses JavaFX `ScrollPane` containers so users can scroll through the complete forms when the available window height is limited. |
| Storage and reporting | `DataManager.java`, `FileStorageService.java`, `LoadReport.java` | Manages in-memory records, saves and loads text files, generates record IDs, and reports loading results. |
| Validation and alerts | `ValidationUtil.java`, `AlertUtil.java`, `InvalidDataFormatException.java` | Provides reusable validation, alert, and invalid-data handling functionality. |

## Run the application on Windows

The project does not use Maven or Gradle. The included batch script compiles all Java files in `src/` and launches the `FoodDonationApp` main class.

### Automatic run

Open the project folder and double-click `run.bat`. You can also open Command Prompt in the project folder and execute:

```bat
run.bat
```

The script performs the following steps:

1. Changes the working directory to the folder containing `run.bat`.
2. Checks whether `javafx.controls.jar` exists in the configured JavaFX library directory.
3. Removes and recreates the `out/` directory.
4. Compiles every Java file in `src/` with `javac`.
5. Starts `FoodDonationApp` with the JavaFX `controls` module.
6. Pauses and displays an error message if the JavaFX library is missing, compilation fails, or the application exits with an error.

The relevant commands in the script are equivalent to:

```bat
if exist out rmdir /s /q out
mkdir out

javac --module-path "C:\Program Files\Java\javafx-sdk-21.0.11\lib" --add-modules javafx.controls -d out src\*.java

java --module-path "C:\Program Files\Java\javafx-sdk-21.0.11\lib" --add-modules javafx.controls -cp out FoodDonationApp
```

Because the project uses the default package, the main class is invoked as `FoodDonationApp`, without a package-qualified name.

### Troubleshooting

| Problem | Recommended action |
|---|---|
| `java` or `javac` is not recognised | Install JDK 21 and add the JDK `bin` directory to the Windows `PATH`. Open a new Command Prompt after changing `PATH`. |
| JavaFX library was not found | Confirm that `javafx.controls.jar` exists under the configured JavaFX `lib` directory, or change `JAVAFX_LIB` in `run.bat`. |
| Compilation fails | Read the compiler output, confirm that all source files are present under `src/`, and verify that the JDK and JavaFX SDK versions are compatible. |
| Old compiled classes cause confusion | Run `run.bat`, which deletes and recreates `out/` before compiling. |
| Saved data is not visible | Confirm that the application is launched from the project folder and inspect the `data/` directory for the relevant text files. |

## Data storage

The application uses local text files rather than a database. The `data/` directory is created or populated when records are saved. Each record occupies one line, and fields are separated by the pipe character (`|`). Donation records written by the integrated module use the original eight columns followed by `PickupLocation`, `DateSubmitted`, and `Status`; the loader remains backward-compatible with older eight-column donation rows. The application sanitises pipe characters and line breaks in user-entered text before saving so that records remain readable.

## CSV export

The application includes an **Export CSV** function for producing a spreadsheet-compatible copy of donation and food-assistance request records. CSV files are useful for reporting, filtering, sorting, printing, and sharing records with team members who do not need to open the JavaFX application.

CSV export is a reporting function and does not replace the application's normal text-file persistence. Continue using the application's save and load functions to maintain the local `data/` files used when the application starts or reloads records. When opening an exported file in a spreadsheet program, verify that the columns are interpreted correctly and that values containing commas are displayed as a single field.

## Scrollable donation and request forms

The Donation and Request forms now use JavaFX `ScrollPane` containers. This layout improvement keeps the form usable when the application window is smaller than the full set of input controls. Users can scroll vertically to reach fields, validation messages, and action buttons that would otherwise be below the visible area.

The scrollable layout is especially useful on smaller laptop screens and when the form contains optional notes or additional request details. Resizing the window does not remove any fields; users can access the complete form by scrolling within the content area.

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
DonationID|DonorName|Phone|FoodName|Category|Quantity|ExpiryDate|Notes|PickupLocation|DateSubmitted|Status
```

Example:

```text
D001|Ali Tan|N/A|Rice|Dry Food|5|2026-12-30||Community Hall|2026-08-17|PENDING
```

### `data/requests.txt`

```text
RequestID|RequesterName|Phone|FamilySize|CategoryNeeded|Urgency|Notes|Status
```

Example:

```text
R001|Siti Aminah|0189999999|4|Dry Food|High|Family has young children|Pending
```

> **Security note:** This is an educational local text-file authentication system. Passwords are stored in the local account file and the project is not intended to provide production-level password security.

## Authentication and roles

When the application starts, `FoodDonationApp` loads the saved account and system data, creates the required data-management objects, and displays the shared `LoginView`. A new normal user can select **Register New Account**, enter a username, password, and confirmation password, and create an account. The registration flow creates the role `User`; users cannot assign themselves the `Admin` role through the registration screen.

After successful authentication, the application routes the account according to its role:

| Role | Destination | Available capabilities |
|---|---|---|
| `User` | `DashboardView` | Submit donations, submit food-assistance requests, view summaries, save or load data, and log out. |
| `Admin` | `AdminDashboardView` | Review records, search and inspect data, update request statuses, delete records, view summaries, save or load data, and log out. |

The demonstration administrator account described by the original project documentation is:

```text
Username: admin
Password: admin123
Role: Admin
```

For a classroom demonstration, change or remove this default account before using the application with real data. Logout clears the current username and role but does not delete saved records.

## Integration guidance for team members

All classes use the default package, so project classes do not require package imports. Forms should use the existing shared `DataManager` instance instead of creating a second data manager. Keeping one shared instance ensures that records entered in one view are immediately available to the other views and to the summary and administrator screens.

A donation can be added through the shared manager using the following pattern:

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

A food-assistance request can be added with the following pattern:

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

The existing application navigation uses the shared data manager and navigator when opening the forms:

```java
setRoot(new DonationFormView(dataManager, this).build());
setRoot(new RequestFormView(dataManager, this).build());
```

The integrated `DonationFormView` preserves the teammate's visual order and wording—FOOD DRIVE header, SUBMIT A FOOD DONATION form, Clear/Submit actions, and MY DONATION HISTORY table—while using JavaFX and the user's shared DataManager. The logged-in username is stored as the donor name, the teammate's pickup location is stored as a dedicated donation field, and the original user-project blue theme is applied through `UiStyle`. If the team replaces either form class, preserve the shared `dataManager` and navigator arguments unless the application architecture is intentionally changed.

## Suggested demonstration flow

For a classroom or project presentation, first sign in with the demonstration administrator account and show the separate Admin Dashboard. Open the record-management screen, select a food request, change its status from `Pending` to `Approved`, and open Summary to demonstrate that the record status is reflected in the overview. Use **Export CSV** to create a spreadsheet-compatible copy of the donation or request records. Then log out, create a normal user account, and sign in as that user. Open the Donation and Request forms, demonstrate that the `ScrollPane` allows access to the complete form on a small window, submit one donation and one food-assistance request, save the data, close the application, reopen it, and load the saved records to demonstrate local persistence.

During the demonstration, explain that `FoodDonationApp` creates the JavaFX stage and controls application navigation; `LoginView` and `RegisterView` handle authentication; `DataManager` manages accounts, donations, and requests; `FileStorageService` handles text-file persistence; `DashboardView` is intended for normal users; and `AdminDashboardView` plus `AdminView` provide administrator functions. `SummaryView` presents the shared record overview, while `ValidationUtil`, `AlertUtil`, and `InvalidDataFormatException` support input checking and user feedback.

## Limitations and future improvements

The current implementation is intentionally lightweight and suitable for an educational desktop project. It uses a flat source layout, local text-file storage, CSV export for reporting, scrollable JavaFX data-entry forms, and a simple role field. Potential future improvements include adding Java packages, replacing text files with a database, hashing passwords, introducing stronger session and permission handling, adding automated tests, supporting configurable JavaFX paths, and providing cross-platform launch scripts.