package com.example.android.unscramble.di

import com.example.android.unscramble.ui.game.GameFragment
import dagger.Component
import dagger.Provides
import dagger.Subcomponent

@Subcomponent // 메모:: 이 컴포넌트는 기존에 있는 다른 컴포넌트들에서 하위로 생성되기 때문에 SubComponent
interface GameComponent {

    @Subcomponent.Factory // 메모:: 대거안드로이드 쓰는 경우면 이렇게까지 안해줘도 되는데, 이 컴포넌트를 액티비티에서 어떻게 생성할지 알려줘야함,
    interface Factory {
        fun create(): GameComponent //  메모:: 이 팩토리의 대상이 되는 것은 컴포넌트다 라는걸 알려주는 정도
    }

    fun inject(gameFragment: GameFragment)
}