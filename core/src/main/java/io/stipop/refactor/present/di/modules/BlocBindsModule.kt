package io.stipop.refactor.present.di.modules

import dagger.Binds
import dagger.Module
import io.stipop.refactor.data.blocs.PackageItemDetailBloc
import io.stipop.refactor.data.blocs.PackageItemDetailBlocV1
import io.stipop.refactor.data.blocs.StickerKeyboardBlocV1
import io.stipop.refactor.domain.blocs.StickerKeyboardBloc

@Module(
    includes = [
        RepositoryBindsModule::class
    ]
)
interface BlocBindsModule {

    @Binds
    fun bindPackageItemDetailBloc(bloc: PackageItemDetailBlocV1): PackageItemDetailBloc

    @Binds
    fun bindStickerKeyboardBloc(bloc: StickerKeyboardBlocV1): StickerKeyboardBloc
}
