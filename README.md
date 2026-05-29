# BuildTogether

> A Java console application that connects **Developers** with **Investors** — enabling teams to post project ideas, collaborate, apply for funding, and track investments end to end.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Data Models](#data-models)
- [Getting Started](#getting-started)
- [Application Flow](#application-flow)
- [Developer Features](#developer-features)
- [Investor Features](#investor-features)
- [Notification System](#notification-system)
- [Key Business Rules](#key-business-rules)
- [Sample Test Flow](#sample-test-flow)

---

## Overview

**BuildTogether** is a console-based Java application built using the **MVC (Model-View-Controller)** architectural pattern. It provides a platform where developers can form teams, post project ideas, and seek investment — while investors can browse projects, fund them, and track their portfolio.

All data is stored **in-memory** using Java `ArrayList`. No external database or internet connection is required. The project is designed as a learning exercise to demonstrate clean Java OOP, MVC pattern, and real-world feature design.

---

## Tech Stack

| Technology | Details |
|---|---|
| Language | Java |
| Architecture | MVC (Model — View — Controller) |
| Storage | In-memory (Java ArrayList) |
| Interface | Console / Terminal |
| IDE | Eclipse |

---

## Architecture

This project strictly follows the **MVC pattern**:

```
User Input
    ↓
  VIEW  →  reads input from terminal, displays output
    ↓       never touches DB directly
 MODEL  →  validates input, contains all business logic
    ↓       calls back the View with result
   DB   →  BuildTogetherDB singleton — stores and retrieves all data
```

### Key Design Principles

- **View** handles only input (`Scanner`) and output (`System.out`)
- **Model** handles only validation and business logic
- **DB** is accessed from Model only — never from View
- **Callback pattern** — Model calls back View methods like `onSuccess()` or `onFailed()`
- **User object** is passed from `LoginView` → `HomeView` → every feature View
- **Singleton DB** — one shared `BuildTogetherDB` instance across the entire app

---

## Project Structure

```
src/com/buildtogether/
│
├── BuildTogetherApplication.java        ← Entry point — main() starts here
│
├── dto/                                 ← Data classes (one per table)
│   ├── User.java                        ← Shared user — Developer or Investor
│   ├── LoginRequest.java                ← Holds email + password for login
│   ├── Developer.java                   ← Developer profile (bio, skills, experience)
│   ├── Investor.java                    ← Investor profile (company, available funds)
│   ├── Team.java                        ← Team created by a developer
│   ├── TeamMember.java                  ← Links developer to team (PENDING/ACCEPTED/REJECTED)
│   ├── Project.java                     ← Project idea posted by a team
│   ├── TechStack.java                   ← Technology used in a project
│   ├── FundingApplication.java          ← Developer's funding request to investor
│   ├── Investment.java                  ← Confirmed investment record (with payment status)
│   ├── ContactRequest.java              ← Investor's contact request to team leader
│   └── Notification.java               ← In-app notification (unread/read)
│
├── repository/
│   └── BuildTogetherDB.java             ← Singleton in-memory database
│
├── util/
│   └── ConsoleInput.java                ← Shared Scanner singleton
│
└── features/
    ├── signup/       → SignUpView, SignUpModel
    ├── signin/       → LoginView, LoginModel
    ├── home/         → HomeView, HomeModel
    ├── developer/    → DeveloperView, DeveloperModel
    ├── team/         → TeamView, TeamModel
    ├── project/      → ProjectView, ProjectModel
    ├── discovery/    → ProjectSearchView, ProjectSearchModel
    ├── funding/      → FundingView, FundingModel
    ├── investors/    → InvestorView, InvestorModel
    ├── contact/      → ContactView, ContactModel
    └── notifications/→ NotificationsView, NotificationsModel
```

---

## Data Models

| DTO | Key Fields | Enums |
|---|---|---|
| `User` | id, name, email, password, role, status, createdAt | `Role`: DEVELOPER, INVESTOR |
| `Developer` | id, userId, bio, skills, experienceLevel | — |
| `Investor` | id, userId, companyName, availableFunds | — |
| `Team` | id, teamName, teamLeaderId, createdAt | — |
| `TeamMember` | id, teamId, developerId, status | `MemberStatus`: PENDING, ACCEPTED, REJECTED |
| `Project` | id, title, description, teamId, domain, estimatedCost, timelineDays, status | `ProjectStatus`: IDEA, IN_PROGRESS, COMPLETED |
| `TechStack` | id, projectId, technology | — |
| `FundingApplication` | id, projectId, investorId, requestedAmount, status, appliedAt | `ApplicationStatus`: PENDING, APPROVED, REJECTED |
| `Investment` | id, investorId, projectId, amount, investedAt, paymentStatus | `PaymentStatus`: PENDING, PAID |
| `ContactRequest` | id, investorId, teamLeaderId, message, status, sentAt | `ContactStatus`: PENDING, ACCEPTED, DECLINED |
| `Notification` | id, userId, message, isRead, createdAt | — |

---

## Getting Started

### Prerequisites

- Java 8 or above
- Eclipse IDE (or any Java IDE)

### Run the Project

1. Clone or download the repository
2. Open in **Eclipse IDE**
3. Navigate to `BuildTogetherApplication.java`
4. Right-click → **Run As** → **Java Application**
5. The console shows the landing menu

```
Welcome to BuildTogether
Connecting Developers with Investors

1. Sign Up
2. Login
3. Exit
Choose an option:
```

---

## Application Flow

```
main() — BuildTogetherApplication
        ↓
Landing Menu → Sign Up / Login / Exit
        ↓
Login successful → User object created
        ↓
HomeModel checks user.getRole()
        ↓
    DEVELOPER                      INVESTOR
       ↓                              ↓
Developer Menu (15 options)    Investor Menu (10 options)
       ↓                              ↓
  Feature Views                 Feature Views
       ↓                              ↓
  Back to menu                  Back to menu
       ↓
Sign Out → Landing Menu again
```

---

## Developer Features

### Option 1 — My Profile
Set bio, skills, and experience level (Beginner / Mid / Senior). Prompts creation on first visit. Shows existing profile on subsequent visits.

### Option 2 — Create Team
Create a team and become the team leader automatically. Maximum 5 accepted members per team.

### Option 3 — Manage Team Members
- **Invite a developer** — sends an invitation request. Developer gets notified and must accept or decline
- **Remove a member** — remove an existing accepted member
- **View current members** — shows all accepted members with names, skills, experience, and role

### Option 4 — Post Project Idea
First selects which team the project belongs to (from all teams the developer is part of), then fills title, description, domain, estimated cost, and timeline. Project starts with status `IDEA`.

### Option 5 — Add Tech Stack
Add technologies to any project across all teams the developer belongs to.

### Option 6 — Update Project Status
Change project status: `IDEA` → `IN_PROGRESS` → `COMPLETED`.
On `COMPLETED`, all investors who invested are automatically notified.

### Option 7 — View My Projects
View all projects from all joined teams with full details — title, description, domain, budget, timeline, status, and tech stack.

### Option 8 — Search Investors
Browse all registered investors with company name and available funds.

### Option 9 — Apply for Funding
Select a project and investor, then enter the requested amount. Validates amount does not exceed investor's available funds. Prevents duplicate pending applications.

### Option 10 — My Funding Applications
View all funding applications across all projects with status (PENDING / APPROVED / REJECTED).

### Option 11 — Messages from Investors
View all contact requests received from investors with company name and message content.

### Option 12 — My Invitations *(pending badge)*
View all pending team invitations. Accept or decline each one. Team leader is notified of the decision.

### Option 13 — My Teams
View all teams the developer has joined (own team + accepted invitations). Each entry shows team name, leader name, and related projects with status. Option to **leave a team** — team leaders cannot leave their own team.

### Option 14 — Notifications *(unread badge)*
View all notifications. All marked as read after viewing.

### Option 15 — Sign Out

---

## Investor Features

### Option 1 — My Profile
Set company name and available funds.

### Option 2 — View All Projects
Browse all posted projects — title, domain, budget, timeline, status.

### Option 3 — Filter Projects
Filter by domain, maximum budget, or technology used.

### Option 4 — View Team Details
Shows all projects first. Select a project ID to view its full team — member names, skills, experience, roles, and tech stack.

### Option 5 — Contact Team Leader
Send a message to a team leader. Developer is immediately notified. View all sent requests and their status.

### Option 6 — View Funding Applications
View all funding applications received. For each application shows requested amount and whether investor can afford it.
- **Approve** — `investor.funds >= requestedAmount`. Funds deducted, project → `IN_PROGRESS`, developer notified
- **Reject** — application marked `REJECTED`, developer notified to try another investor

### Option 7 — Invest in Project
Browse all projects and invest directly. Two strict validations:
- `enteredAmount >= project.estimatedCost` (must meet full budget)
- `enteredAmount <= investor.availableFunds` (must have the funds)

On success: funds deducted, project → `IN_PROGRESS`, developer notified.

### Option 8 — Track My Investments
View all investments with project name, amount, project status, and payment status.
Once project is `COMPLETED`, investor can **mark payment as done** — developer is notified.

### Option 9 — Notifications *(unread badge)*
View all notifications. All marked as read after viewing.

### Option 10 — Sign Out

---

## Notification System

A real-time in-app notification system that shows badge counts in the menu.

| Trigger | Recipient | Message Tag |
|---|---|---|
| Team invite sent | Invited developer | `[TEAM INVITE]` |
| Invite accepted | Team leader | `[INVITE ACCEPTED]` |
| Invite declined | Team leader | `[INVITE DECLINED]` |
| Member left team | Team leader | `[MEMBER LEFT]` |
| Funding approved | Developer | `[FUNDING APPROVED]` |
| Funding rejected | Developer | `[FUNDING REJECTED]` |
| Investor invested directly | Developer | `[INVESTOR INTEREST]` |
| Project marked COMPLETED | All investors | `[PROJECT COMPLETED]` |
| Payment marked done | Developer | `[PAYMENT RECEIVED]` |
| Contact request received | Developer | `[NEW MESSAGE]` |

---

## Key Business Rules

| Rule | Details |
|---|---|
| Max team size | 5 accepted members per team |
| Team invite flow | Invitation sent → developer accepts or declines — no direct add |
| Team leader restriction | Cannot leave their own team |
| Multi-team membership | A developer can be part of many teams simultaneously |
| Project-team assignment | Developer selects which team the project belongs to when posting |
| Cross-team operations | Tech stack, status update, and funding apply across ALL developer's teams |
| Funding approval check | `investor.availableFunds >= application.requestedAmount` |
| Direct invest check | `enteredAmount >= project.estimatedCost` AND `enteredAmount <= investor.availableFunds` |
| Duplicate application | Cannot apply to the same investor for the same project twice (PENDING check) |
| Payment restriction | Investor can only mark payment done after project status is `COMPLETED` |
| Notification badges | Separate badge — invitations show `(N pending)`, notifications show `(N new)` |

---

## Sample Test Flow

```
1.  Sign up as Developer A  → login → complete profile → create team
2.  Sign up as Developer B  → login → complete profile → sign out
3.  Login as Developer A    → invite Developer B (option 3) → sign out
4.  Login as Developer B    → accept invitation (option 12) → sign out
5.  Login as Developer A    → post project — select team (option 4)
                            → add tech stack (option 5)
6.  Sign up as Investor     → login → complete profile (set company + funds)
7.  Login as Developer A    → apply for funding (option 9) → sign out
8.  Login as Investor       → view funding applications (option 6) → approve
9.  Login as Developer A    → check notifications (option 14)
                            → update project status → COMPLETED (option 6)
10. Login as Investor       → check notifications (option 9)
                            → track investments (option 8) → mark payment done
11. Login as Developer A    → check notifications → see [PAYMENT RECEIVED]
```

---

## Developer

Built by **Balavignesh S** — learning Java application development through hands-on project building.

Developed step by step following the **ThiranX** project MVC pattern as reference.

---

## License

This project is for educational purposes only.
