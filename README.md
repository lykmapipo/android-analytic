android-analytic
=======================

[![](https://jitpack.io/v/lykmapipo/android-analytic.svg)](https://jitpack.io/#lykmapipo/android-analytic)

Simplified wrapper for Firebase Analytics to provide utility helpers

## Installation
Add [https://jitpack.io](https://jitpack.io) to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
add `android-analytic` dependency into your project

```gradle
dependencies {
    implementation 'com.github.lykmapipo:android-analytic:v0.9.1'
}
```

## Usage

Initialize `android-analytic`

```java
public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // initialize {@link Analytic} internals
        Analytic.of(new Provider() {
            @NonNull
            @Override
            public Context getApplicationContext() {
                return SampleApp.this;
            }

            @NonNull
            @Override
            public Boolean isDebug() {
                return BuildConfig.DEBUG;
            }
        });

    }
}
```

In your `android components` start tracking and log events

```js
Analytic.App.opened();
Analytic.App.signedIn("google");
Analytic.App.loggedIn("google");
Analytic.App.share("sms");


Analytic.Tutorial.begin();
Analytic.Tutorial.complete();


Analytic.View.item("Hello", "Song");
Analytic.View.item("Get Real", "Book");


```

## Test
```sh
./gradlew test
```

## Contribute
It will be nice, if you open an issue first so that we can know what is going on, then, fork this repo and push in your ideas.
Do not forget to add a bit of test(s) of what value you adding.

## License

(The MIT License)

Copyright (c) lykmapipo && Contributors

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
