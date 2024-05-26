# wf_companion_app
![image](https://github.com/bredlix/wf_companion_app/assets/91148776/c0fe5aa3-950f-4fcf-ab6b-a9218d8a0a19)

The main purpose of this app is to open your watch face’s Google Play Store page directly on the connected Wear OS smartwatch with a simple click.

Additional features:
- Opens your google developer's page to showcase other apps
- Directs users to review section of the watch face to rate it

Bonus: support of light/dark themes

### **You can freely use it to build and distribute your own companion apps.**

NOTE:
- companion app's package should be the same as the watch face package
- all the steps bellow suppose the use of `Project` project view
- most of the changes are supposed to be done in the `scr > main` folder (app module)
- the build can be tested on the device either before step.2 or after step.4. simply `File > Sync Project with Gradle Files`
- everything is done in Android Studio v2023.2.1.25

## 

### 1a. Clone the project directly in Android Studio

Open Android Studio and select `Get from Version Control`. Enter the URL of this GitHub repository and click `Clone`. The project will be downloaded and opened automatically in Android Studio.

### OR

### 1b. Download zip and open with Android Studio locally

On the repository page click `Code > Download ZIP`. Extract content to the preferred folder. Open Android Studio and select `Open an existing Android Studio project`. Navigate to the directory where you extracted the project and open it.

### 2. Update the Gradle build file

Open the `build.gradle.kts` file in the `app` directory.
- Find the `android` block and change the `namespace` to your new package name.
- Find the `defaultConfig` block and change the `applicationId` to your new package name.

### 3. Change the Package Name
![image](https://github.com/bredlix/wf_companion_app/assets/91148776/f5a43c6e-dde7-4855-8e6f-fff31d33c8e9)![Снимок экрана 2024-04-27 234356](https://github.com/bredlix/wf_companion_app/assets/91148776/a559450c-331a-4e5d-8d5f-f0326fe81985)


In the Project pane, go to the `Options (3 dots) > Tree appearance > Compact Middle Packages` and uncheck/de-select it. Your package directory will now be broken up into individual directories (`com > android > wf_companion_app >...`).
Individually select each directory(`android`, `wf_companion_app`) and:

- Right-click it
- Select `Refactor`
- Click on `Rename`
- In the Pop-up dialog, click on `All Directories`
- Enter the new name and hit `Refactor`
- Allow a minute to let Android Studio update all changes.

Eventually you should have `com.your_comany.your_app_name` and the corresponding folder structure `main > java > com > your_comany > your_app_name`.

### 4. Update AndroidManifest.xml

Open the `AndroidManifest.xml` file. Find the `<activity>` tag and change the `android:name` attribute to `com.your_comany.your_app_name.MainActivity`.

### 5. Change app icon

Right-click the res folder and go to `New > Image Asset`. On the `Foreground Layer` tab, select the `Asset Type` to `Image` and browse to the new icon you want.
On the `Background Layer` tab, select the `Asset Type` to `Color` and pick a color of your choice. Click `Next > Finish` and the icon should be changed.

### 6. Change watchface image

Go to `res > drawable` to find the `wf_img.png`. You can replace an existing image by right-clicking it and selecting `Reveal in Finder` (or `Show in Explorer` for Windows), then replace the file there with your new image (it should have the same name and format).
If you want to add a new image, simply copy and paste your image file into the `res/drawable` directory. You can then use this image in your project by referencing it as `@drawable/your_image_name`.

### 7. Change app display name

Go to `res > values` to find `strings.xml`. Change `name="app_name"` entry to your desired app name.

### 8. Change google play developer ID

Go to `res > values` to find `strings.xml`. Change `name="dev_id"` entry to your google play developer ID (to open your dev page when user clicks 'more apps' button).

### 9. Change Privacy policy URL

Go to `res > values` to find `strings.xml`. Change `name="policy_url"` entry to your Privacy Policy URL.

### 10. Sync the project with gradle

After you've made these changes, be sure to sync your project with Gradle files. You can do this by clicking on `File > Sync Project with Gradle Files`.

### 11. Test the build

Run your project on the connected or emulated device to see the changes take effect.

### 12. Build apk/aab for upload

Click `Build > Generate signed Bundle/APK...`. Sign with the same keystore & alias used for the watch face build.
