🔐 PRIVORA

Privacy-Controlled Document Sharing & Secure Printing System

“Share the document, but never lose control over it.”

PRIVORA is a secure document sharing and controlled printing system designed to protect sensitive documents when users need to print them through external Xerox/printing centres.

Instead of simply sending a document to a printing centre, PRIVORA allows the document owner to define who can print, where it can be printed, how many copies can be printed, and how long the request remains valid.


📌 Problem Statement

Organizations and individuals often need to print confidential documents through external Xerox or printing centres when an in-house printer is unavailable.

This creates several problems:

- 🔓 Risk of unauthorized access to confidential documents
- 📄 No proper control over how many copies are printed
- 🗑️ Uncertainty about document handling after printing
- 🔑 No additional authorization before printing
- ⏳ Documents may remain accessible longer than required
- 📋 Manual management of printing requests and queues
- 👥 Difficulty in tracking printing activities

PRIVORA addresses these problems by introducing a secure, rule-based and trackable printing workflow.


💡 Our Solution

PRIVORA creates a secure connection between:

Document Owner → Xerox Centre → Physical Printer → Admin

The user uploads a document and defines printing rules before sending it to a selected Xerox centre.

The Xerox operator can process the request only according to the permissions defined by the user.

Core Concept

«The user controls the document.
PRIVORA enforces the rules.
The Xerox centre performs the print.»


🚀 Key Features

👤 User Module

- 🔐 Secure user authentication
- 📤 Document upload
- 🔒 Document encryption
- 🔢 Set maximum print limit
- ⏱️ Set document/request expiry time
- 🔑 PIN-based print authorization
- 🏪 Select a specific Xerox centre
- 📋 Create and track print requests
- 📊 View request status
- 🔔 Monitor printing activity


🏪 Xerox Centre Module

- 🔐 Secure Xerox-centre login
- 📥 Incoming print requests
- 📄 View request details
- 🔑 PIN verification
- 🔓 Controlled document decryption
- 🖨️ Physical printer integration
- 🔢 Enforced print limits
- 📋 Request status management
- 📊 Printing activity tracking


👨‍💼 Admin Module

- 👥 User management
- 🏪 Xerox-centre management
- 📋 Request monitoring
- 📊 System analytics
- 📈 Activity monitoring
- 🔍 Centralized system overview


🔄 System Workflow

                    PRIVORA
                       │
                       ▼
                 USER LOGIN
                       │
                       ▼
                UPLOAD DOCUMENT
                       │
                       ▼
                 ENCRYPT DOCUMENT
                       │
                       ▼
                 SET PRINT LIMIT
                       │
                       ▼
                  SET EXPIRY
                       │
                       ▼
                   CREATE PIN
                       │
                       ▼
              SELECT XEROX CENTRE
                       │
                       ▼
              ┌─────────────────┐
              │  XEROX LOGIN    │
              └────────┬────────┘
                       │
                       ▼
              INCOMING REQUEST
                       │
                       ▼
                  VIEW REQUEST
                       │
                       ▼
                VERIFY PIN
                       │
                       ▼
              SECURE DECRYPTION
                       │
                       ▼
                     PRINT
                       │
                       ▼
              PHYSICAL PRINTER
                       │
                       ▼
             CONTROLLED PRINTING
                       │
                       ▼
                ADMIN MONITORING


🔐 Security Architecture

Security is one of the core components of PRIVORA.

AES-256-GCM

Used to encrypt sensitive documents before they are transferred/stored.

AES-256-GCM provides:

- Confidentiality
- Data integrity
- Secure authenticated encryption


RSA-2048 / OAEP

Used for secure protection of encryption keys.

This provides an additional asymmetric cryptography layer for key protection.

SHA-256

Used for secure hashing of sensitive values such as PIN-related data.

Passwords/PINs should not be stored directly as plain text.


🔑 PIN-Based Authorization

A valid print request alone is not sufficient to print the document.

The authorized PIN must be verified before the document proceeds to the printing stage.

🛡️ Role-Based Access Control

PRIVORA separates permissions according to user roles:

USER
 │
 ├── Upload Documents
 ├── Create Requests
 ├── Set Print Rules
 └── Track Requests


XEROX CENTRE
 │
 ├── Receive Requests
 ├── Verify PIN
 ├── Process Documents
 └── Print


ADMIN
 │
 ├── Monitor Users
 ├── Monitor Centres
 ├── Monitor Requests
 └── View System Analytics


🖨️ Controlled Printing

One of PRIVORA's main features is print-limit enforcement.

For example:

User sets:

Print Limit = 2

The system allows:

Print #1 → Allowed
Print #2 → Allowed
Print #3 → Blocked

Therefore, the Xerox operator does not independently decide the number of copies.

PRIVORA enforces the print rule defined by the document owner.


⏱️ Document Expiration

The user can define how long a printing request should remain valid.

Example:

Request Created
      ↓
Valid for 10 minutes
      ↓
10 Minutes Completed
      ↓
Request Expired
      ↓
Further Processing Blocked

This reduces the period during which a sensitive document remains available for processing.

---

📋 Request Management

Instead of depending entirely on manual communication at the Xerox centre, PRIVORA creates digital print requests.

Typical request lifecycle:

PENDING
   ↓
ACCEPTED
   ↓
APPROVED
   ↓
PRINTING
   ↓
COMPLETED

This makes the printing workflow easier to monitor and helps organize multiple incoming requests.

---

☁️ Cloud & Backend

PRIVORA uses cloud services to support authentication, data management and protected document storage.

Firebase Authentication

Used for:

- User authentication
- Login management
- Identity verification

Firebase Firestore

Used for storing and tracking application data such as:

- Users
- Xerox centres
- Print requests
- Request status
- Activities

Cloudinary

Used as the cloud storage layer for protected/encrypted document data.

---

🧰 Technology Stack

Technology| Purpose
☕ Java| Core application development
🎨 JavaFX| Desktop GUI
🔥 Firebase Authentication| User authentication
🗄️ Firebase Firestore| Application data & request tracking
☁️ Cloudinary| Cloud document storage
🔐 AES-256-GCM| Document encryption
🔑 RSA-2048/OAEP| Key protection
#️⃣ SHA-256| Secure hashing
📄 Apache PDFBox| PDF processing
🖨️ Java Print API| Physical printer communication
📦 Maven| Dependency & project management

---

🏗️ Project Architecture

PRIVORA
│
├── User Module
│   ├── Authentication
│   ├── Upload Document
│   ├── Security Rules
│   ├── Print Request
│   └── Request Tracking
│
├── Xerox Module
│   ├── Authentication
│   ├── Incoming Requests
│   ├── PIN Verification
│   ├── Document Processing
│   └── Printing
│
├── Admin Module
│   ├── User Management
│   ├── Xerox Management
│   ├── Request Monitoring
│   └── Analytics
│
└── Security Layer
    ├── AES-256-GCM
    ├── RSA-2048/OAEP
    ├── SHA-256
    ├── PIN Authorization
    └── Expiration Control

---

📂 Project Structure

The main application is contained inside the "ciphercore" project directory.

A simplified structure is:

ciphercore/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── myprivora/
│   │   │           ├── config/
│   │   │           ├── controller/
│   │   │           ├── dao/
│   │   │           ├── model/
│   │   │           ├── session/
│   │   │           └── view/
│   │   │
│   │   └── resources/
│   │       ├── assets/
│   │       └── css/
│   │
│   └── ...
│
├── pom.xml
└── README.md

---

⚙️ Requirements

Before running PRIVORA, make sure the following are installed:

- Java JDK 17+
- Apache Maven
- JavaFX 21+
- Git
- Internet connection for Firebase/Cloudinary services
- A compatible physical printer for the printing module

---

🚀 Installation & Setup

1. Clone the Repository

git clone https://github.com/utkarshc06/PRIVORA_finall.git

Navigate into the project:

cd PRIVORA_finall/ciphercore

---

2. Build the Project

Run:

mvn clean

Then:

mvn compile

---

3. Configure Backend Services

Before running the complete application, configure the required:

- Firebase Authentication
- Firebase Firestore
- Cloudinary
- Required API/configuration values

⚠️ Do not commit API keys, private keys, passwords or other secrets to GitHub.

Use environment variables or secure configuration files where appropriate.

---

4. Run the Application

If the JavaFX Maven plugin is configured:

mvn javafx:run

Alternatively, run the application's main class from your IDE.

---

🔄 Example Use Case

Scenario

A company employee needs to print a confidential document at an external Xerox centre.

Without PRIVORA

Employee
   ↓
Gives Document to Xerox Centre
   ↓
Operator Opens/Prints Document
   ↓
No Strong Print Control
   ↓
Uncertain Document Handling

With PRIVORA

Employee
   ↓
Upload Document
   ↓
Encrypt
   ↓
Set Print Limit
   ↓
Set Expiry
   ↓
Create PIN
   ↓
Select Xerox Centre
   ↓
Xerox Receives Request
   ↓
PIN Verification
   ↓
Secure Processing
   ↓
Controlled Printing
   ↓
Request Completed

---

🎯 Advantages

- 🔐 Better control over sensitive documents
- 🔢 Controlled number of prints
- ⏱️ Time-based request expiration
- 🔑 PIN-based authorization
- 🏪 Specific Xerox-centre selection
- 📋 Digital request management
- 📊 Centralized monitoring
- 🖨️ Physical printer integration
- 👥 Role-based access
- 🔒 Multi-layer security architecture

---

🔮 Future Scope

PRIVORA can be extended with:

- 📱 Mobile application
- 🔔 Real-time push notifications
- 📷 QR-based document authorization
- 🔐 Advanced hardware-backed key management
- 📊 Advanced analytics and reporting
- 🏢 Enterprise organization accounts
- 🖨️ Multi-printer management
- 📝 Detailed audit logs
- 🔍 Advanced document activity monitoring
- 🌐 Web-based Xerox-centre portal
- 🤖 AI-based sensitive-document classification

---

👥 Team

Team CIPHERCORE

Project: PRIVORA

Domain: Cybersecurity • Secure Document Management • Controlled Printing

---

📜 Project Status

Status: ✅ Completed Prototype

PRIVORA demonstrates a complete secure document-to-print workflow involving the user, Xerox centre, physical printer and administrator.

---

⭐ Core Philosophy

«“Your document. Your rules. Your control.”»

PRIVORA transforms traditional document printing from a simple file-sharing process into a controlled and secure printing workflow.

---

📄 License

This project is developed as an academic/project implementation by Team CIPHERCORE.

For educational and demonstration purposes.
