# retrofit2-api-result-adapter

retrofit のレスポンスを `InProgress`, `Success`, `Failure` で受け取れるようにするライブラリ。

## 使い方

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
