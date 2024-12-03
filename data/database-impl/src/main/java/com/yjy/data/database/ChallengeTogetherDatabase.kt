package com.yjy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.dao.ChallengePostDao
import com.yjy.data.database.dao.NotificationDao
import com.yjy.data.database.dao.TogetherChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.database.model.ChallengePostEntity
import com.yjy.data.database.model.NotificationEntity
import com.yjy.data.database.model.TogetherChallengeEntity
import com.yjy.data.database.util.Converters

@Database(
    entities = [
        ChallengeEntity::class,
        ChallengePostEntity::class,
        TogetherChallengeEntity::class,
        NotificationEntity::class,
    ],
    version = 1,
)
@TypeConverters(Converters::class)
internal abstract class ChallengeTogetherDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
    abstract fun challengePostDao(): ChallengePostDao
    abstract fun togetherChallengeDao(): TogetherChallengeDao
    abstract fun notificationDao(): NotificationDao
}
