package com.example.android.unscramble.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
interface ViewModelBuilder {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
class ViewModelFactory @Inject constructor(
    // 메모:: 기본적으로 이게 자바로 구현이 되었기 때문에 이 차이로 인해서 에러가 생길 수 있다.
    private val providerMap: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>> // 메모:: 뷰모델을 직접 생성할 수 없기 때문에 ViewModelProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var provider: Provider<out ViewModel>? = providerMap[modelClass]
        if (provider == null) {
            // 메모:: 폴백, 근데 지금 모델클래스의 자식형태로 있을 수도 있다.
            for ((key, value) in providerMap) {
                if (modelClass.isAssignableFrom(key)) {
                    provider = value
                    break
                }
            }
        }
        requireNotNull(provider) { "Unknown ViewModel class: $modelClass"}

        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }

}