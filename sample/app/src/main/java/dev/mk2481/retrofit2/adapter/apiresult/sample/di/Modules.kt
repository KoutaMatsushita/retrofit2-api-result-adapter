package dev.mk2481.retrofit2.adapter.apiresult.sample.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.mk2481.retrofit2.adapter.apiresult.ApiResultCallAdapterFactory
import dev.mk2481.retrofit2.adapter.apiresult.sample.MainViewModel
import dev.mk2481.retrofit2.adapter.apiresult.sample.domain.StatusRepository
import dev.mk2481.retrofit2.adapter.apiresult.sample.repository.StatusAPI
import dev.mk2481.retrofit2.adapter.apiresult.sample.repository.StatusRepositoryImpl
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Modules {
    val appModule = module {
        single<Retrofit> {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            Retrofit.Builder()
                .baseUrl("https://httpstat.us")
                .addCallAdapterFactory(ApiResultCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }
        single<StatusAPI> { get<Retrofit>().create(StatusAPI::class.java) }
        single<StatusRepository> { StatusRepositoryImpl(get()) }

        viewModel { MainViewModel(get()) }
    }
}