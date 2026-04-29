# JUPITER Management System - JavaFX GUI

## Overview

This document describes the JavaFX graphical user interface that has been added to the JUPITER Management System project. The GUI provides a modern, professional interface for the existing console-based application while maintaining all the original business logic.

## Features

### Login Interface
- Clean, modern login screen with gradient background
- Input validation and error handling
- Attempt tracking with user blocking after failed attempts
- Support for all user roles (Admin, Team Leader, Coder)

### Admin Dashboard
- **Users Tab**: View, create, and delete users
- **Clans Tab**: View, create, and delete clans
- **Statistics Tab**: Real-time system statistics including:
  - Total users count
  - Total clans count
  - Users by role (Admins, Team Leaders, Coders)

### Team Leader Dashboard
- **Cells Tab**: Manage cells and cell assignments
- **Information Tab**: Create and manage information/announcements
- **Team Tab**: View team statistics and information

### Coder Dashboard
- **Information Tab**: View available information and announcements
- **Profile Tab**: View personal profile information
- Double-click on information items to view details

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 17.0.2 (automatically included via Maven)

### Build Instructions

1. **Compile the project**:
   ```bash
   mvn clean compile
   ```

2. **Package the application**:
   ```bash
   mvn clean package
   ```

## Running the Application

### Console Mode (Original)
```bash
mvn exec:java -Dexec.mainClass="com.management.jupiter.Main"
```

### GUI Mode (New JavaFX Interface)
```bash
mvn exec:java -Dexec.mainClass="com.management.jupiter.Main" -Dexec.args="--gui"
```

Or alternatively, run the JavaFX application directly:
```bash
mvn javafx:run
```

## Architecture

### Project Structure
```
src/main/java/com/management/jupiter/
├── fx/
│   ├── JupiterFXApplication.java          # Main JavaFX application class
│   └── controllers/
│       ├── LoginController.java           # Login interface controller
│       ├── AdminDashboardController.java  # Admin dashboard controller
│       ├── TlDashboardController.java     # Team Leader dashboard controller
│       └── CoderDashboardController.java   # Coder dashboard controller
├── controllers/                           # Existing business controllers (unchanged)
├── models/                               # Existing data models (unchanged)
├── repository/                            # Existing data access layer (unchanged)
├── services/                              # Existing business services (unchanged)
└── views/                                 # Original console views (unchanged)

src/main/resources/
├── views/
│   ├── login.fxml                         # Login interface FXML
│   ├── admin-dashboard.fxml              # Admin dashboard FXML
│   ├── tl-dashboard.fxml                  # Team Leader dashboard FXML
│   └── coder-dashboard.fxml              # Coder dashboard FXML
└── styles/
    └── main.css                           # Global CSS styling
```

### Key Design Principles

1. **Non-Intrusive Integration**: The original business logic remains completely unchanged
2. **Dual Mode Support**: Application can run in both console and GUI modes
3. **Separation of Concerns**: GUI logic is completely separate from business logic
4. **Professional Design**: Modern, clean interface with consistent styling
5. **Error Handling**: Comprehensive error handling and user feedback

## UI Components

### Styling
- Modern CSS-based styling with consistent color scheme
- Responsive design elements
- Professional gradient backgrounds
- Hover effects and transitions
- Card-based layouts for statistics

### Common Features
- Tab-based navigation for different functional areas
- Data tables with sorting and selection
- Modal dialogs for create/edit operations
- Real-time data refresh capabilities
- Consistent navigation and logout functionality

## Database Configuration

The GUI uses the same database configuration as the console application. Ensure your `.env` file is properly configured with database connection details.

## Troubleshooting

### Common Issues

1. **Java Version Compatibility**: Ensure you're using Java 17 or higher
2. **JavaFX Dependencies**: The Maven configuration includes all necessary JavaFX dependencies
3. **Resource Loading**: FXML and CSS files are loaded from the classpath
4. **Database Connection**: Verify your database configuration in the `.env` file

### Error Messages

- **"Invalid target release"**: Update your Java version or modify the Maven compiler configuration
- **"Resource not found"**: Ensure FXML and CSS files are in the correct `src/main/resources` directory
- **"Database connection failed"**: Check your database configuration and connectivity

## Future Enhancements

Potential improvements that could be added:
- Advanced filtering and search capabilities
- Data export functionality
- User profile editing
- Enhanced cell management visualization
- Real-time notifications
- Dark mode theme support
- Multi-language support

## Development Notes

### Adding New Features

1. Create or modify FXML files in `src/main/resources/views/`
2. Implement controller logic in the appropriate controller class
3. Add CSS styles in `src/main/resources/styles/main.css`
4. Update the main application class if new navigation is needed

### Testing

The GUI integration maintains compatibility with existing unit tests. GUI-specific tests should be added for new functionality.

## Support

For issues related to:
- **Business Logic**: Refer to the original project documentation
- **GUI Issues**: Check this documentation and ensure proper JavaFX setup
- **Build Issues**: Verify Maven configuration and Java version
