package com.manoj.base.injection

/*import com.seasia.mqttbase.data.local.MachineDao
import com.seasia.mqttbase.data.local.MachineRepository*/
import com.manoj.base.domain.repositary.BaseRepoImpl
import com.manoj.base.domain.repositary.BaseRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositaryModule {

    @Singleton
    @Provides
    fun providesPostRepositary(postRepositaryImp: BaseRepoImpl): BaseRepo =
        postRepositaryImp
}