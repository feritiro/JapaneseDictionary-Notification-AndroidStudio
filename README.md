# JP Notify (JLPT Kanji Flashcard Notifications)

**JP Notify** is an Android app that shows Japanese JLPT vocabulary as lockscreen flashcard notifications. It works 100% offline using a local SQLite database and shows a random word when the screen is turned off — perfect for passive learning.

## Features

- Shows JLPT words (N1 to N5) on the lockscreen when the screen is turned off.
- Select which JLPT levels (N1–N5) you want to study using checkboxes.
- Fully offline – no internet required.
- Automatically launches after device reboot (`BOOT_COMPLETED`).
- Saves your selected JLPT level preferences between sessions.

## Notification Example

```
漢 (かん)
"Han (Chinese dynasty)"
```

If no kanji is available (e.g., テープ), it shows the reading directly.

## Tech Stack

- Kotlin + Android SDK
- SQLite (bundled in `/assets`)
- BroadcastReceiver: `SCREEN_OFF`, `BOOT_COMPLETED`
- Notifications with custom layout (`RemoteViews`)
- SharedPreferences to persist JLPT filter

## Project Structure

```
app/
├── assets/
│   └── jlpt_words.sqlite        # Local SQLite database
├── java/com/itiro/jp_notify/
│   ├── MainActivity.kt          # JLPT level selection (checkboxes)
│   ├── KanjiDatabaseHelper.kt   # SQLite logic + Kanji selection
│   ├── NotificationUtils.kt     # Handles notification display
│   ├── ScreenOffReceiver.kt     # Triggers on screen-off
│   └── BootReceiver.kt          # Triggers on device boot
├── res/layout/
│   └── notification_custom.xml  # Custom layout for the notification
```

## Permissions Required

- `POST_NOTIFICATIONS` – Required for Android 13+
- `RECEIVE_BOOT_COMPLETED` – To start the app after reboot

## Recommended Android Settings

To ensure the app works even when closed:

1. **Allow Auto Start** (depends on manufacturer)
2. **Disable Battery Optimization** for this app
3. **Uncheck “Remove permissions if unused”**
4. **Enable Lock Screen Notifications** in system settings

## How to Test

1. Install the app on a physical Android device (not emulator).
2. Open the app once and select desired JLPT levels (e.g., N5).
3. Turn off the screen using the power button.
4. Turn the screen back on — you’ll see a JLPT word as a notification.


---

Created for effortless, lockscreen-based Japanese learning.
