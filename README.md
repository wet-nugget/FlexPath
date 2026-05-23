# FlexPath

FlexPath is an Android workout planning app built with a lightweight MVP architecture. It uses DataStore preferences to persist user credentials, saved workouts, and workout plans.

## Folder structure

- `app/`
  - `build.gradle.kts` — Android module configuration and dependencies.
  - `src/main/java/com/example/flexpath/` — app code organized by screen and feature package.
  - `src/main/res/` — UI layouts, icons, strings, and other Android resources.
  - `src/main/AndroidManifest.xml` — app entry points and component declarations.
  - `src/test/` — JVM unit tests.
  - `src/androidTest/` — Android instrumentation tests.
- `gradle/` — Gradle wrapper and version catalogs.
- `build.gradle.kts` — root Gradle build configuration.
- `settings.gradle.kts` — module inclusion and project name.
- `gradle.properties` — shared Gradle properties.
- `local.properties` — local SDK and environment settings.

## How the app works

The app flow is centered around a simple login/register flow, a dashboard, workout management, plan management, and a session runner.

- `LoginActivity` authenticates the user against saved DataStore credentials.
- `RegisterActivity` stores a username/password pair in DataStore.
- `DashboardActivity` displays a greeting and navigation shortcuts to workouts, plans, and profile.
- `WorkoutsActivity` exposes a predefined workout pool and the user's saved workout list.
- `PlansActivity` lists saved workout plans and supports plan creation, editing, cloning, deletion, and navigation to details.
- `PlanDetailActivity` shows plan contents, allows adding/removing workouts, and supports drag-and-drop reordering.
- `PlanRunnerActivity` runs a plan in sequence, letting the user skip or complete each workout.
- `ProfileActivity` displays the current username and allows clearing stored user data.

## Core features

- credential-based login and registration
- persistent user data in Android DataStore
- predefined workout pool with muscle group, equipment, and difficulty metadata
- save workouts to a personal list
- create, rename, delete, and clone workout plans
- add workouts to plans and reorder plan items
- run a workout plan session with skip/complete controls
- clear saved user credentials from profile

## Key packages and classes

### `com.example.flexpath.screens.login`
- `LoginActivity`
- `LoginPresenter`
- `LoginContract`

Important methods:
- `LoginPresenter.onLoginClicked(username, password)`
- `LoginContract.View.showUsernameError(message)`
- `LoginContract.View.showPasswordError(message)`
- `LoginContract.View.showLoginSuccess(username)`
- `LoginContract.View.showLoginFailure(message)`
- `LoginContract.View.showLoading(show)`

### `com.example.flexpath.screens.register`
- `RegisterActivity`
- `RegisterPresenter`
- `RegisterContract`

Important methods:
- `RegisterPresenter.onRegister(username, password, reenter)`
- `RegisterContract.View.showError(message)`
- `RegisterContract.View.showSuccess()`
- `RegisterContract.View.showLoading(show)`

### `com.example.flexpath.screens.dashboard`
- `DashboardActivity`
- `DashboardPresenter`
- `DashboardContract`

Important methods:
- `DashboardPresenter.loadUser()`
- `DashboardPresenter.onLogoutClicked()`
- `DashboardPresenter.onProfileClicked()`
- `DashboardPresenter.onWorkoutsClicked()`
- `DashboardPresenter.onPlansClicked()`
- `DashboardContract.View.showGreeting(username)`
- `DashboardContract.View.navigateToProfile(username)`
- `DashboardContract.View.navigateToLogin()`
- `DashboardContract.View.navigateToWorkouts()`
- `DashboardContract.View.navigateToPlans()`

### `com.example.flexpath.screens.profile`
- `ProfileActivity`
- `ProfilePresenter`
- `ProfileContract`

Important methods:
- `ProfilePresenter.loadProfile()`
- `ProfilePresenter.clearUsers()`
- `ProfilePresenter.onDashboardClicked()`
- `ProfilePresenter.onWorkoutsClicked()`
- `ProfilePresenter.onPlansClicked()`
- `ProfileContract.View.showUsername(username)`
- `ProfileContract.View.showMessage(message)`
- `ProfileContract.View.showLoading(show)`
- `ProfileContract.View.navigateToDashboard()`
- `ProfileContract.View.navigateToLogin()`
- `ProfileContract.View.navigateToWorkouts()`
- `ProfileContract.View.navigateToPlans()`

### `com.example.flexpath.screens.workouts`
- `WorkoutsActivity`
- `WorkoutsPresenter`
- `WorkoutsContract`
- `WorkoutAdapter`
- `WorkoutItem`
- `MuscleGroup`
- `Equipment`
- `Difficulty`

Important methods:
- `WorkoutsPresenter.loadAll()`
- `WorkoutsPresenter.addFromPool(id)`
- `WorkoutsPresenter.removeFromUserList(id)`
- `WorkoutsPresenter.onDashboardClicked()`
- `WorkoutsPresenter.onProfileClicked()`
- `WorkoutsPresenter.onPlansClicked()`
- `WorkoutsContract.View.showProvidedPool(pool)`
- `WorkoutsContract.View.showUserList(list)`
- `WorkoutsContract.View.showAdded(item)`
- `WorkoutsContract.View.showRemoved(item)`
- `WorkoutsContract.View.showMessage(message)`
- `WorkoutsContract.View.showLoading(show)`

### `com.example.flexpath.screens.plans`
- `PlansActivity`
- `PlansPresenter`
- `PlansContract`
- `PlanDetailActivity`
- `PlanDetailPresenter`
- `PlanDetailContract`
- `PlanRunnerActivity`
- `PlanRunnerPresenter`
- `PlanRunnerContract`
- `PlanAdapter`
- `PlanDetailAdapter`
- `WorkoutPlan`

Important methods:
- `PlansPresenter.loadPlans()`
- `PlansPresenter.createPlan(name, description)`
- `PlansPresenter.deletePlan(planId)`
- `PlansPresenter.clonePlan(planId)`
- `PlansPresenter.renamePlan(planId, name, description)`
- `PlansPresenter.onPlanClicked(planId)`
- `PlanDetailPresenter.loadPlan(planId)`
- `PlanDetailPresenter.addWorkoutToPlan(planId, workoutId)`
- `PlanDetailPresenter.removeWorkoutFromPlan(planId, workoutId)`
- `PlanDetailPresenter.reorderWorkout(planId, fromPosition, toPosition)`
- `PlanDetailPresenter.updatePlan(planId, name, description)`
- `PlanDetailPresenter.startPlan(planId)`
- `PlanRunnerPresenter.loadSession(planId)`
- `PlanRunnerPresenter.completeWorkout()`
- `PlanRunnerPresenter.skipWorkout()`
- `PlanDetailContract.View.showPlan(plan, workouts)`
- `PlanDetailContract.View.showEmptyPlan()`
- `PlanRunnerContract.View.showSession(planName, workout, currentIndex, total)`
- `PlanRunnerContract.View.showFinished()`

### `com.example.flexpath.data`
- `UserRepository`
- `WorkoutsRepository`
- `DataStoreModule`

Important methods:
- `UserRepository.getSavedCredentials()`
- `UserRepository.getSavedUsername()`
- `UserRepository.saveUser(username, password)`
- `UserRepository.clearUser()`
- `WorkoutsRepository.getProvidedPool()`
- `WorkoutsRepository.getWorkoutById(id)`
- `WorkoutsRepository.loadUserList()`
- `WorkoutsRepository.saveUserList(list)`
- `WorkoutsRepository.addFromPoolById(id)`
- `WorkoutsRepository.removeFromUserListById(id)`
- `WorkoutsRepository.clearUserList()`
- `Context.dataStore` (DataStore preferences entry point)

### `com.example.flexpath.ui`
- `ViewExtensions`

Important methods:
- `ViewGroup.setEnabledRecursive(enabled)`
- `View.setVisible(visible)`

## Data model

- `WorkoutItem` defines each workout with `id`, `title`, `primaryMuscle`, `secondaryMuscles`, `equipment`, `difficulty`, and `description`.
- `WorkoutPlan` stores a plan with `id`, `name`, `description`, `workoutIds`, `createdAt`, and `lastUsedAt`.
- `WorkoutPlan` also exposes derived values such as `workoutCount`, `estimatedDurationMinutes`, and `displaySubtitle()`.
