# Simple Image Picker
This is a yet another image picker that dispaly your images side by side by it directory, it's been said that "If you can't find a way already, make one!" this is the one that suit my Usecase.


## Installation
[![](https://jitpack.io/v/shittu33/SimpleImagePicker.svg)](https://jitpack.io/#shittu33/SimpleImagePicker)

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.shittu33:SimpleImagePicker:v1.o'
	}
  
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
       SimpleImagePicker.with(this)
            .start()
```
For more customization

```kotlin
       SimpleImagePicker.with(this)
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

