# Simple Image Picker
This is a yet another image picker that dispaly your images side by side by it directory, it's been said that "If you can't find a way already, make one!" this is the one that suit my Usecase.

![alt text](https://github.com/shittu33/SimpleImagePicker/blob/master/picker_1.gif?raw=true)![alt text](https://github.com/shittu33/SimpleImagePicker/blob/master/picker_2gif.gif?raw=true)![alt text](https://github.com/shittu33/SimpleImagePicker/blob/master/picker3-gif.gif?raw=true)

## Installation [![](https://jitpack.io/v/shittu33/SimpleImagePicker.svg)](https://jitpack.io/#shittu33/SimpleImagePicker)

Add this to your project's build.gradle
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

And add this to your module's build.gradle

```
dependencies {
	implementation 'com.github.shittu33:SimpleImagePicker:v1.o'
}
  ```
You have to migrate your project to support AndroidX by add following lines on gradle.properties file:
```Json
android.useAndroidX=true
android.enableJetifier=true
```
Add android:requestLegacyExternalStorage="true" attribute to the application tag in the AndroidManifest.xml file:
```xml
<application
    ...
    android:requestLegacyExternalStorage="true">

    ...

</application>
```

## Usage

#### Start Picker
This should get you started
```kotlin
       SimpleImagePicker.with(activity)
            .start()
```
For more customization

```kotlin
       SimpleImagePicker.with(activity)
            .setMultipleSelection(true)
            .setTheme(PickerTheme.dark)
            .setRequestCode(MY_PICKER_REQUEST_CODE)
            .setResultCodeSuccess(CUSTOM_RESULT_CODE_SUCCESS)
            .setResultCodeCancel(CUSTOM_RESULT_CODE_CANCEL)
            .setResultDataKey(CUSTOM_DATA_KEY)
            .start()
```

#### Listen For result
```Kotlin

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_CODE_SUCCESS) {
                val pics = data?.getStringArrayListExtra(RESULT_DATA_KEY)
                if (pics != null) {
                    galleryAdapter!!.submitList(pics)
                }
            } else if (resultCode == RESULT_CODE_CANCEL) {
                Toast.makeText(this, "nothing selected!", LENGTH_SHORT).show()
            }
        }
    }
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

```
 Copyright 2020 Abdulmujeeb Olawale

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
