[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)

# retrofit2-api-result-adapter

retrofit のレスポンスを `InProgress`, `Success`, `Failure` で受け取れるようにするライブラリ。

## 使い方

```build.gradle
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'http://KoutaMatsushita.github.io/retrofit2-api-result-adapter' }
    }
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1"
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "dev.mk2481:retrofit2-api-result-adapter:0.2-SNAPSHOT"
}
```

```kotlin
interface HogeAPI {
    @GET("/")
    fun hoge(): Flow<ApiResult<HogeJSON>>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://example.com")
    .addCallAdapterFactory(ApiResultCallAdapterFactory())
    .addConverterFactory(factory)
    .build()

retrofit.create(HogeAPI::class.java)
    .hoge()
    .collect {
        when(it) {
            InProgress -> {}
            is Success -> { println(it.result) }
            is Failure -> { println(it.error) }
        }
    }
```

詳しい使い方は [サンプル](https://github.com/KoutaMatsushita/retrofit2-api-result-adapter/tree/master/sample) をご覧ください。
